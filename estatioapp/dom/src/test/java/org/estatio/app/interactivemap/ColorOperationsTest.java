package org.estatio.app.interactivemap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class ColorOperationsTest {

    @Test
    public void test() {
        assertThat(ColorUtils.HSLtoRGB(250f, 50f, 25f), is("#2b2060"));
    }

}
