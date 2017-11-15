package org.estatio.module.base.platform.applib;

import java.util.Objects;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public abstract class ModuleAbstract implements Module {

    @XmlAttribute(required = true)
    public String getNamed() {
        return getClass().getSimpleName();
    }

    @XmlTransient
    public Set<Module> getDependencies() {
        return Module.super.getDependencies();
    }

    // @XmlElementWrapper
    @XmlElement(name = "module", required = true)
    public Set<ModuleAbstract> getModuleDependencies() {
        return (Set)getDependencies();
    }


    @Override
    public String toString() {
        return getNamed();
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ModuleAbstract)) {
            return false;
        }
        final ModuleAbstract other = (ModuleAbstract) o;
        return Objects.equals(getNamed(), other.getNamed());
    }

    public int hashCode() {
        return getNamed().hashCode();
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ModuleAbstract;
    }
}
