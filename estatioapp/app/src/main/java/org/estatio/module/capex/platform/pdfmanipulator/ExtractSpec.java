package org.estatio.module.capex.platform.pdfmanipulator;

import java.util.TreeSet;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.Data;

/**
 * Which pages to extract from the original PDF.
 */
@Data
public class ExtractSpec {

    final int numFirstPages;
    final int numLastPages;

    public Integer[] pageNumbersFor(int numPagesInTotal) {
        final TreeSet<Integer> pages = Sets.newTreeSet();

        for (int i = 0; i < numFirstPages && i < numPagesInTotal; i++) {
            final int pageFromStart = i;
            pages.add(pageFromStart);
        }

        for (int i = 0; i < numLastPages && i < numPagesInTotal; i++) {
            final int pageFromEnd = numPagesInTotal - i - 1;
            pages.add(pageFromEnd);
        }

        return Lists.newArrayList(pages).toArray(new Integer[0]);
    }

    public static ExtractSpec ALL_PAGES = new ExtractSpec(Integer.MAX_VALUE, Integer.MAX_VALUE);
    public static ExtractSpec FIRST_PAGE_ONLY = new ExtractSpec(1, 0);

}
