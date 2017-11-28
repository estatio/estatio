package org.isisaddons.module.base.platform.fixturesupport;

import org.apache.isis.applib.fixturescripts.FixtureScript;

public interface EnumWithFixtureScript<T, F extends FixtureScript> {

    F toFixtureScript();

}

