package org.isisaddons.module.base.platform.applib;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.apache.isis.applib.fixturescripts.FixtureScript;

/**
 * have moved this to isisaddons only so that S101 gives us a nice picture.
 * Eventually gonna move to org.apache.isis.applib
 */
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

    default Set<Class<?>> getAdditionalServices() {
        return Collections.emptySet();
    }

    /**
     * Recursively obtain the transitive dependencies.
     *
     * <p>
     *     The dependencies are returned in order, with this (the top-most) module last.
     * </p>
     */
    default List<Module> getTransitiveDependencies() {
        final List<Module> ordered = Lists.newArrayList();
        final List<Module> visited = Lists.newArrayList();
        appendDependenciesTo(ordered, this, visited);
        final LinkedHashSet<Module> sequencedSet = Sets.newLinkedHashSet(ordered);
        return Lists.newArrayList(sequencedSet);
    }

    /**
     * Obtain the {@link #getDependenciesAsClass()} of this module and all its
     * {@link #getTransitiveDependencies() transitive dependencies}.
     *
     * <p>
     *     No guarantees are made as to the order of these additional module classes.
     * </p>
     */
    default List<Class<?>> getTransitiveDependenciesAsClass() {
        final Set<Class<?>> modules = Sets.newHashSet();
        final List<Module> transitiveDependencies = getTransitiveDependencies();
        for (Module transitiveDependency : transitiveDependencies) {
            final Set<Class<?>> additionalModules = transitiveDependency.getDependenciesAsClass();
            if(additionalModules != null && !additionalModules.isEmpty()) {
                modules.addAll(additionalModules);
            }
        }
        return Lists.newArrayList(modules);
    }

    /**
     * Obtain the {@link #getAdditionalServices()} of this module and all its
     * {@link #getTransitiveDependencies() transitive dependencies}.
     *
     * <p>
     *     No guarantees are made as to the order of these additional service classes.
     * </p>
     */
    default List<Class<?>> getTransitiveAdditionalServices() {
        final Set<Class<?>> services = Sets.newHashSet();
        final List<Module> transitiveDependencies = getTransitiveDependencies();
        for (Module transitiveDependency : transitiveDependencies) {
            final Set<Class<?>> additionalServices = transitiveDependency.getAdditionalServices();
            if(additionalServices != null && !additionalServices.isEmpty()) {
                services.addAll(additionalServices);
            }
        }
        return Lists.newArrayList(services);
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
