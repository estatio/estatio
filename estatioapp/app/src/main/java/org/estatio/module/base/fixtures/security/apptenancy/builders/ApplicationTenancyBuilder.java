package org.estatio.module.base.fixtures.security.apptenancy.builders;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"path"}, callSuper = false)
@Accessors(chain = true)
public class ApplicationTenancyBuilder extends BuilderScriptAbstract<ApplicationTenancy, ApplicationTenancyBuilder> {

    @Getter @Setter
    String path;
    @Getter @Setter
    String name;
    @Getter @Setter
    String pathOfParent;

    @Getter
    private ApplicationTenancy object;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        checkParam("path", executionContext, String.class);
        checkParam("name", executionContext, String.class);

        ApplicationTenancy applicationTenancy = repository.findByPath(path);
        if(applicationTenancy == null) {
            final ApplicationTenancy parent =
                    path.length() > 1
                            ? repository.findByPath(pathOfParent)
                            : null;
            applicationTenancy = repository.newTenancy(name, path, parent);
        }

        executionContext.addResult(this, path, applicationTenancy);

        object = applicationTenancy;
    }

    @Inject
    ApplicationTenancyRepository repository;
}
