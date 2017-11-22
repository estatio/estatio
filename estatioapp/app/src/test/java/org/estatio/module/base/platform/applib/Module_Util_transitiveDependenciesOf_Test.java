package org.estatio.module.base.platform.applib;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import org.assertj.core.util.Lists;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.isisaddons.module.base.platform.applib.Module;
import org.isisaddons.module.base.platform.applib.ModuleAbstract;

import lombok.AllArgsConstructor;
import static org.assertj.core.api.Assertions.assertThat;

public class Module_Util_transitiveDependenciesOf_Test {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @AllArgsConstructor
    public class ModuleImpl extends ModuleAbstract {
        private final String name;
        @Override public String toString() {
            return name;
        }
    }

    static class ModuleP {}
    static class ModuleQ {}
    static class ModuleR {}

    static class ServiceX {}
    static class ServiceY {}
    static class ServiceZ {}

    final Module moduleF = new ModuleImpl("F");
    final Module moduleE = new ModuleImpl("E") {
        @Override public Set<Class<?>> getAdditionalServices() {
            return Sets.newHashSet(ServiceX.class);
        }
        @Override
        public Set<Class<?>> getDependenciesAsClass() {
            return Sets.newHashSet(ModuleP.class);
        }
    };
    final Module moduleD = new ModuleImpl("D") {
        @Override public Set<Module> getDependencies() {
            return Sets.newHashSet(moduleE);
        }
    };

    final Module moduleC = new ModuleImpl("C") {
        @Override public Set<Module> getDependencies() {
            return Sets.newHashSet(moduleE, moduleD);
        }
        @Override
        public Set<Class<?>> getDependenciesAsClass() {
            return Sets.newHashSet(ModuleQ.class, ModuleR.class);
        }
        @Override
        public Set<Class<?>> getAdditionalServices() {
            return Sets.newHashSet(ServiceY.class, ServiceZ.class);
        }
    };
    final Module moduleB = new ModuleImpl("B") {
        @Override public Set<Module> getDependencies() {
            return Sets.newHashSet(moduleF, moduleC);
        }
    };
    final Module moduleA = new ModuleImpl("A") {
        @Override public Set<Module> getDependencies() {
            return Sets.newHashSet(moduleE, moduleC);
        }
    };

    final Module moduleG = new ModuleImpl("G") {
        @Override public Set<Module> getDependencies() {
            return Sets.newHashSet(moduleH);
        }
    };
    final Module moduleH = new ModuleImpl("H") {
        @Override public Set<Module> getDependencies() {
            return Sets.newHashSet(moduleI);
        }
    };

    final Module moduleI = new ModuleImpl("I") {
        @Override public Set<Module> getDependencies() {
            return Sets.newHashSet(moduleG);
        }
    };


    @Test
    public void no_cyclic_dependencies() throws Exception {

        // moduleF
        // moduleE [P; X]
        // moduleD            -> moduleE
        // moduleC [Q,R; Y,Z] -> moduleE, moduleD
        // moduleB            -> moduleF, moduleC
        // moduleA            -> moduleE, moduleC

        assertTransitiveDependencies(
                moduleF, Lists.newArrayList(moduleF));
        assertTransitiveDependenciesAsClass(
                moduleF, Lists.newArrayList());
        assertTransitiveServices(
                moduleF, Lists.newArrayList());

        assertTransitiveDependencies(
                moduleE, Lists.newArrayList(moduleE));
        assertTransitiveDependenciesAsClass(
                moduleE, Lists.newArrayList(ModuleP.class));
        assertTransitiveServices(
                moduleE, Lists.newArrayList(ServiceX.class));

        assertTransitiveDependencies(
                moduleD, Lists.newArrayList(moduleE, moduleD));
        assertTransitiveDependenciesAsClass(
                moduleD, Lists.newArrayList(ModuleP.class));
        assertTransitiveServices(
                moduleD, Lists.newArrayList(ServiceX.class));

        assertTransitiveDependencies(
                moduleC, Lists.newArrayList(moduleE, moduleD, moduleC));
        assertTransitiveDependenciesAsClass(
                moduleC, Lists.newArrayList(ModuleP.class, ModuleQ.class, ModuleR.class));
        assertTransitiveServices(
                moduleC, Lists.newArrayList(ServiceX.class, ServiceY.class, ServiceZ.class));

        assertTransitiveDependencies(
                moduleB, Lists.newArrayList(moduleE, moduleD, moduleC, moduleF, moduleB));
        assertTransitiveDependenciesAsClass(
                moduleB, Lists.newArrayList(ModuleP.class, ModuleQ.class, ModuleR.class));
        assertTransitiveServices(
                moduleB, Lists.newArrayList(ServiceX.class, ServiceY.class, ServiceZ.class));

        assertTransitiveDependencies(
                moduleA, Lists.newArrayList(moduleE, moduleD, moduleC, moduleA));
        assertTransitiveDependenciesAsClass(
                moduleA, Lists.newArrayList(ModuleP.class, ModuleQ.class, ModuleR.class));
        assertTransitiveServices(
                moduleA, Lists.newArrayList(ServiceX.class, ServiceY.class, ServiceZ.class));

    }

    @Test
    public void with_cyclic_dependencies() throws Exception {

        expectedException.expect(IllegalStateException.class);

        Module.Util.transitiveDependenciesOf(moduleG);
    }

    void assertTransitiveDependencies(
            final Module module, final List<Module> expected) {
        final List<Module> dependencies = Module.Util.transitiveDependenciesOf(module);
        assertThat(dependencies).containsAll(expected);
        assertThat(expected).containsAll(dependencies);
    }

    void assertTransitiveServices(
            final Module module, final List<Class<?>> expected) {
        final List<Class<?>> services = Module.Util.transitiveAdditionalServicesOf(module);
        assertThat(services).containsAll(expected);
        assertThat(expected).containsAll(services);
    }

    void assertTransitiveDependenciesAsClass(
            final Module module, final List<Class<?>> expected) {
        final List<Class<?>> dependenciesAsClass = Module.Util.transitiveDependenciesAsClassOf(module);
        assertThat(dependenciesAsClass).containsAll(expected);
        assertThat(expected).containsAll(dependenciesAsClass);
    }

}