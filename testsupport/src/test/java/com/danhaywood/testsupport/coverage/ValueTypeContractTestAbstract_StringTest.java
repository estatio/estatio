package com.danhaywood.testsupport.coverage;

import java.util.Arrays;
import java.util.List;

import com.danhaywood.testsupport.coverage.ValueTypeContractTestAbstract;

public class ValueTypeContractTestAbstract_StringTest extends ValueTypeContractTestAbstract<String> {

    @Override
    protected List<String> getObjectsWithSameValue() {
        return Arrays.asList(new String("1"), new String("1"));
    }

    @Override
    protected List<String> getObjectsWithDifferentValue() {
        return Arrays.asList(new String("1   "), new String("   1"), new String("2"));
    }

}
