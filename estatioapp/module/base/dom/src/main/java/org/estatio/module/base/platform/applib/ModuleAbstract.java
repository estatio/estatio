package org.estatio.module.base.platform.applib;

import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
}
