package com.danhaywood.testsupport.coverage;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class PrivateConstructorTesterTest {

	public static class Constants {
		private static boolean called;
		private Constants() {
			// this is where we want some coverage!
			called = true;
		}
		
		public final static int FOO = 1;
		public final static int BAR = 1;
	}
	
	@Test
	public void invokeConstructor() throws Exception {
		assertThat(Constants.called, is(false));
		new PrivateConstructorTester(Constants.class).exercise();
		assertThat(Constants.called, is(true));
	}
	
}
