package org.estatio.dom;

import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;

public class Dflt {

    private Dflt(){}

    @Programmatic
    public static <T> T of(final List<T> choices) {
        switch(choices.size()) {
            case 0: return null;
            case 1: return choices.get(0);
            default: return null;
        }
    }
}
