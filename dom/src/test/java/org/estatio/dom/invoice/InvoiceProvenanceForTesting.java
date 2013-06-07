package org.estatio.dom.invoice;

import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable 
// necessary because of the interface mapping we have; 
// I *think* that DN will otherwise setup a SerializedMapping, and then get its knickers in a twist 
public class InvoiceProvenanceForTesting implements InvoiceProvenance {

}
