package org.estatio.module.base.platform.applib;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import org.assertj.core.util.Lists;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import lombok.AllArgsConstructor;
import static org.assertj.core.api.Assertions.assertThat;

public class Module_getTransitiveDependencies_Test {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @AllArgsConstructor
    public class ModuleImpl implements Module {
        private final String name;
        @Override public String toString() {
            return name;
        }
    }

    final Module moduleF = new ModuleImpl("F") {};
    final Module moduleE = new ModuleImpl("E") {};
    final Module moduleD = new ModuleImpl("D") {
        @Override public Set<Module> getDependencies() {
            return Sets.newHashSet(moduleE);
        }
    };

    final Module moduleC = new ModuleImpl("C") {
        @Override public Set<Module> getDependencies() {
            return Sets.newHashSet(moduleE, moduleD);
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

        assertTransitiveDependencies(moduleF, Lists.newArrayList(moduleF));
        assertTransitiveDependencies(moduleE, Lists.newArrayList(moduleE));
        assertTransitiveDependencies(moduleD, Lists.newArrayList(moduleE, moduleD));
        assertTransitiveDependencies(moduleC, Lists.newArrayList(moduleE, moduleD, moduleC));
        assertTransitiveDependencies(moduleB, Lists.newArrayList(moduleE, moduleD, moduleC, moduleF, moduleB));
        assertTransitiveDependencies(moduleA, Lists.newArrayList(moduleE, moduleD, moduleC, moduleA));

    }

    @Test
    public void with_cyclic_dependencies() throws Exception {

        expectedException.expect(IllegalStateException.class);

        moduleG.getTransitiveDependencies();
    }

    void assertTransitiveDependencies(
            final Module module, final List<Module> expected) {
        final List<Module> transitiveDependencies = module.getTransitiveDependencies();
        assertThat(transitiveDependencies).containsAll(expected);
    }

}