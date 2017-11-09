package org.estatio.module.base.platform.fixturesupport;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

public interface DemoData2<D extends DemoData2<D, T>, T> {

    T asDomainObject(final ServiceRegistry2 serviceRegistry2);
    default T upsertUsing(final ServiceRegistry2 serviceRegistry2) {
        return Util.upsert(this, serviceRegistry2);
    }
    default T findUsing(final ServiceRegistry2 serviceRegistry) {
        return Util.firstMatch(this, serviceRegistry);
    }
    default Class<T> toDomainClass() {
        final Type[] genericInterfaces = this.getClass().getGenericInterfaces();
        final ParameterizedType genericSuperclass = (ParameterizedType) genericInterfaces[0];
        final Type[] actualTypeArguments = genericSuperclass.getActualTypeArguments();
        final Type type = actualTypeArguments[1];
        return (Class<T>) type;
    }

    class Util {

        private Util(){}

        public static <D extends DemoData2<D, T>, T> T upsert(
                final DemoData2<D, T> data,
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

        public static <D extends DemoData2<D, T>, T> T uniqueMatch(
                final DemoData2<D, T> data,
                final ServiceRegistry2 serviceRegistry2) {
            final RepositoryService repositoryService = serviceRegistry2.lookupService(RepositoryService.class);
            final T domainObject = data.asDomainObject(serviceRegistry2);
            final Class<T> domainClass = domainClassOf(data);
            return repositoryService.uniqueMatch(domainClass, x -> Objects.equals(x, domainObject));
        }

        public static <D extends DemoData2<D, T>, T> T firstMatch(
                final DemoData2<D, T> data,
                final ServiceRegistry2 serviceRegistry2) {
            final RepositoryService repositoryService = serviceRegistry2.lookupService(RepositoryService.class);
            final T domainObject = data.asDomainObject(serviceRegistry2);
            final Class<T> domainClass = domainClassOf(data);
            return repositoryService.firstMatch(domainClass, x -> Objects.equals(x, domainObject));
        }


        public static <D extends DemoData2<D, T>, T> Class<T> demoDataClassOf(final DemoData2<D, T> data) {
            return genericType(data, 0, "demoDataClass");
        }

        public static <D extends DemoData2<D, T>, T> Class<T> domainClassOf(final DemoData2<D, T> data) {
            return genericType(data, 1, "domainClass");
        }

        private static <D extends DemoData2<D, T>, T> Class<T> genericType(
                final DemoData2<D, T> data,
                final int i,
                final String genericTypeName) {
            final Class<? extends DemoData2> aClass = data.getClass();
            final Type[] genericInterfaces = aClass.getGenericInterfaces();
            for (Type genericInterface : genericInterfaces) {
                final String typeName = genericInterface.getTypeName();
                if(typeName.startsWith(DemoData2.class.getName() + "<")) {
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

