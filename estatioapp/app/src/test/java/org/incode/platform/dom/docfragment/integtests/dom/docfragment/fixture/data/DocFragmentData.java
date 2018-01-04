package org.incode.platform.dom.docfragment.integtests.dom.docfragment.fixture.data;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.docfragment.dom.impl.DocFragment;
import org.incode.module.docfragment.dom.impl.DocFragmentRepository;
import org.incode.module.fixturesupport.dom.data.DemoData;
import org.incode.module.fixturesupport.dom.data.DemoDataPersistAbstract;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum DocFragmentData implements DemoData<DocFragmentData, DocFragment> {

    Customer_hello_GLOBAL("exampledemodocfragment.DemoCustomer", "hello", "/", "Hello, nice to meet you, ${title} ${lastName}"),
    Customer_hello_ITA("exampledemodocfragment.DemoCustomer", "hello", "/ITA", "Ciao, piacere di conoscerti, ${title} ${lastName}"),
    Customer_hello_FRA("exampledemodocfragment.DemoCustomer", "hello", "/FRA", "Bonjour, ${title} ${lastName}, agréable de vous rencontrer"),
    Customer_goodbye_GLOBAL("exampledemodocfragment.DemoCustomer", "goodbye", "/", "So long, ${firstName}"),
    Invoice_due_GLOBAL("exampledemodocfragment.DemoInvoiceWithAtPath", "due", "/", "The invoice will be due on the ${dueBy?string[\"dd-MMM-yyyy\"]}, payable in ${numDays} days"),
    Invoice_due_FRA("exampledemodocfragment.DemoInvoiceWithAtPath", "due", "/FRA", "La facture sera due sur le ${dueBy?string[\"dd-MMM-yyyy\"]}, payable dans ${numDays} jours");

    private final String objectType;
    private final String name;
    private final String atPath;
    private final String templateText;

    @Programmatic
    public DocFragment asDomainObject() {
        return DocFragment.builder()
                .objectType(this.getObjectType())
                .name(this.getName())
                .atPath(this.getAtPath())
                .templateText(this.getTemplateText())
                .build();
    }

    @Programmatic
    public DocFragment persistUsing(final ServiceRegistry2 serviceRegistry) {
        return Util.persist(this, serviceRegistry);
    }

    @Programmatic
    public DocFragment createWith(final DocFragmentRepository repository) {
        return repository.create(getObjectType(), getName(), getAtPath(), getTemplateText());
    }

    @Programmatic
    public DocFragment findUsing(final ServiceRegistry2 serviceRegistry) {
        return Util.firstMatch(this, serviceRegistry);
    }

    public static class PersistScript extends DemoDataPersistAbstract<PersistScript, DocFragmentData, DocFragment> {
        public PersistScript() {
            super(DocFragmentData.class);
        }
    }

}
