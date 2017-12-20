package org.incode.platform.dom.alias.integtests.dom.alias.dom;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Property;

import org.incode.module.alias.dom.impl.Alias;
import org.incode.module.alias.dom.impl.AliasRepository;
import org.incode.module.alias.dom.impl.T_addAlias;
import org.incode.module.alias.dom.impl.T_aliases;
import org.incode.module.alias.dom.impl.T_removeAlias;
import org.incode.platform.dom.alias.integtests.demo.dom.demo.DemoObject;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType= IdentityType.DATASTORE,
        schema="exampleDomAlias"
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@DomainObject
public class AliasForDemoObject extends Alias {


    @Column(allowsNull = "false", name = "demoObjectId")
    @Property(editing = Editing.DISABLED)
    @Getter @Setter
    private DemoObject demoObject;


    @Override
    public Object getAliased() {
        return getDemoObject();
    }
    @Override
    protected void setAliased(final Object aliased) {
        setDemoObject((DemoObject) aliased);
    }


    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SubtypeProvider extends AliasRepository.SubtypeProviderAbstract {
        public SubtypeProvider() {
            super(DemoObject.class, AliasForDemoObject.class);
        }
    }


    @Mixin
    public static class _aliases extends T_aliases<DemoObject> {
        public _aliases(final DemoObject aliased) {
            super(aliased);
        }
    }
    @Mixin
    public static class _addAlias extends T_addAlias<DemoObject> {
        public _addAlias(final DemoObject aliased) {
            super(aliased);
        }
    }
    @Mixin
    public static class _removeAlias extends T_removeAlias<DemoObject> {
        public _removeAlias(final DemoObject aliased) {
            super(aliased);
        }
    }

}
