package org.incode.module.document.dom.impl.paperclips;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

public class Paperclip_changeRole_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private Paperclip mockPaperclip;

    Paperclip_changeRole mixin;

    @Before
    public void setUp() throws Exception {
        // when
        mixin = new Paperclip_changeRole(mockPaperclip);
    }

    public static class Default_Test extends Paperclip_changeRole_Test {

        @Ignore
        @Test
        public void xxx() throws Exception {

        }

    }

    public static class ActionInvocation_Test extends Paperclip_changeRole_Test {

        @Ignore
        @Test
        public void xxx() throws Exception {

        }
    }


}