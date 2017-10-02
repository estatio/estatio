package org.estatio.dom.viewmodels;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.Importable;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.asset.PropertyType;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.LeaseType;
import org.estatio.dom.lease.LeaseTypeRepository;
import org.estatio.dom.lease.breaks.prolongation.ProlongationOptionRepository;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.LeaseImport"
)
public class LeaseImport implements ExcelFixtureRowHandler, Importable {

    private static final Logger LOG = LoggerFactory.getLogger(LeaseImport.class);

    @Getter @Setter
    private String reference;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String tenantReference;

    @Getter @Setter
    private String landlordReference;

    @Getter @Setter
    private String type;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private LocalDate endDate;

    @Getter @Setter
    private LocalDate tenancyStartDate;

    @Getter @Setter
    private LocalDate tenancyEndDate;

    @Getter @Setter
    private String propertyReference;

    @Getter @Setter
    private String externalReference;

    @Getter @Setter
    private String prolongationPeriod;

    @Getter @Setter
    private String notificationPeriod;

    @Getter @Setter
    private String comments;

    @Getter @Setter
    private String previousLeaseReference;

    static int counter = 0;

//    @Override
//    public List<Class> importAfter() {
//        return Lists.newArrayList(OrganisationImport.class, LeaseTypeImport.class, PropertyImport.class);
//    }

    @Override
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object previousRow) {
        return importData(previousRow);
    }

    // REVIEW: other import view models have @Action annotation here...  but in any case, is this view model actually ever surfaced in the UI?
    public List<Object> importData() {
        return importData(null);
    }

    @Programmatic
    @Override
    public List<Object> importData(final Object previousRow) {

        final Party tenant = fetchParty(tenantReference);
        final Party landlord = fetchParty(landlordReference);
        Lease lease = leaseRepository.findLeaseByReferenceElseNull(reference);
        final LeaseType leaseType = leaseTypeRepository.findByReference(type);
        final Property property = fetchProperty(propertyReference, null, false);

        if (lease == null) {
            lease = leaseRepository.newLease(property.getApplicationTenancy(), reference, name, leaseType, startDate, endDate, tenancyStartDate, tenancyEndDate, landlord, tenant);
        }
        lease.setTenancyStartDate(tenancyStartDate);
        lease.setTenancyEndDate(tenancyEndDate);
        lease.setExternalReference(externalReference);
        lease.setComments(getComments());

        if (getProlongationPeriod() != null && getNotificationPeriod() != null) {
            prolongationOptionRepository.newProlongationOption(lease, getProlongationPeriod(), getNotificationPeriod(), null);
        }

        if (getPreviousLeaseReference() != null) {
            Lease previous = leaseRepository.findLeaseByReference(getPreviousLeaseReference());
            if (previous == null) {
                //oops, not found?
                System.out.println(String.format("On lease [%s] the previous lease [%s] was not found", getReference(), getPreviousLeaseReference()));
            } else {
                lease.setPrevious(previous);
                previous.setNext(lease); //Huh? Two sided
            }
        }

//        container.flush();
        return Lists.newArrayList(lease);

    }

    private Party fetchParty(final String partyReference) {
        final Party party = partyRepository.findPartyByReference(partyReference);
        if (party == null) {
            throw new ApplicationException(String.format("Party with reference %s not found.", partyReference));
        }
        return party;
    }

    private Property fetchProperty(
            final String reference,
            final ApplicationTenancy appTenancy,
            final boolean createIfNotFond) {
        if (reference == null) {
            return null;
        }
        Property property = propertyRepository.findPropertyByReference(reference);
        if (property == null) {
            if (!createIfNotFond)
                throw new ApplicationException(String.format("Property with reference %s not found.", reference));
            property = propertyRepository.newProperty(reference, null, PropertyType.MIXED, null, null, null);
        }
        return property;
    }

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    private LeaseTypeRepository leaseTypeRepository;

    @Inject
    private PartyRepository partyRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject ProlongationOptionRepository prolongationOptionRepository;


//    @Inject
//    private DomainObjectContainer container;

//    @Inject
//    WrapperFactory wrapperFactory;


}
