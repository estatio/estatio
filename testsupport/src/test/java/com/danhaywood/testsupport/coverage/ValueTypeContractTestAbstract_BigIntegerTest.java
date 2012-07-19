package com.danhaywood.testsupport.coverage;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import com.danhaywood.testsupport.coverage.ValueTypeContractTestAbstract;

public class ValueTypeContractTestAbstract_BigIntegerTest extends ValueTypeContractTestAbstract<BigInteger> {

    @Override
    protected List<BigInteger> getObjectsWithSameValue() {
        return Arrays.asList(new BigInteger("1"), new BigInteger("1"));
    }

    @Override
    protected List<BigInteger> getObjectsWithDifferentValue() {
        return Arrays.asList(new BigInteger("2"));
    }

}
