package org.estatio.module.capex.dom.order.generateDoc;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.SortedSet;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.config.ConfigurationService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.freemarker.dom.service.FreeMarkerService;
import org.isisaddons.module.xdocreport.dom.service.OutputType;
import org.isisaddons.module.xdocreport.dom.service.XDocReportService;

import org.incode.module.document.dom.impl.docs.DocumentTemplate;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.seed.ordertmplt.DocumentTemplateFSForOrderConfirm;
import org.estatio.module.capex.spiimpl.docs.rml.RendererModelFactoryForOrder;
import org.estatio.module.party.dom.Organisation;

import fr.opensagres.xdocreport.core.io.IOUtils;
import static org.assertj.core.api.Assertions.assertThat;

public class DocumentTemplate_ORDER_TEMPLATE_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    DocumentTemplate mockDocumentTemplate;
    @Mock
    ConfigurationService mockConfigurationService;
    @Mock
    ClockService mockClockService;

    private FreeMarkerService freeMarkerService;
    private XDocReportService xDocReportService;

    private Order order;
    private Property property;
    private Organisation seller;
    private RendererModelFactoryForOrder.DataModel orderModel;

    private byte[] contentTemplate;
    private String nameTemplate;


    @Before
    public void setUp() throws Exception {

        context.checking(new Expectations() {{
            ignoring(mockConfigurationService);

            allowing(mockClockService).now();
            will(returnValue(new LocalDate(2019,2,6)));
        }});


        freeMarkerService = new FreeMarkerService();
        // yuk!
        final Field field = freeMarkerService.getClass().getDeclaredField("configurationService");
        field.setAccessible(true);
        field.set(freeMarkerService, mockConfigurationService);

        freeMarkerService.init(Maps.newHashMap());


        xDocReportService = new XDocReportService();
        xDocReportService.init();


        final DocumentTemplateFSForOrderConfirm fs = new DocumentTemplateFSForOrderConfirm(null);
        contentTemplate = fs.loadBytesForOrderConfirmTemplateItaDocx();
        nameTemplate = fs.loadCharsForOrderConfirmTemplateTitleItaFtl();

        order = new Order();
        order.setOrderNumber("3775/CUR/212/005");
        order.setOrderDate(new LocalDate(2018,6,2));
        order.setAtPath("/ITA");

        seller = new Organisation();
        seller.setName("PROGETTO ERRE SRL");
        seller.setReference("ITFO11145");
        order.setSeller(seller);

        property = new Property();
        property.setFullName("Centro Commerciale I Gigli");
        order.setProperty(property);

        final SortedSet<OrderItem> items = Sets.newTreeSet();
        order.setItems(items);
        items.add(newOrderItem(order, 0,
                "Ristrutturazione appartamento Via San Quirico 165 in n. 3 appartamenti", "34848.00"));
        items.add(newOrderItem(order, 1,
                "Ristrutturazione appartamento Via San Quirico 165 in n. 3 appartamenti - Integrazione n. 1", "2175.00"));
        items.add(newOrderItem(order, 2,
                "Ristrutturazione appartamento Via San Quirico 165 in n. 3 appartamenti - Integrazione n. 2", "2321.00"));

        final RendererModelFactoryForOrder modelFactory = new RendererModelFactoryForOrder() {
            @Override protected ClockService getClockService() { return mockClockService; }
        };

        orderModel = (RendererModelFactoryForOrder.DataModel)
                modelFactory.newRendererModel(mockDocumentTemplate, order);
    }

    private static OrderItem newOrderItem(
            final Order order,
            final int number,
            final String description,
            final String netAmountStr) {
        final OrderItem orderItem = new OrderItem() {
            @Override public String getAtPath() {
                return order.getAtPath();
            }
        };
        orderItem.setOrdr(order);
        orderItem.setNumber(number);
        orderItem.setDescription(description);
        orderItem.setNetAmount(new BigDecimal(netAmountStr));
        return orderItem;
    }

    @Test
    public void name_renders_ok() throws Exception {

        // when
        final String renderedName = freeMarkerService.render("abc", nameTemplate, orderModel);

        // then
        assertThat(renderedName).isEqualTo("Order 1234 02-06-2018");

    }
    @Test
    public void content_renders_ok() throws Exception {

        // when
        final byte[] renderedBytes = xDocReportService.render(contentTemplate, orderModel, OutputType.DOCX);

        // then
        assertThat(renderedBytes).isNotNull();

        IOUtils.write(renderedBytes,new FileOutputStream(new File("target/Order.docx")));

    }

}
