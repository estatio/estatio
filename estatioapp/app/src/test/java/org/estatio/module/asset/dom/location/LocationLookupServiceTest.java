package org.estatio.module.asset.dom.location;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.isisaddons.wicket.gmap3.cpt.applib.Location;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

public class LocationLookupServiceTest {

    private LocationLookupService locationLookupService;

    @Before
    public void setup() {
        assumeThat(isInternetReachable(), is(true));

        locationLookupService = new LocationLookupService();
    }

    @Ignore // this is ignored only because it's a dependency on an external dependency; it *does* work (or did, the last time we tried ... :-)
    @Test
    public void whenValid() {
        Location location = locationLookupService.lookup("10 Downing Street,London,UK");
        assertThat(location, is(not(nullValue())));
        assertEquals(51.503, location.getLatitude(), 0.01);
        assertEquals(-0.128, location.getLongitude(), 0.01);
    }

    @Ignore // this is ignored only because it's a dependency on an external dependency; it *does* work (or did, the last time we tried ... :-)
    @Test
    public void whenInvalid() {
        Location location = locationLookupService.lookup("$%$%^Y%^fgnsdlfk glfg");
        assertThat(location, is(nullValue()));
    }

    /**
     * Tries to retrieve some content, 1 second timeout.
     */
    private static boolean isInternetReachable()
    {
        try {
            final URL url = new URL("http://www.google.com");
            final HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();
            urlConnect.setConnectTimeout(1000);
            urlConnect.getContent();
            urlConnect.disconnect();
        } catch (UnknownHostException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }


}
