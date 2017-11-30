package org.isisaddons.module.base.platform.fixturesupport;

import java.util.List;

import com.google.common.collect.Lists;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;
import org.apache.isis.applib.fixturescripts.EnumWithBuilderScript;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScriptWithExecutionStrategy;
import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import lombok.Getter;

public class DataEnumPersist<E extends EnumWithBuilderScript<T,F>, T, F extends BuilderScriptAbstract<T,F>>
        extends FixtureScript
        implements FixtureScriptWithExecutionStrategy {

    private final Class<E> dataEnumClass;

    public DataEnumPersist(final Class<E> demoDataClass) {
        this.dataEnumClass = demoDataClass;
    }

    /**
     * The number of objects to create.
     */
    @Getter
    private Integer number;
    public void setNumber(final Integer number) {
        this.number = number;
    }

    /**
     * The objects created by this fixture (output).
     */
    @Getter
    private final List<T> objects = Lists.newArrayList();


    @Override
    protected void execute(final FixtureScript.ExecutionContext ec) {

        final E[] enumConstants = dataEnumClass.getEnumConstants();
        final int max = enumConstants.length;

        // defaults
        final int number = defaultParam("number", ec, max);

        // validate
        if(number < 0 || number > max) {
            throw new IllegalArgumentException(String.format("number must be in range [0,%d)", max));
        }

        for (int i = 0; i < number; i++) {
            final F enumFixture = enumConstants[i].toFixtureScript();
            final T domainObject = ec.executeChildT(this, enumFixture).getObject();
            ec.addResult(this, domainObject);
            objects.add(domainObject);
        }
    }

    @Override
    public FixtureScripts.MultipleExecutionStrategy getMultipleExecutionStrategy() {
        return FixtureScripts.MultipleExecutionStrategy.EXECUTE_ONCE_BY_VALUE;
    }

    @javax.inject.Inject
    ServiceRegistry2 serviceRegistry;

}

