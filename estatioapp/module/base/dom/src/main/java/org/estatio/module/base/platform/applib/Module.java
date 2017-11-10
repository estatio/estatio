package org.estatio.module.base.platform.applib;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import org.apache.isis.applib.fixturescripts.FixtureScript;

public interface Module {

    /**
     * As per Maven's &lt;dependencies&gt;&lt;/dependencies&gt; element; in the future might be derived (code generated?) from java 9's <code>module-info.java</code> metadata
     *
     * <p>
     *     We use Set (rather than List) because we rely on {@link Module} being a value type based solely on its
     *     class.  What this means is that each module can simply instantiate its dependencies, and the framework will
     *     be able to eliminate duplicates.
     * </p>
     */
    default Set<Module> getDependencies() {
        return Collections.emptySet();
    }

    /**
     * Support for "legacy" modules that do not implement {@link Module}.
     */
    default Set<Class<?>> getDependenciesAsClass() {
        return Collections.emptySet();
    }

    default FixtureScript getRefDataSetupFixture() {
        return null;
    }

    default FixtureScript getTeardownFixture() {
        return null;
    }

    /**
     * Recursively obtain the transitive dependencies.
     */
    default List<Module> getTransitiveDependencies() {
        final List<Module> ordered = Lists.newArrayList();
        final List<Module> visited = Lists.newArrayList();
        appendDependenciesTo(ordered, this, visited);
        return ordered;
    }

    default void appendDependenciesTo(
            final List<Module> ordered,
            final Module module,
            final List<Module> visited) {

        if(visited.contains(module)) {
            throw new IllegalStateException(String.format(
                    "Cyclic dependency detected; visited: %s", visited));
        } else {
            visited.add(module);
        }

        final Set<Module> dependencies = module.getDependencies();
        if(dependencies.isEmpty() || ordered.containsAll(dependencies)) {
            ordered.add(module);
            visited.clear(); // reset
        } else {
            for (Module dependency : dependencies) {
                appendDependenciesTo(ordered, dependency, visited);
            }
        }
        if(!ordered.contains(module)) {
            ordered.add(module);
        }
    }


    class Utils {
        private Utils(){}
        public static FixtureScript allOf(
                final FixtureScript... fixtureScriptArray) {
            return new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {
                    for (FixtureScript fixtureScript : fixtureScriptArray) {
                        executionContext.executeChild(this, fixtureScript);
                    }
                }
            };
        }
    }

}
