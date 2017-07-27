package org.estatio.capex.dom.pdfmanipulator;

import java.util.Arrays;
import java.util.Collection;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import org.estatio.capex.dom.pdfmanipulator.ExtractSpec;

@RunWith(Parameterized.class)
public class ExtractSpecTest_pagesFor {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { 1, 0, 1, arr(0) },
                { 1, 1, 1, arr(0) },
                { 1, 0, 2, arr(0) },
                { 1, 1, 2, arr(0,1) },
                { 1, 1, 3, arr(0,2) },
                { 2, 1, 3, arr(0,1,2) },
                { 1, 2, 3, arr(0,1,2) },
                { 2, 2, 3, arr(0,1,2) },
                { 3, 2, 3, arr(0,1,2) },
                { 4, 2, 3, arr(0,1,2) },
                { 1, 3, 3, arr(0,1,2) },
                { 1, 4, 3, arr(0,1,2) },
                { 3, 1, 6, arr(0,1,2,5) },
        });
    }

    private static Integer[] arr(final Integer... integers) {
        return integers;
    }

    private final int numFirstPages;
    private final int numLastPages;
    private final int sizeOfDoc;
    private final Integer[] expected;

    public ExtractSpecTest_pagesFor(
            final int numFirstPages,
            final int numLastPages,
            final int sizeOfDoc,
            final Integer[] expected) {
        this.numFirstPages = numFirstPages;
        this.numLastPages = numLastPages;
        this.sizeOfDoc = sizeOfDoc;
        this.expected = expected;
    }

    @Test
    public void exercise() throws Exception {
        Assertions.assertThat(new ExtractSpec(numFirstPages, numLastPages).pageNumbersFor(sizeOfDoc)).containsExactly(expected);
    }

}