package org.estatio.junit;

import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.PropertyActors;
import org.estatio.dom.asset.Units;
import org.estatio.dom.communicationchannel.CommunicationChannels;
import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.States;
import org.estatio.dom.index.Indices;
import org.estatio.dom.invoice.Charges;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.lease.LeaseActors;
import org.estatio.dom.lease.LeaseItems;
import org.estatio.dom.lease.LeaseTerms;
import org.estatio.dom.lease.LeaseUnits;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.party.Parties;
import org.estatio.dom.tax.Taxes;
import org.junit.Before;
import org.junit.runner.RunWith;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.progmodel.wrapper.applib.WrapperFactory;
import org.apache.isis.progmodel.wrapper.applib.WrapperObject;
import org.apache.isis.viewer.junit.ConfigDir;
import org.apache.isis.viewer.junit.IsisTestRunner;
import org.apache.isis.viewer.junit.Service;
import org.apache.isis.viewer.junit.Services;

@RunWith(IsisTestRunner.class)
@ConfigDir("../webapp/src/main/webapp/WEB-INF")
// acts as default, but can be overridden by annotations
@Services({ 
    @Service(Parties.class), 
    @Service(Properties.class), 
    @Service(Units.class), 
    @Service(Countries.class),
    @Service(States.class), 
    @Service(CommunicationChannels.class),
    @Service(Indices.class),
    @Service(PropertyActors.class),
    @Service(Leases.class),
    @Service(LeaseItems.class),
    @Service(LeaseTerms.class),
    @Service(LeaseActors.class),
    @Service(LeaseUnits.class),
    @Service(Invoices.class),
    @Service(Charges.class),
    @Service(Indices.class),
    @Service(Taxes.class),
    })
public abstract class AbstractTest {

    private DomainObjectContainer domainObjectContainer;
    private WrapperFactory wrapperFactory;

    @Before
    public void wrapInjectedServices() throws Exception {
        units = wrapped(units);
        properties = wrapped(properties);
        parties = wrapped(parties);
        countries = wrapped(countries);
    }

    protected <T> T wrapped(final T obj) {
        return wrapperFactory.wrap(obj);
    }

    @SuppressWarnings("unchecked")
    protected <T> T unwrapped(final T obj) {
        if (obj instanceof WrapperObject) {
            final WrapperObject wrapperObject = (WrapperObject) obj;
            return (T) wrapperObject.wrapped();
        }
        return obj;
    }

    // //////////////////////////////////////////////////////
    // Injected.
    // //////////////////////////////////////////////////////

    protected WrapperFactory getWrapperFactory() {
        return wrapperFactory;
    }

    public void setWrapperFactory(final WrapperFactory wrapperFactory) {
        this.wrapperFactory = wrapperFactory;
    }

    protected DomainObjectContainer getDomainObjectContainer() {
        return domainObjectContainer;
    }

    public void setDomainObjectContainer(final DomainObjectContainer domainObjectContainer) {
        this.domainObjectContainer = domainObjectContainer;
    }

    // {{ injected: Properties
    protected Properties properties;

    public void setProperties(final Properties properties) {
        this.properties = properties;
    }

    // }}

    // {{ injected: Units
    protected Units units;

    public void setUnits(final Units units) {
        this.units = units;
    }

    // }}

    // {{ injected: Parties
    protected Parties parties;

    public void setParties(final Parties parties) {
        this.parties = parties;
    }

    // }}

    // {{ injected: Countries
    protected Countries countries;

    public void setCountries(final Countries countries) {
        this.countries = countries;
    }
    // }}

}
