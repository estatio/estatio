package org.estatio.dom.project;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ProgramRoleTypeTest {

	@Test
	public void test() {
		assertTrue(ProgramRoleType.PROGRAM_BOARDMEMBER.isAllowMultilple());
	}
	
	

}
