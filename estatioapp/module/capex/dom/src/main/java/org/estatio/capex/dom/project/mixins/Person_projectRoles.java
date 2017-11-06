package org.estatio.capex.dom.project.mixins;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.project.ProjectRole;
import org.estatio.capex.dom.project.ProjectRoleRepository;
import org.estatio.module.party.dom.Person;

@Mixin(method="act")
public class Person_projectRoles {

    private final Person person;

    public Person_projectRoles(Person person) {
        this.person = person;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(defaultView = "table")
    public List<ProjectRole> act() {
        return projectRoleRepository.findByParty(person);
    }

    @Inject
    ProjectRoleRepository projectRoleRepository;

}
