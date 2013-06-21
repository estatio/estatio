package org.estatio.dom.financial;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.WithNameGetter;
import org.estatio.dom.WithReferenceGetter;
import org.estatio.dom.party.Party;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "FINANCIALACCOUNT_ID")
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
            name = "findByReference", language = "JDOQL", 
            value = "SELECT FROM org.estatio.dom.financial.FinancialAccount WHERE reference.matches(:r)"),
    @javax.jdo.annotations.Query(
            name = "findByTypeAndParty", language = "JDOQL", 
            value = "SELECT FROM org.estatio.dom.financial.FinancialAccount WHERE type == :type && owner == :owner")
})
@javax.jdo.annotations.Version(strategy=VersionStrategy.VERSION_NUMBER, column="VERSION")
public abstract class FinancialAccount extends EstatioTransactionalObject<FinancialAccount> implements WithReferenceGetter, WithNameGetter {

    public FinancialAccount() {
        super("type, reference");
    }
    
    // //////////////////////////////////////

    private String reference;

    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    private String name;

    @Title
    @MemberOrder(sequence = "2")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // //////////////////////////////////////

    private FinancialAccountType type;
    
    @Hidden
    @MemberOrder(sequence = "1")
    public FinancialAccountType getType() {
        return type;
    }

    public void setType(final FinancialAccountType type) {
        this.type = type;
    }
    
    // //////////////////////////////////////

    private Party owner;

    @MemberOrder(sequence = "1")
    public Party getOwner() {
        return owner;
    }

    public void setOwner(final Party owner) {
        this.owner = owner;
    }


}
