package com.eurocommercialproperties.estatio.junit;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.progmodel.wrapper.applib.WrapperFactory;
import org.apache.isis.progmodel.wrapper.applib.WrapperObject;
import org.apache.isis.viewer.junit.ConfigDir;
import org.apache.isis.viewer.junit.IsisTestRunner;
import org.apache.isis.viewer.junit.Service;
import org.apache.isis.viewer.junit.Services;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.eurocommercialproperties.estatio.dom.asset.Properties;
import com.eurocommercialproperties.estatio.dom.asset.Units;
import com.eurocommercialproperties.estatio.dom.party.Owners;
import com.eurocommercialproperties.estatio.objstore.dflt.asset.PropertiesDefault;
import com.eurocommercialproperties.estatio.objstore.dflt.asset.UnitsDefault;
import com.eurocommercialproperties.estatio.objstore.dflt.communicationchannel.CommunicationChannelsDefault;
import com.eurocommercialproperties.estatio.objstore.dflt.geography.CountriesDefault;
import com.eurocommercialproperties.estatio.objstore.dflt.geography.StatesDefault;
import com.eurocommercialproperties.estatio.objstore.dflt.party.OwnersDefault;



@RunWith(IsisTestRunner.class)
@ConfigDir("../webapp/src/main/webapp/WEB-INF") // acts as default, but can be overridden by annotations
@Services({ 
	@Service(OwnersDefault.class), 
	@Service(PropertiesDefault.class), 
	@Service(UnitsDefault.class), 
	@Service(CountriesDefault.class), 
	@Service(StatesDefault.class),
	@Service(CommunicationChannelsDefault.class)
})
public abstract class AbstractTest {

    private DomainObjectContainer domainObjectContainer;
    private WrapperFactory wrapperFactory;
    

    @Before
    public void wrapInjectedServices() throws Exception {
        units = wrapped(units);
        properties = wrapped(properties);
        owners = wrapped(owners);
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
	
	// {{ injected: Owners
	protected Owners owners;

	public void setOwners(final Owners owners) {
		this.owners = owners;
	}
	// }}


}
