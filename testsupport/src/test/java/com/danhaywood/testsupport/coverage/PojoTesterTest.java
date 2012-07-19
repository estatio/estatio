package com.danhaywood.testsupport.coverage;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.junit.Test;

import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;

public class PojoTesterTest {

	private PojoTester pojoTester;
	private SpyingPojo spyingPojo;
	private SpyingPojoExt spyingPojoExt;

	private final int NUMBER_OF_GETTERS_AND_SETTERS = 56;

	@Before
	public void setUp() throws Exception {
		pojoTester = new PojoTester();
		spyingPojo = new SpyingPojo();
		spyingPojoExt = new SpyingPojoExt();
	}

	@Test
	public void happyCase() {
		pojoTester.exercise(spyingPojo);
		assertThat(spyingPojo.methodInvocations.size(), is(NUMBER_OF_GETTERS_AND_SETTERS)); // for each getter/setter
	}

	@Test
	public void includingOnly() {
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("z", "longWrapper"));
		assertThat(spyingPojo.methodInvocations.size(), is(4)); // for each getter/setter
	}

	@Test
	public void excluding() {
		pojoTester.exercise(spyingPojo, FilterSet.excluding("z", "longWrapper"));
		assertThat(spyingPojo.methodInvocations.size(), is(NUMBER_OF_GETTERS_AND_SETTERS - 4)); // for each getter/setter
	}

	@Test
	public void withOtherClass_fixtureDataSupplied() {
		pojoTester.withFixture(SpyingPojoExt.OtherClass.class, new SpyingPojoExt.OtherClass(1), new SpyingPojoExt.OtherClass(2));
		
		pojoTester.exercise(spyingPojoExt, FilterSet.includingOnly("otherClass"));
		assertThat(spyingPojoExt.methodInvocations.size(), is(2)); // for each getter/setter
	}

	@Test(expected=AssertionFailedError.class)
	public void withOtherClass_noFixtureDataSupplied_atAll() {
		pojoTester.exercise(spyingPojoExt, FilterSet.includingOnly("otherClass"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void withOtherClass_noFixtureDataSupplied_missingValues() {
		pojoTester.withFixture(SpyingPojoExt.OtherClass.class);
		pojoTester.exercise(spyingPojoExt, FilterSet.includingOnly("otherClass"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void attemptToSupplyForEnum() {
		pojoTester.withFixture(SpyingPojo.MyEnum.class, SpyingPojo.MyEnum.ONE, SpyingPojo.MyEnum.TWO);
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken1() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("z"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken2() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("b"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken3() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("s"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken4() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("i"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken5() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("l"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken6() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("f"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken7() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("d"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken8() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("c"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken9() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("boolWrapper"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken10() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("byteWrapper"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken11() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("shortWrapper"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken12() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("intWrapper"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken13() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("longWrapper"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken14() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("floatWrapper"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken15() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("doubleWrapper"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken16() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("charWrapper"));
	}


	@Test(expected=AssertionFailedError.class)
	public void whenBroken17() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("string"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken18() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("pattern"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken19() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("file"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken20() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("bigDecimal"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken21() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("bigInteger"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken22() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("javaUtilDate"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken23() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("timestamp"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken24() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("list"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken25() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("iterable"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken26() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("collection"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken27() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("set"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken28() {
		spyingPojo.broken = true;
		pojoTester.exercise(spyingPojo, FilterSet.includingOnly("myEnum"));
	}

	@Test(expected=AssertionFailedError.class)
	public void whenBroken_withOtherClass_fixtureDataSupplied() {
		spyingPojoExt.broken = true;
		pojoTester.withFixture(SpyingPojoExt.OtherClass.class, new SpyingPojoExt.OtherClass(1), new SpyingPojoExt.OtherClass(2));
		
		pojoTester.exercise(spyingPojoExt, FilterSet.includingOnly("otherClass"));
	}



	////////////////
	
	public static class SpyingPojo {

		public enum MyEnum {
			ONE,TWO,THREE
		}
		
		private boolean z;
		private byte b;
		private short s;
		private int i;
		private long l;
		private float f;
		private double d;
		private char c;

		private Boolean boolWrapper;
		private Byte byteWrapper;
		private Short shortWrapper;
		private Integer intWrapper;
		private Long longWrapper;
		private Float floatWrapper;
		private Double doubleWrapper;
		private Character charWrapper;

		private String string;
		private Pattern pattern;
		private File file;
		private BigDecimal bigDecimal;
		private BigInteger bigInteger;
		private java.util.Date javaUtilDate;
		private Timestamp timestamp;
		private List<?> list;
		private Iterable<?> iterable;
		private Collection<?> collection;
		private Set<?> set;
		
		private MyEnum myEnum;

		Set<String> methodInvocations = new HashSet<String>();
		boolean broken;
		
		public boolean isZ() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return z;
		}
		public void setZ(boolean z) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
				this.z = z;
		}
		public byte getB() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return b;
		}
		public void setB(byte b) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
				this.b = b;
		}
		public short getS() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return s;
		}
		public void setS(short s) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.s = s;
		}
		public int getI() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return i;
		}
		public void setI(int i) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.i = i;
		}
		public long getL() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return l;
		}
		public void setL(long l) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.l = l;
		}
		public float getF() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return f;
		}
		public void setF(float f) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.f = f;
		}
		public double getD() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return d;
		}
		public void setD(double d) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.d = d;
		}
		public char getC() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return c;
		}
		public void setC(char c) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.c = c;
		}

		public Boolean getBoolWrapper() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return boolWrapper;
		}
		public void setBoolWrapper(Boolean zWrapper) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.boolWrapper = zWrapper;
		}
		public Byte getByteWrapper() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return byteWrapper;
		}
		public void setByteWrapper(Byte bWrapper) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.byteWrapper = bWrapper;
		}
		public Short getShortWrapper() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return shortWrapper;
		}
		public void setShortWrapper(Short sv) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.shortWrapper = sv;
		}
		public Integer getIntWrapper() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return intWrapper;
		}
		public void setIntWrapper(Integer iWrapper) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.intWrapper = iWrapper;
		}
		public Long getLongWrapper() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return longWrapper;
		}
		public void setLongWrapper(Long lWrapper) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.longWrapper = lWrapper;
		}
		public Float getFloatWrapper() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return floatWrapper;
		}
		public void setFloatWrapper(Float fWrapper) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.floatWrapper = fWrapper;
		}
		public Double getDoubleWrapper() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return doubleWrapper;
		}
		public void setDoubleWrapper(Double dWrapper) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.doubleWrapper = dWrapper;
		}
		public Character getCharWrapper() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return charWrapper;
		}
		public void setCharWrapper(Character cWrapper) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.charWrapper = cWrapper;
		}
		public String getString() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return string;
		}
		public void setString(String string) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.string = string;
		}
		public Pattern getPattern() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return pattern;
		}
		public void setPattern(Pattern pattern) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.pattern = pattern;
		}
		public File getFile() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return file;
		}
		public void setFile(File file) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.file = file;
		}
		public BigDecimal getBigDecimal() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return bigDecimal;
		}
		public void setBigDecimal(BigDecimal bigDecimal) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.bigDecimal = bigDecimal;
		}
		public BigInteger getBigInteger() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return bigInteger;
		}
		public void setBigInteger(BigInteger bigInteger) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.bigInteger = bigInteger;
		}
		public java.util.Date getJavaUtilDate() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return javaUtilDate;
		}
		public void setJavaUtilDate(java.util.Date javaUtilDate) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.javaUtilDate = javaUtilDate;
		}
		public Timestamp getTimestamp() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return timestamp;
		}
		public void setTimestamp(Timestamp timestamp) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.timestamp = timestamp;
		}
		public List<?> getList() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return list;
		}
		public void setList(List<?> list) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.list = list;
		}
		public Iterable<?> getIterable() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return iterable;
		}
		public void setIterable(Iterable<?> iterable) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.iterable = iterable;
		}
		public Collection<?> getCollection() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return collection;
		}
		public void setCollection(Collection<?> collection) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.collection = collection;
		}
		public Set<?> getSet() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return set;
		}
		public void setSet(Set<?> set) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.set = set;
		}
		public MyEnum getMyEnum() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return myEnum;
		}
		public void setMyEnum(MyEnum myEnum) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.myEnum = myEnum;
		}
	}

	public static class SpyingPojoExt extends SpyingPojo {

		public static class OtherClass {
			public OtherClass(int x) {
				this.x = x;
			}
			int x;
		}
		
		private OtherClass otherClass;

		public OtherClass getOtherClass() {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			return otherClass;
		}
		public void setOtherClass(OtherClass otherClass) {
			methodInvocations.add(Thread.currentThread().getStackTrace()[1].getMethodName());
			if(!broken)
			this.otherClass = otherClass;
		}
	}

}
