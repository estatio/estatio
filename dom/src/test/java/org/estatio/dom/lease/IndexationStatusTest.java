package org.estatio.dom.lease;

import static org.junit.Assert.*;

import org.junit.Test;

public class IndexationStatusTest {

    @Test
    public void isUpdatableTest() {
        assertFalse(IndexationStatus.PREVIOUS.isUpdatable());
        assertTrue(IndexationStatus.NEXT.isUpdatable());
    }

}
