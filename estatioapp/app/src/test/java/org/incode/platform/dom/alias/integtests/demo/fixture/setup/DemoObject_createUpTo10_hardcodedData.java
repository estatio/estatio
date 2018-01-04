package org.incode.platform.dom.alias.integtests.demo.fixture.setup;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.platform.dom.alias.integtests.demo.dom.demo.DemoObject;
import org.incode.platform.dom.alias.integtests.demo.fixture.setup.sub.DemoObject_create;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class DemoObject_createUpTo10_hardcodedData extends FixtureScript {

    public final List<String> NAMES = Collections.unmodifiableList(Arrays.asList(
            "Foo", "Bar", "Baz", "Frodo", "Froyo", "Fizz", "Bip", "Bop", "Bang", "Boo"));

    @Getter @Setter
    private Integer number;

    @Getter
    private final List<DemoObject> demoObjects = Lists.newArrayList();

    @Override
    protected void execute(final ExecutionContext ec) {

        // defaults
        final int number = defaultParam("number", ec, 5);

        // validate
        if(number < 0 || number > NAMES.size()) {
            throw new IllegalArgumentException(String.format("number must be in range [0,%d)", NAMES.size()));
        }

        // execute
        for (int i = 0; i < number; i++) {
            final String name = NAMES.get(i);
            final DemoObject_create fs = new DemoObject_create().setName(name);
            ec.executeChild(this, fs.getName(), fs);
            demoObjects.add(fs.getDemoObject());
        }
    }

}
