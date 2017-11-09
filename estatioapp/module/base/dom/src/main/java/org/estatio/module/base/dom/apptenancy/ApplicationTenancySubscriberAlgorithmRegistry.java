package org.estatio.module.base.dom.apptenancy;

import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.estatio.module.base.dom.UdoDomainObject2;

/**
 * The original intent of this registry was to centralize mechanisms (algorithms) by which application tenancy paths of different
 * entities be kept in sync, or their modification vetoed.   Conceptually: cascade update/delete, or referential
 * integrity restrictions.
 *
 * <p>
 *     In the event, this registry is not used; no such because we have decided in the end <i>not</i> to have an
 *     application tenancy path on every entity; instead only certain &quot;root&quot; entities hold the path, and they
 *     each apply their own logic to ensure that they don't get out of sync.
 * </p>
 *
 * <p>
 *     I've decided to keep this code around in case we need to re-introduce it in some manner or means; it's also a good
 *     demonstrator of the possible power of subscribing to domain events.
 * </p>
 *
 * <p>Examples showing original intended usage (nb: none of these are actually installed)</p>
 *
 * <p>Veto:</p>
 * <pre>
 * add(new ApplicationTenancySubscriberAlgorithm.OnChanged.Hide&lt;&gt;(FixedAssetFinancialAccount.class));
 *
 * add(new ApplicationTenancySubscriberAlgorithm.OnChanged.Disable.BecauseGlobalData&lt;&gt;(AgreementType.class));
 *
 * add(new ApplicationTenancySubscriberAlgorithm.OnChanged.Disable&lt;&gt;(AgreementRole.class, &quot;Use owning Agreement to change.&quot;));
 *
 * </pre>
 *
 * <p>Cascade update:</p>
 * <pre>
 * add(new ApplicationTenancySubscriberAlgorithm.OnChanged&lt;Agreement&gt;(Agreement.class) {
 *       public void executed(final ApplicationTenancyEventChanged ev, final Agreement source) {
 *         sync(source, agreementRoleRepository.findByAgreement(source));
 *         sync(source, agreementRoleCommunicationChannels.findByAgreement(source));
 *       }
 *       private AgreementRoles agreementRoleRepository;
 *       private AgreementRoleCommunicationChannels agreementRoleCommunicationChannels;
 *    });
 *
 * add(new ApplicationTenancySubscriberAlgorithm.OnChanged.CascadeUpdate&lt;&gt;(FixedAsset.class, new AccessMany&lt;FixedAsset&gt;() {
 *       public Iterable&lt;? extends UdoDomainObject2&lt;?&gt;&gt; get(final FixedAsset source) {
 *        return fixedAssetRoles.findByAsset(source);
 *       }
 *       private FixedAssetRoles fixedAssetRoles;
 *     }));
 *
 * </pre>
 *
 * <p>Restrict update:</p>
 * <pre>
 * add(new ApplicationTenancySubscriberAlgorithm.OnMovedDown.InvalidIfRelatedBecomesPeer&lt;&gt;(Agreement.class, &quot;Primary party&quot;,
 *     new AccessOne&lt;Agreement&gt;() {
 *         public UdoDomainObject2&lt;?&gt; get(final Agreement source) {
 *           return source.getPrimaryParty();
 *         }
 *       }));
 * </pre>
 */
class ApplicationTenancySubscriberAlgorithmRegistry {

    private final Map<Class<?>,Map<Class<? extends ApplicationTenancyEventChanged>,List<ApplicationTenancySubscriberAlgorithm>>> algorithmListByEventClassBySourceClass = Maps.newHashMap();
    /**
     * lazily cached
     */
    private final Map<Class<?>, Map<Class<? extends ApplicationTenancyEventChanged>,List<ApplicationTenancySubscriberAlgorithm>>> flattenedAlgorithmListByEventClassBySourceClass = Maps.newHashMap();


    void addAlgorithms() {

    }


    private <S> void add(ApplicationTenancySubscriberAlgorithm<S, ?> algorithm) {
        final Class<? extends S> sourceClass = algorithm.getSourceClass();
        final Class<? extends ApplicationTenancyEventChanged> eventClass = algorithm.getEventClass();

        Map<Class<? extends ApplicationTenancyEventChanged>, List<ApplicationTenancySubscriberAlgorithm>> algorithmListByEventClass = algorithmListByEventClassBySourceClass.get(sourceClass);
        if(algorithmListByEventClass == null) {
            algorithmListByEventClass = Maps.newHashMap();
            algorithmListByEventClassBySourceClass.put(sourceClass, algorithmListByEventClass);
        }

        List<ApplicationTenancySubscriberAlgorithm> algorithms = algorithmListByEventClass.get(eventClass);
        if(algorithms == null) {
            algorithms = Lists.newArrayList();
            algorithmListByEventClass.put(eventClass, algorithms);
        }
        algorithms.add(algorithm);
    }

    List<ApplicationTenancySubscriberAlgorithm> lookup(
            final UdoDomainObject2 source,
            final Class<? extends ApplicationTenancyEventChanged> eventClass) {

        final Class<? extends UdoDomainObject2> sourceClass = source.getClass();

        Map<Class<? extends ApplicationTenancyEventChanged>, List<ApplicationTenancySubscriberAlgorithm>> flattenedAlgorithmListByEventClass = flattenedAlgorithmListByEventClassBySourceClass.get(sourceClass);
        if(flattenedAlgorithmListByEventClass == null) {
            flattenedAlgorithmListByEventClass = Maps.newHashMap();
            flattenedAlgorithmListByEventClassBySourceClass.put(sourceClass, flattenedAlgorithmListByEventClass);
        }

        List<ApplicationTenancySubscriberAlgorithm> algorithmList = flattenedAlgorithmListByEventClass.get(sourceClass);
        if(algorithmList == null) {
            algorithmList = Lists.newArrayList();

            final List<Class<?>> types = superTypesOf(source);

            for (final Class<?> eachType : types) {
                final Map<Class<? extends ApplicationTenancyEventChanged>, List<ApplicationTenancySubscriberAlgorithm>> algorithmListByEventClass = algorithmListByEventClassBySourceClass.get(eachType);
                if(algorithmListByEventClass != null) {
                    final List<ApplicationTenancySubscriberAlgorithm> list = algorithmListByEventClass.get(eventClass);
                    if(list != null) {
                        algorithmList.addAll(list);
                    }
                }
            }

            if(!algorithmList.isEmpty()) {
                flattenedAlgorithmListByEventClass.put(eventClass, algorithmList);
            }

        }

        return algorithmList;
    }

    private static List<Class<?>> superTypesOf(final UdoDomainObject2 source) {
        List<Class<?>> types = Lists.newArrayList();
        Class<?> type = source.getClass();
        while(UdoDomainObject2.class.isAssignableFrom(type) && type != UdoDomainObject2.class) {
            types.add(type);
            type = type.getSuperclass();
        }
        return types;
    }

}
