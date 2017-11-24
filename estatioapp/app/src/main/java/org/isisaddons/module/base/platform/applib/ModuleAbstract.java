package org.isisaddons.module.base.platform.applib;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.isis.applib.fixturescripts.FixtureScript;

public abstract class ModuleAbstract implements Module {


    /**
     * As per Maven's &lt;dependencies&gt;&lt;/dependencies&gt; element; in the future might be derived (code generated?) from java 9's <code>module-info.java</code> metadata
     *
     * <p>
     *     We use Set (rather than List) because we rely on {@link Module} being a value type based solely on its
     *     class.  What this means is that each module can simply instantiate its dependencies, and the framework will
     *     be able to eliminate duplicates.
     * </p>
     */
    @Override
    @XmlTransient
    public Set<Module> getDependencies() {
        return Collections.emptySet();
    }

    /**
     * Support for "legacy" modules that do not implement {@link Module}.
     */
    @Override
    @XmlTransient
    public Set<Class<?>> getDependenciesAsClass() {
        return Collections.emptySet();
    }

    @Override
    @XmlTransient
    public FixtureScript getRefDataSetupFixture() {
        return null;
    }

    @Override
    @XmlTransient
    public FixtureScript getTeardownFixture() {
        return null;
    }

    @Override
    @XmlTransient
    public Set<Class<?>> getAdditionalServices() {
        return Collections.emptySet();
    }




    @XmlAttribute(required = true)
    public String getName() {
        return getClass().getSimpleName();
    }

    private String getFullName() {
        return getClass().getName();
    }


    @XmlElement(name = "module", required = true)
    private Set<ModuleAbstract> getModuleDependencies() {
        return (Set) getDependencies();
    }

    @Override
    public String toString() {
        return getFullName();
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ModuleAbstract)) {
            return false;
        }
        final ModuleAbstract other = (ModuleAbstract) o;
        return Objects.equals(getFullName(), other.getFullName());
    }

    public int hashCode() {
        return getFullName().hashCode();
    }

}