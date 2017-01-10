package org.estatio.dom.apptenancy;

import javax.inject.Inject;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.services.wrapper.WrapperFactory;
import org.estatio.dom.UdoDomainObject2;

public class ApplicationTenancySubscriberAlgorithm<S, E extends ApplicationTenancyEventChanged> {

    private final Class<S> sourceClass;
    private final Class<E> eventClass;

    public ApplicationTenancySubscriberAlgorithm(final Class<S> sourceClass, final Class<E> eventClass) {
        this.sourceClass = sourceClass;
        this.eventClass = eventClass;
    }

    public Class<? extends S> getSourceClass() {
        return sourceClass;
    }

    public Class<E> getEventClass() {
        return eventClass;
    }


    public void hide(final E ev, final S source) {
    }
    public void disable(final E ev, final S source) {
    }
    public void validate(final E ev, final S source) {
    }
    public void executing(final E ev, final S source) {
    }
    public void executed(final E ev, final S source) {
    }

    /**
     * Convenience for subclasses.
     */
    protected void sync(final UdoDomainObject2<?> source, final Iterable<? extends UdoDomainObject2<?>> targetList) {
        for (UdoDomainObject2<?> target : targetList) {
            if (target instanceof WithApplicationTenancyPathPersisted) {
                final WithApplicationTenancyPathPersisted withApplicationTenancyPathPersisted = (WithApplicationTenancyPathPersisted) target;
                withApplicationTenancyPathPersisted.setApplicationTenancyPath(source.getApplicationTenancy().getPath());
            }
        }
    }

    protected S wrap(final S domainObject) {
        return wrapperFactory.wrap(domainObject);
    }
    protected S wrapNoExecute(S domainObject) {
        return wrapperFactory.wrapNoExecute(domainObject);
    }
    protected S wrap(S domainObject, final WrapperFactory.ExecutionMode mode) {
        return wrapperFactory.wrap(domainObject, mode);
    }

    @Inject
    private WrapperFactory wrapperFactory;

    // //////////////////////////////////////

    public static class OnChanged<S> extends ApplicationTenancySubscriberAlgorithm<S, ApplicationTenancyEventChanged> {
        public OnChanged(final Class<S> sourceClass) {
            super(sourceClass, ApplicationTenancyEventChanged.class);
        }

        @Inject
        DomainObjectContainer container;

        public static class Hide<S> extends OnChanged<S> {

            public Hide(final Class<S> sourceClass) {
                super(sourceClass);
            }

            @Override
            public void hide(final ApplicationTenancyEventChanged ev, final S source) {
                ev.hide();
            }
        }


        public static class Disable<S> extends OnChanged<S> {
            private final String reason;

            public Disable(final Class<S> sourceClass, final String reason) {
                super(sourceClass);
                this.reason = reason;
            }

            @Override
            public void disable(final ApplicationTenancyEventChanged ev, final S source) {
                ev.disable(reason);
            }

            public static class BecauseGlobalData<S> extends OnChanged.Disable<S> {

                public BecauseGlobalData(final Class<S> sourceClass) {
                    super(sourceClass, "Global data cannot be changed");
                }
            }
        }

        public static class Invalid<S> extends OnChanged<S> {
            private final String reason;

            public Invalid(final Class<S> sourceClass, final String reason) {
                super(sourceClass);
                this.reason = reason;
            }

            @Override
            public void validate(final ApplicationTenancyEventChanged ev, final S source) {
                ev.invalidate(reason);
            }
        }

        public static class CascadeUpdate<S> extends OnChanged<S> {
            private final AccessMany<S> accessMany;

            public CascadeUpdate(final Class<S> sourceClass, final AccessMany<S> accessMany) {
                super(sourceClass);
                this.accessMany = accessMany;
            }

            @Override
            public void executed(final ApplicationTenancyEventChanged ev, final S source) {
                container.injectServicesInto(accessMany);
                sync((UdoDomainObject2<?>)source, accessMany.get(source));
            }
        }
    }

    public static class OnMovedDown<S> extends ApplicationTenancySubscriberAlgorithm<S, ApplicationTenancyEventMovedDown> {
        public OnMovedDown(final Class<S> sourceClass) {
            super(sourceClass, ApplicationTenancyEventMovedDown.class);
        }

        @Inject
        DomainObjectContainer container;

        public static class Hide<S> extends OnMovedDown<S> {

            public Hide(final Class<S> sourceClass) {
                super(sourceClass);
            }

            @Override
            public void hide(final ApplicationTenancyEventMovedDown ev, final S source) {
                ev.hide();
            }
        }

        public static class Disable<S> extends OnMovedDown<S> {
            private final String reason;

            public Disable(final Class<S> sourceClass, final String reason) {
                super(sourceClass);
                this.reason = reason;
            }

            @Override
            public void disable(final ApplicationTenancyEventMovedDown ev, final S source) {
                ev.disable(reason);
            }
        }

        public static class Invalid<S> extends OnMovedDown<S> {
            private final String reason;

            public Invalid(final Class<S> sourceClass, final String reason) {
                super(sourceClass);
                this.reason = reason;
            }

            @Override
            public void validate(final ApplicationTenancyEventMovedDown ev, final S source) {
                ev.invalidate(reason);
            }

        }

        public static class InvalidIfRelatedBecomesPeer<S> extends OnMovedDown<S> {

            private final String relatedRoleName;
            private final AccessOne<S> relatedAccessOne;
            private final AccessMany<S> relatedAccessMany;

            public InvalidIfRelatedBecomesPeer(final Class<S> sourceClass, final String relatedRoleName, final AccessOne<S> relatedAccessor) {
                super(sourceClass);
                this.relatedRoleName = relatedRoleName;
                this.relatedAccessOne = relatedAccessor;
                this.relatedAccessMany = null;
            }

            public InvalidIfRelatedBecomesPeer(final Class<S> sourceClass, final String relatedRoleName, final AccessMany<S> relatedAccessor) {
                super(sourceClass);
                this.relatedRoleName = relatedRoleName;
                this.relatedAccessOne = null;
                this.relatedAccessMany = relatedAccessor;
            }

            @Override
            public void validate(final ApplicationTenancyEventMovedDown ev, final S source) {
                final ApplicationTenancy proposed = (ApplicationTenancy) ev.getArguments().get(0);
                final ApplicationTenancyLevel proposedLevel = ApplicationTenancyLevel.of(proposed);
                if(relatedAccessOne != null) {
                    container.injectServicesInto(relatedAccessOne);

                    final ApplicationTenancyLevel relatedLevel = ApplicationTenancyLevel.of(relatedAccessOne.get(source));

                    if(proposedLevel.peerOf(relatedLevel)) {
                        ev.veto(relatedRoleName + " is at a peer level '%s'.", relatedLevel);
                        return;
                    }
                } else {
                    container.injectServicesInto(relatedAccessMany);

                    for (final UdoDomainObject2<?> udoDomainObject2 : relatedAccessMany.get(source)) {
                        final ApplicationTenancyLevel relatedLevel = ApplicationTenancyLevel.of(udoDomainObject2);

                        if(proposedLevel.peerOf(relatedLevel)) {
                            ev.veto(relatedRoleName + " is at a peer level '%s'.", relatedLevel);
                            return;
                        }
                    }
                }
            }
        }
    }

    public static class OnMovedUp<S extends UdoDomainObject2<S>> extends ApplicationTenancySubscriberAlgorithm<S, ApplicationTenancyEventMovedUp> {
        public OnMovedUp(final Class<S> sourceClass) {
            super(sourceClass, ApplicationTenancyEventMovedUp.class);
        }

        @Inject
        DomainObjectContainer container;

        public static class Hide<S extends UdoDomainObject2<S>> extends OnMovedUp<S> {

            public Hide(final Class<S> sourceClass) {
                super(sourceClass);
            }

            @Override
            public void hide(final ApplicationTenancyEventMovedUp ev, final S source) {
                ev.hide();
            }
        }

        public static class Disable<S extends UdoDomainObject2<S>> extends OnMovedUp<S> {
            private final String reason;

            public Disable(final Class<S> sourceClass, final String reason) {
                super(sourceClass);
                this.reason = reason;
            }

            @Override
            public void disable(final ApplicationTenancyEventMovedUp ev, final S source) {
                ev.disable(reason);
            }
        }

        public static class Invalid<S extends UdoDomainObject2<S>> extends OnMovedUp<S> {
            private final String reason;

            public Invalid(final Class<S> sourceClass, final String reason) {
                super(sourceClass);
                this.reason = reason;
            }

            @Override
            public void validate(final ApplicationTenancyEventMovedUp ev, final S source) {
                ev.invalidate(reason);
            }
        }
    }
}
