package org.estatio.module.base.platform.applib;

import java.util.Objects;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public abstract class ModuleAbstract implements Module {

    @XmlAttribute(required = true)
    public String getName() {
        return getClass().getSimpleName();
    }

    @XmlAttribute(required = true)
    public String getFullName() {
        return getClass().getName();
    }

    @XmlTransient
    public Set<Module> getDependencies() {
        return Module.super.getDependencies();
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