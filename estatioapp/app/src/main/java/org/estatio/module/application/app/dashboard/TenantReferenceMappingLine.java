package org.estatio.module.application.app.dashboard;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;

import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL, objectType = "dashboard.TenantReferenceMappingLine")
public class TenantReferenceMappingLine {

    public static Logger LOG = LoggerFactory.getLogger(TenantReferenceMappingLine.class);

    public TenantReferenceMappingLine(){}

    @Getter @Setter
    private String oldTenantReference;

    @Getter @Setter
    private String newTenantReference;


    public void importData() {

        Party p = partyRepository.findPartyByReference(getOldTenantReference());

        if (p==null) {
            LOG.warn(String.format("Party with reference %s not found", getOldTenantReference()));
        } else {
            LOG.info(String.format("Replacing party reference %s with %s", getOldTenantReference(), getNewTenantReference()));
            p.setReference(getNewTenantReference());
        }

    }

    @Inject PartyRepository partyRepository;
}
