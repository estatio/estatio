package org.estatio.dom.invoice;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;


public class InvoiceItemTest_compareTo {

    private InvoiceItem item1;
    private InvoiceItem item2;
    private InvoiceItem item3;
    private InvoiceItem item4;
    
    private Invoice inv;
    private Invoice inv2;
    
    @Before
    public void setup() {
        
        inv = new Invoice();
        inv.setInvoiceNumber("000001");
        
        inv2 = new Invoice();
        inv2.setInvoiceNumber("000002");
        
        item1 = newInvoiceItem();
        item2 = newInvoiceItem();
        item3 = newInvoiceItem();
        item4 = newInvoiceItem();
    }

    
    @Test
    public void onInvoice() {
        
        item2.setInvoice(inv);
        item3.setInvoice(inv);
        item4.setInvoice(inv2);
        
        assertOrder();
    }



    @Test
    public void onStartDate() {

        item1.setInvoice(inv);
        item2.setInvoice(inv);
        item3.setInvoice(inv);
        item4.setInvoice(inv);

        item1.setStartDate(null);
        item2.setStartDate(new LocalDate(2013,4,1));
        item3.setStartDate(new LocalDate(2013,4,1));
        item4.setStartDate(new LocalDate(2013,4,2));
        
        assertOrder();
    }

    @Test
    public void onDueDate() {
        item1.setInvoice(inv);
        item2.setInvoice(inv);
        item3.setInvoice(inv);
        item4.setInvoice(inv);

        item1.setStartDate(new LocalDate(2013,4,1));
        item2.setStartDate(new LocalDate(2013,4,1));
        item3.setStartDate(new LocalDate(2013,4,1));
        item4.setStartDate(new LocalDate(2013,4,1));
        
        item1.setDueDate(null);
        item2.setDueDate(new LocalDate(2013,5,1));
        item3.setDueDate(new LocalDate(2013,5,1));
        item4.setDueDate(new LocalDate(2013,5,2));
        
        assertOrder();
    }

    @Test
    public void onDescription() {
        item1.setInvoice(inv);
        item2.setInvoice(inv);
        item3.setInvoice(inv);
        item4.setInvoice(inv);

        item1.setStartDate(new LocalDate(2013,4,1));
        item2.setStartDate(new LocalDate(2013,4,1));
        item3.setStartDate(new LocalDate(2013,4,1));
        item4.setStartDate(new LocalDate(2013,4,1));
        
        item1.setStartDate(new LocalDate(2013,5,2));
        item2.setStartDate(new LocalDate(2013,5,2));
        item3.setStartDate(new LocalDate(2013,5,2));
        item4.setStartDate(new LocalDate(2013,5,2));
        
        item1.setDescription(null);
        item2.setDescription("desc 1");
        item3.setDescription("desc 1");
        item4.setDescription("desc 2");
        
        assertOrder();
    }


    void assertOrder() {
        assertThat(item1.compareTo(item2), is(Matchers.lessThan(0)));
        assertThat(item2.compareTo(item1), is(Matchers.greaterThan(0)));
        
        assertThat(item2.compareTo(item3), is(0));
        
        assertThat(item3.compareTo(item4), is(Matchers.lessThan(0)));
        assertThat(item4.compareTo(item3), is(Matchers.greaterThan(0)));
    }
    

    public InvoiceItem newInvoiceItem() {
        return new InvoiceItem() {
            @Override
            public void attachToInvoice() {
            }
        };
    }

}
