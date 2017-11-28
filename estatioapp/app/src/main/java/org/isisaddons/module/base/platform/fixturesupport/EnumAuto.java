package org.isisaddons.module.base.platform.fixturesupport;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

/**
 * have moved this to isisaddons only so that S101 gives us a nice picture.
 * Eventually gonna move to org.apache.isis.core.fixturesupport
 */
public interface EnumAuto<T>
        extends EnumWithUpsert<T>, EnumWithFixtureScript<T,FixtureScript> {

    T asDomainObject(final ServiceRegistry2 serviceRegistry2);
    @Override
    default T upsertUsing(final ServiceRegistry2 serviceRegistry) {
        return Util.upsert(this, serviceRegistry);
    }
    @Override
    default T findUsing(final ServiceRegistry2 serviceRegistry) {
        return Util.firstMatch(this, serviceRegistry);
    }
    @Override
    default FixtureScript toFixtureScript() {
        return new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                final T t = upsertUsing(serviceRegistry);
                executionContext.addResult(this, t);
            }
        };
    }

    class Util {

        private Util(){}

        public static <T> T upsert(
                final EnumAuto<T> data,
                final ServiceRegistry2 serviceRegistry2) {
            T domainObject = data.findUsing(serviceRegistry2);
            if(domainObject != null) {
                return domainObject;
            }
            final RepositoryService repositoryService = serviceRegistry2.lookupService(RepositoryService.class);
            domainObject = data.asDomainObject(serviceRegistry2);
            repositoryService.persist(domainObject);
            return domainObject;
        }

        public static <T> T uniqueMatch(
                final EnumAuto<T> data,
                final ServiceRegistry2 serviceRegistry2) {
            final RepositoryService repositoryService = serviceRegistry2.lookupService(RepositoryService.class);
            final T domainObject = data.asDomainObject(serviceRegistry2);
            final Class<T> domainClass = domainClassOf(data);
            return repositoryService.uniqueMatch(domainClass, x -> Objects.equals(x, domainObject));
        }

        public static <T> T firstMatch(
                final EnumAuto<T> data,
                final ServiceRegistry2 serviceRegistry2) {
            final RepositoryService repositoryService = serviceRegistry2.lookupService(RepositoryService.class);
            final T domainObject = data.asDomainObject(serviceRegistry2);
            final Class<T> domainClass = domainClassOf(data);
            return repositoryService.firstMatch(domainClass, x -> Objects.equals(x, domainObject));
        }

        private static <T> Class<T> domainClassOf(final EnumAuto<T> data) {
            return genericType(data, 0, "domainClass");
        }

        private static <T> Class<T> genericType(
                final EnumAuto<T> data,
                final int i,
                final String genericTypeName) {
            final Class<? extends EnumAuto> aClass = data.getClass();
            final Type[] genericInterfaces = aClass.getGenericInterfaces();
            for (Type genericInterface : genericInterfaces) {
                final String typeName = genericInterface.getTypeName();
                if(typeName.startsWith(EnumAuto.class.getName() + "<")) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                    final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    return (Class) actualTypeArguments[i];
                }
            }
            throw new RuntimeException(String.format(
                    "Could not determine %s generic type of %s", genericTypeName, aClass.getName()));
        }

    }
}

