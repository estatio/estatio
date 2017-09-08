package org.estatio.dom.asset.role.mixins;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.asset.role.FixedAssetRole;
import org.estatio.dom.asset.role.FixedAssetRoleRepository;
import org.estatio.dom.party.Person;

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
