package com.danhaywood.testsupport.jmock;

import junit.framework.AssertionFailedError;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.ClassUnderTest;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Mode;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Never;

public class JUnitRuleMockery2Test_mockAnnotatedWithNever_sadCase {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Never
    @Mock
    private Collaborator collaborator;

    @ClassUnderTest
	private Collaborating collaborating;

    @Before
	public void setUp() throws Exception {
    	collaborating = (Collaborating) context.getClassUnderTest();
	}
    
    @Test(expected=java.lang.AssertionError.class)
    public void invocationOnCollaboratorIsIgnored() {
    	collaborating.collaborateWithCollaborator();
    }

}
