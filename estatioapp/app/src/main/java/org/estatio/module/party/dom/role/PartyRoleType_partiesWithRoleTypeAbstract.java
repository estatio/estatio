package org.estatio.module.party.dom.role;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.party.dom.Party;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class PartyRoleType_partiesWithRoleTypeAbstract<T extends Party> {

    final PartyRoleType partyRoleType;
    final Class<T> subclass;

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(defaultView = "table")
    public List<T> coll() {
        return partyRoleRepository.findByRoleType(partyRoleType).stream()
                .map(PartyRole::getParty)
                .filter(subclass::isInstance)
                .map(subclass::cast)
                .collect(Collectors.toList());
    }

    @Inject
    PartyRoleRepository partyRoleRepository;


}
