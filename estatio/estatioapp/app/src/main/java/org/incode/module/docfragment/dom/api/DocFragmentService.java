package org.incode.module.docfragment.dom.api;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.FluentIterable;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.metamodel.MetaModelService3;

import org.incode.module.docfragment.dom.impl.DocFragment;
import org.incode.module.docfragment.dom.impl.DocFragmentRepository;
import org.incode.module.docfragment.dom.spi.ApplicationTenancyService;

import freemarker.template.TemplateException;

@DomainService(nature = NatureOfService.DOMAIN)
public class DocFragmentService {

    public static class RenderException extends ApplicationException {

        private static final long serialVersionUID = 1L;

        public RenderException(String format, Object... args) {
            super(String.format(format, args));
        }
    }

    /**
     * @param domainObject used to determine the {@link ApplicationTenancyService#atPathFor(Object) atPath} of the {@link DocFragment} to use to render, and also provides the state for the interpolation into the fragment's {@link DocFragment#getTemplateText() template text}
     * @param name corresponds to the {@link DocFragment#getName() name} of the {@link DocFragment} to use to render.
     *
     * @return the rendered text
     *
     * @throws IOException
     * @throws TemplateException
     * @throws RenderException - if could not locate any {@link DocFragment}.
     */
    @Programmatic
    public String render(
                final Object domainObject,
                final String name)
            throws IOException, TemplateException, RenderException {

        final String atPath = atPathFor(domainObject);
        return render(domainObject, name, atPath);
    }

    public String atPathFor(final Object domainObject) {
        for (ApplicationTenancyService applicationTenancyService : applicationTenancyServices) {
            final String atPathFor = applicationTenancyService.atPathFor(domainObject);
            if(atPathFor != null) {
                return atPathFor;
            }
        }
        return null;
    }

    /**
     * Overload of {@link #render(Object, String)}, but allowing the atPath to be specified explicitly rather than inferred from the supplied domain object.
     *
     * @param domainObject provides the state for the interpolation into the fragment's {@link DocFragment#getTemplateText() template text}
     * @param name corresponds to the {@link DocFragment#getName() name} of the {@link DocFragment} to use to render.
     * @param atPath corrsponds to the {@link ApplicationTenancyService#atPathFor(Object) atPath} of the {@link DocFragment} to use to render
     *
     * @throws IOException
     * @throws TemplateException
     * @throws RenderException - if could not locate any {@link DocFragment}.
     */
    @Programmatic
    public String render(
                final Object domainObject,
                final String name,
                final String atPath)
            throws IOException, TemplateException, RenderException {
        final String objectType = objectTypeFor(domainObject);

        final DocFragment fragment = repo.findByObjectTypeAndNameAndApplicableToAtPath(objectType, name, atPath);

        if (fragment != null)
            return fragment.render(domainObject);
        else
            throw new RenderException(
                    "No fragment found for objectType: %s, name: %s, atPath: %s",
                    objectType, name, atPath);
    }

    private String objectTypeFor(final Object domainObject) {
        return metaModelService3.toObjectType(domainObject.getClass());
    }

    @Inject
    DocFragmentRepository repo;
    @Inject
    MetaModelService3 metaModelService3;
    @Inject
    List<ApplicationTenancyService> applicationTenancyServices;

}