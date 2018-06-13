package org.estatio.module.fastnet.dom;

import org.apache.isis.applib.annotation.ViewModel;

import lombok.Getter;
import lombok.Setter;

@ViewModel
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
