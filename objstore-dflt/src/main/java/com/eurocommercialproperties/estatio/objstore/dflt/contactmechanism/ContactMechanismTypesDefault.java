package com.eurocommercialproperties.estatio.objstore.dflt.contactmechanism;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.QueryOnly;

import com.eurocommercialproperties.estatio.dom.contactmechanism.ContactMechanismType;
import com.eurocommercialproperties.estatio.dom.contactmechanism.ContactMechanismTypes;
import com.eurocommercialproperties.estatio.dom.geography.Countries;
import com.eurocommercialproperties.estatio.dom.geography.Country;

public class ContactMechanismTypesDefault extends AbstractFactoryAndRepository implements ContactMechanismTypes {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "contactmechanismtype";
    }

    public String iconName() {
        return "ContactMechanismType";
    }

    // }}

    // {{ New  (hidden)
    @Override
	public ContactMechanismType newContactMechanismType(
			String fullyQualifiedClassName) {
		return null;
	}
    // }}

    // {{ AllInstances
    @Override
    public List<ContactMechanismType> allInstances() {
    	return allInstances(ContactMechanismType.class);
    }
    // }}
	
}
