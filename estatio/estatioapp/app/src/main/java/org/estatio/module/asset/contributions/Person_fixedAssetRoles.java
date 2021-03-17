package org.estatio.module.asset.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.asset.dom.role.FixedAssetRoleRepository;
import org.estatio.module.party.dom.Person;

@Mixin(method="act")
public class Person_fixedAssetRoles {

    private final Person person;

    public Person_fixedAssetRoles(Person person) {
        this.person = person;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(defaultView = "table")
    public List<FixedAssetRole> act() {
        return fixedAssetRoleRepository.findByParty(person);
    }

    @Inject
    FixedAssetRoleRepository fixedAssetRoleRepository;

}
