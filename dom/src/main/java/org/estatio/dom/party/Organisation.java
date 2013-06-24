package org.estatio.dom.party;



@javax.jdo.annotations.Queries({ 
    @javax.jdo.annotations.Query(name = "findByReferenceOrName", language = "JDOQL", value = "SELECT FROM org.estatio.dom.party.Organisation WHERE (reference.matches(:searchArg) || :name.matches(:searchArg))")
})
@javax.jdo.annotations.PersistenceCapable
public class Organisation extends Party {


}
