package org.incode.module.document.dom.services;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

public class ClassNameServiceAbstract_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    ClassNameServiceAbstract service;

    public static class SomeClass {}
    public static class SomeClassSubtypeA extends SomeClass {}
    public static class SomeClassSubtypeB extends SomeClass {}

    @Before
    public void setUp() throws Exception {
        service = new ClassNameServiceAbstract(SomeClass.class, SomeClass.class.getPackage().getName()){};
    }

    public static class asClass_Test extends ClassNameServiceAbstract_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {


        }


    }

    public static class classNames_Test extends ClassNameServiceAbstract_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {


        }

        @Ignore
        @Test
        public void with_predicate() throws Exception {


        }


    }



}