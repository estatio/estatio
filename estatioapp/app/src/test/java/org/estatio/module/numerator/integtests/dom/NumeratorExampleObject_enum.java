package org.estatio.module.numerator.integtests.dom;

import javax.inject.Inject;

import com.google.common.base.Objects;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;
import org.apache.isis.applib.fixturescripts.EnumWithBuilderScript;
import org.apache.isis.applib.fixturescripts.EnumWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum NumeratorExampleObject_enum
        implements EnumWithBuilderScript<NumeratorExampleObject, NumeratorExampleObject_enum.Builder>,
        EnumWithFinder<NumeratorExampleObject> {

    Kal("Kal"),
    Oxf("Oxf");

    private final String name;


    @Override
    public Builder toFixtureScript() {
        return new Builder().setName(name);
    }

    @Override
    public NumeratorExampleObject findUsing(final ServiceRegistry2 serviceRegistry) {
        return serviceRegistry.lookupService(RepositoryService.class)
                .firstMatch(NumeratorExampleObject.class, c -> Objects.equal(c.getName(), name));
    }

    @EqualsAndHashCode(of={"name"}, callSuper = false)
    @ToString(of={"name"})
    @Accessors(chain = true)
    public final static class Builder extends BuilderScriptAbstract<NumeratorExampleObject, NumeratorExampleObject_enum.Builder> {

        @Getter @Setter
        String name;

        @Getter
        NumeratorExampleObject object;

        @Override
        protected void execute(final ExecutionContext executionContext) {
            checkParam("name", executionContext, String.class);
            NumeratorExampleObject numeratorExampleObject =
                    repositoryService.firstMatch(NumeratorExampleObject.class, c -> Objects.equal(c.getName(), name));
            if(numeratorExampleObject == null) {
                numeratorExampleObject = repositoryService.persistAndFlush(new NumeratorExampleObject(name));
            }
            object = numeratorExampleObject;
        }

        @Inject
        RepositoryService repositoryService;
    }

}
