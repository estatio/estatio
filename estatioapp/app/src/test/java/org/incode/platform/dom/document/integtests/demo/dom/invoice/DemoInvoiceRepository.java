package org.incode.platform.dom.document.integtests.demo.dom.invoice;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.platform.dom.document.integtests.demo.dom.demowithnotes.DemoObjectWithNotes;

@DomainService(nature = NatureOfService.DOMAIN )
public class DemoInvoiceRepository {


    @Programmatic
    public List<DemoInvoice> listAll() {
        return repositoryService.allInstances(DemoInvoice.class);
    }

    @Programmatic
    public List<DemoInvoice> findByCustomer(final DemoObjectWithNotes demoCustomer) {
        return Lists.newArrayList(
                listAll().stream()
                        .filter(x -> Objects.equals(x.getCustomer(), demoCustomer))
                        .collect(Collectors.toList()));
    }

    @Programmatic
    public DemoInvoice create(
            final String num,
            final DemoObjectWithNotes customer) {
        final DemoInvoice obj = new DemoInvoice(num, customer);
        repositoryService.persist(obj);
        return obj;
    }

    @javax.inject.Inject
    RepositoryService repositoryService;

}
