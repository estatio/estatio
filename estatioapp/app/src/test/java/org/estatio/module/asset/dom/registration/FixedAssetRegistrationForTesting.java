package org.estatio.module.asset.dom.registration;

import javax.jdo.annotations.DiscriminatorStrategy;

import org.apache.isis.applib.annotation.DomainObject;

@DomainObject(
        objectType = "org.estatio.module.asset.dom.registration.FixedAssetRegistrationForTesting"
)
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.VALUE_MAP,
        column = "discriminator",
        value = "org.estatio.module.asset.dom.registration.FixedAssetRegistrationForTesting"
)
public class FixedAssetRegistrationForTesting extends FixedAssetRegistration {

}
