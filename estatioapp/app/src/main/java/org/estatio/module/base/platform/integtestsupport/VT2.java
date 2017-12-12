package org.estatio.module.base.platform.integtestsupport;

import java.math.BigInteger;

import org.incode.module.base.integtests.VT;

public class VT2 {
    private VT2(){}
    public static BigInteger bi(Integer val) {
            return val != null ? VT.bi(val.intValue()) : null;
        }
}
