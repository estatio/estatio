package org.estatio.module.fastnet.dom;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.fastnet.dom.LeaseViewModel"
)
@Getter @Setter
public class LeaseViewModel {

    public LeaseViewModel(){};

    public LeaseViewModel(final String leaseReference, final String externalReference){
        this.leaseReference = leaseReference;
        this.externalReference = externalReference;
    }

    private String leaseReference;

    private String externalReference;

}
