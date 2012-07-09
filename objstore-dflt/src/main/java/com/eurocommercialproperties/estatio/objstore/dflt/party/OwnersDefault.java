package com.eurocommercialproperties.estatio.objstore.dflt.party;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.filter.Filter;

import com.eurocommercialproperties.estatio.dom.party.Owner;
import com.eurocommercialproperties.estatio.dom.party.Owners;

public class OwnersDefault extends AbstractFactoryAndRepository implements Owners {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "owners";
    }

    public String iconName() {
        return "Owner";
    }
    // }}

     // {{ NewOwner (action)
    @Override
    public Owner newOwner(String reference, String name) {
        final Owner owner = newTransientInstance(Owner.class);
        owner.setReference(reference);
        owner.setName(name);
        persist(owner);
        return owner;
    }
    // }}

    // {{ AllInstances
    @Override
    public List<Owner> allInstances() {
    	return allInstances(Owner.class);
    }
    // }}

    @Override
    public Owner findByReference(final String reference) {
        return firstMatch(Owner.class, new Filter<Owner>() {
            @Override
            public boolean accept(final Owner owner) {
                return reference.equals(owner.getReference());
            }
        });
    }

}
