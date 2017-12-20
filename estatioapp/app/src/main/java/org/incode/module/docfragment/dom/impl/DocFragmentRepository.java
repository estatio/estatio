package org.incode.module.docfragment.dom.impl;

import java.util.Collections;
import java.util.List;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = DocFragment.class
)
public class DocFragmentRepository {

    public List<DocFragment> listAll() {
        return repositoryService.allInstances(DocFragment.class);
    }

    /**
     * Returns the most applicable {@link DocFragment} by atPath
     *
     * <p>
     * for example, will match "/ITA/CAR" precedence over "/ITA" precedence over "/".
     * </p>
     *
     * @param objectType - the (as per {@link DomainObject#objectType() objectType} of the object to be used to interpolate
     * @param name
     * @param atPath
     */
    @Programmatic
    public DocFragment findByObjectTypeAndNameAndApplicableToAtPath(
            final String objectType,
            final String name,
            final String atPath) {

        // workaround, the ORDER BY atPath DESC doesn't seem to be honoured, don't know why...
        // we therefore do a client-side sort
        final List<DocFragment> ts = repositoryService.allMatches(
                new QueryDefault<>(
                        DocFragment.class,
                        "findByObjectTypeAndNameAndApplicableToAtPath",
                        "objectType", objectType,
                        "name", name,
                        "atPath", atPath));
        Collections.sort(ts, (o1, o2) -> o2.getAtPath().length() - o1.getAtPath().length());

        return ts.isEmpty() ? null : ts.get(0);
    }

    /**
     * Returns the most applicable {@link DocFragment} by atPath
     *
     * <p>
     * for example, will match "/ITA/CAR" precedence over "/ITA" precedence over "/".
     * </p>
     *
     * @param objectType - the (as per {@link DomainObject#objectType() objectType} of the object to be used to interpolate
     * @param name
     * @param atPath
     */
    @Programmatic
    public DocFragment findByObjectTypeAndNameAndAtPath(
            final String objectType,
            final String name,
            final String atPath) {

        return repositoryService.firstMatch(
                new QueryDefault<>(
                        DocFragment.class,
                        "findByObjectTypeAndNameAndAtPath",
                        "objectType", objectType,
                        "name", name,
                        "atPath", atPath));
    }

    @Programmatic
    public DocFragment create(final String objectType, final String name, final String atPath, final String templateText) {
        final DocFragment object = new DocFragment(objectType, name, atPath, templateText);
        serviceRegistry.injectServicesInto(object);
        repositoryService.persist(object);
        return object;
    }

    @javax.inject.Inject
    RepositoryService repositoryService;
    @javax.inject.Inject
    ServiceRegistry2 serviceRegistry;

}
