package org.estatio.dom.lease;


// REVIEW: this needs to be made into an entity, so can make into a multi-tenanted entity:
//@javax.jdo.annotations.PersistenceCapable
public enum LeaseType {

    AA("Apparecchiature Automatic"),
    AD("Affitto d'Azienda"),
    CG("Comodato Gratuito"),
    CO("Comodato"),
    DH("Dehors"),
    LO("Locazione"),
    OA("Occup. Abusiva Affito"),
    OL("Occup. Abusiva Locazione"),
    PA("Progroga Affitto"),
    PL("Progroga Locazione"),
    PP("Pannelli Pubblicitari"),
    PR("Precaria"),
    SA("Scritt. Privata Affitto"),
    SL("Scritt. Privata Locazione");

    private final String title;

    private LeaseType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }
    
    //TODO: Handle localised titles. 
    // 2013-04-13: still not need
    
}
