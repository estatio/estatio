package com.danhaywood.testsupport.jmock;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Checking;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.ClassUnderTest;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.ExpectationsOn;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Ignoring;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Mode;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Never;

public class JUnitRuleMockery2Test_mockAnnotatedWithChecking {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    public static class ExpectCall extends ExpectationsOn<Collaborator> {
    	public ExpectCall(Object mock) {
			super(mock);
		}
	{
    	one(mock()).doOtherStuff();
    }}

    @Checking(ExpectCall.class)
    @Mock
    private Collaborator collaborator;

    @ClassUnderTest
	private Collaborating collaborating;

    @Before
	public void setUp() throws Exception {
    	collaborating = (Collaborating) context.getClassUnderTest();
	}
    
    @Test
    public void invocationOnCollaboratorIsIgnored() {
    	collaborating.collaborateWithCollaborator();
    }
}
