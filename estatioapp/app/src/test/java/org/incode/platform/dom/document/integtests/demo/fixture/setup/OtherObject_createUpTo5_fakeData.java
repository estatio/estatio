package org.incode.platform.dom.document.integtests.demo.fixture.setup;

import java.util.List;

import com.google.common.collect.Lists;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.incode.platform.dom.document.integtests.demo.dom.other.OtherObject;
import org.incode.platform.dom.document.integtests.demo.dom.other.OtherObjectMenu;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class OtherObject_createUpTo5_fakeData extends FixtureScript {

    @javax.inject.Inject
    OtherObjectMenu otherObjectMenu;

    @javax.inject.Inject
    FakeDataService fakeDataService;

    @Getter @Setter
    private Integer number ;

    @Getter
    private List<OtherObject> otherObjects = Lists.newArrayList();


    @Override
    protected void execute(final ExecutionContext ec) {

        defaultParam("number", ec, 5);
        if(getNumber() < 1 || getNumber() > 5) {
            throw new IllegalArgumentException("number of other objects to create must be within [1,5]");
        }

        for (int i = 0; i < getNumber(); i++) {
            final OtherObject otherObject = create(ec);
            getOtherObjects().add(otherObject);
        }
    }

    private OtherObject create(final ExecutionContext ec) {
        final String name = fakeDataService.name().firstName();

        final OtherObject otherObject = wrap(otherObjectMenu).createOtherObjects(name);

        return ec.addResult(this, otherObject);
    }

}
