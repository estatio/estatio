package org.estatio.fixture.index;

import java.math.BigDecimal;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.estatio.dom.index.Index;
import org.estatio.dom.index.IndexBase;
import org.estatio.dom.index.Indices;
import org.joda.time.LocalDate;


public class IndexFixture extends AbstractFixture {

    private Indices indices;

    public Indices getIndices() {
        return indices;
    }

    public void setIndices(Indices indices) {
        this.indices = indices;
    }

    @Override
    public void install() {

        Index index = createIndex("ISTAT-FOI", "ISTAT FOI");

        IndexBase base1989 = createIndexBase(index, null, 1990, 1.242);
        
        createIndexValues(base1989, 1990, new double[] { 103.3, 104, 104.4, 104.8, 105.1, 105.5, 105.9, 106.6, 107.2, 108.1, 108.8, 109.2 }, 106.1);
        createIndexValues(base1989, 1991, new double[] { 110, 111, 111.3, 111.8, 112.2, 112.8, 113, 113.3, 113.8, 114.7, 115.5, 115.8 }, 112.9);
        createIndexValues(base1989, 1992, new double[] { 116.7, 116.9, 117.4, 117.9, 118.5, 118.9, 119.1, 119.2, 119.6, 120.3, 121, 121.2 }, 118.9);

        IndexBase base1992 = createIndexBase(index,base1989, 1993, 1.189);
        createIndexValues(base1992, 1993, new double[] { 102.3, 102.7, 102.9, 103.3, 103.7, 104.2, 104.6, 104.7, 104.8, 105.5, 106, 106 }, 104.2);
        createIndexValues(base1992, 1994, new double[] { 106.6, 107, 107.2, 107.5, 107.9, 108.1, 108.4, 108.6, 108.9, 109.5, 109.9, 110.3 }, 108.3);
        createIndexValues(base1992, 1995, new double[] { 110.7, 111.6, 112.5, 113.1, 113.8, 114.4, 114.5, 114.9, 115.2, 115.8, 116.5, 116.7 }, 114.1);

        IndexBase base1995 = createIndexBase(index, base1992, 1996, 1.141);
        createIndexValues(base1995, 1996, new double[] { 102.4, 102.7, 103, 103.6, 104, 104.2, 104, 104.1, 104.4, 104.5, 104.8, 104.9 }, 103.9);
        createIndexValues(base1995, 1997, new double[] { 105.1, 105.2, 105.3, 105.4, 105.7, 105.7, 105.7, 105.7, 105.9, 106.2, 106.5, 106.5 }, 105.7);
        createIndexValues(base1995, 1998, new double[] { 106.8, 107.1, 107.1, 107.3, 107.5, 107.6, 107.6, 107.7, 107.8, 108, 108.1, 108.1 }, 107.6);
        createIndexValues(base1995, 1999, new double[] { 108.2, 108.4, 108.6, 109, 109.2, 109.2, 109.4, 109.4, 109.7, 109.9, 110.3, 110.4 }, 109.3);
        createIndexValues(base1995, 2000, new double[] { 110.5, 111, 111.3, 111.4, 111.7, 112.1, 112.3, 112.3, 112.5, 112.8, 113.3, 113.4 }, 112.1);
        createIndexValues(base1995, 2001, new double[] { 113.9, 114.3, 114.4, 114.8, 115.1, 115.3, 115.3, 115.3, 115.4, 115.7, 115.9, 116 }, 115.1);
        createIndexValues(base1995, 2002, new double[] { 116.5, 116.9, 117.2, 117.5, 117.7, 117.9, 118, 118.2, 118.4, 118.7, 119, 119.1 }, 117.9);
        createIndexValues(base1995, 2003, new double[] { 119.6, 119.8, 120.2, 120.4, 120.5, 120.6, 120.9, 121.1, 121.4, 121.5, 121.8, 121.8 }, 120.8);
        createIndexValues(base1995, 2004, new double[] { 122, 122.4, 122.5, 122.8, 123, 123.3, 123.4, 123.6, 123.6, 123.6, 123.9, 123.9 }, 123.2);
        createIndexValues(base1995, 2005, new double[] { 123.9, 124.3, 124.5, 124.9, 125.1, 125.3, 125.6, 125.8, 125.9, 126.1, 126.1, 126.3 }, 125.3);
        createIndexValues(base1995, 2006, new double[] { 126.6, 126.9, 127.1, 127.4, 127.8, 127.9, 128.2, 128.4, 128.4, 128.2, 128.3, 128.4 }, 127.8);
        createIndexValues(base1995, 2007, new double[] { 128.5, 128.8, 129, 129.2, 129.6, 129.9, 130.2, 130.4, 130.4, 130.8, 131.3, 131.8 }, 130);
        createIndexValues(base1995, 2008, new double[] { 132.2, 132.5, 133.2, 133.5, 134.2, 134.8, 135.4, 135.5, 135.2, 135.2, 134.7, 134.5 }, 134.2);
        createIndexValues(base1995, 2009, new double[] { 134.2, 134.5, 134.5, 134.8, 135.1, 135.3, 135.3, 135.8, 135.4, 135.5, 135.6, 135.8 }, 135.2);
        createIndexValues(base1995, 2010, new double[] { 136, 136.2, 136.5, 137, 137.1, 137.1, 137.6, 137.9, 137.5, 137.8, 137.9, 138.4 }, 137.3);

        IndexBase base2000 = createIndexBase(index, base1995, 2011, 1.373);
        createIndexValues(base2000, 2011, new double[] { 101.2, 101.5, 101.9, 102.4, 102.5, 102.6, 102.9, 103.2, 103.2, 103.6, 103.7, 104 }, 102.7);
        createIndexValues(base2000, 2012, new double[] { 104.4, 104.8, 105.2, 105.7, 105.6, 105.8, 105.9, 106.4, 106.4, 106.4, 106.2, 106.5 }, 0);
    }

    private Index createIndex(String reference, String name) {
        return indices.newIndex(reference, name);
    }

    private IndexBase createIndexBase(Index index, IndexBase previousBase, int year, double factor) {
        return indices.newIndexBase(index, previousBase, new LocalDate(year, 1, 1), BigDecimal.valueOf(factor));
    }

    private void createIndexValues(IndexBase indexBase, int year, double[] values, double average) {
        int i = 0;
        for (double value : values) {
            indices.newIndexValue(indexBase, new LocalDate(year, i + 1, 1), new LocalDate(year, i + 1, 1).dayOfMonth().withMaximumValue(), BigDecimal.valueOf(value));
            i++;
        }
    }
}
