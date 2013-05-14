package org.estatio.dom.lease;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public class LeaseUnitActivity extends LeaseUnitReference {

}
