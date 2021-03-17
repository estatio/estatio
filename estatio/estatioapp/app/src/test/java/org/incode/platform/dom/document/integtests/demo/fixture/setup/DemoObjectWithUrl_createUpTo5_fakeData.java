package org.incode.platform.dom.document.integtests.demo.fixture.setup;

import java.util.List;

import com.google.common.collect.Lists;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.incode.platform.dom.document.integtests.demo.dom.demowithurl.DemoObjectWithUrl;
import org.incode.platform.dom.document.integtests.demo.dom.demowithurl.DemoObjectWithUrlMenu;

import lombok.Getter;

public class DemoObjectWithUrl_createUpTo5_fakeData extends FixtureScript {

    @javax.inject.Inject
    DemoObjectWithUrlMenu demoObjectMenu;

    @javax.inject.Inject
    FakeDataService fakeDataService;

    @Getter
    private Integer number ;


    public DemoObjectWithUrl_createUpTo5_fakeData setNumber(final Integer number) {
        this.number = number;
        return this;
    }

    @Getter
    private List<DemoObjectWithUrl> demoObjects = Lists.newArrayList();


    @Override
    protected void execute(final ExecutionContext ec) {

        defaultParam("number", ec, 3);
        if(getNumber() < 1 || getNumber() > 5) {
            // there are 5 sample PDFs
            throw new IllegalArgumentException("number of demo objects to create must be within [1,5]");
        }

        for (int i = 0; i < getNumber(); i++) {
            final DemoObjectWithUrl demoObject = create(i, ec);
            getDemoObjects().add(demoObject);
        }
    }

    private DemoObjectWithUrl create(final int n, final ExecutionContext ec) {
        final String name = fakeDataService.name().firstName();
        final String url = "http://www.pdfpdf.com/samples/Sample" + (n+1) + ".PDF";

        final DemoObjectWithUrl demoObject = wrap(demoObjectMenu).createDemoObjectWithUrl(name);
        wrap(demoObject).setUrl(url);

        return ec.addResult(this, demoObject);
    }

}
