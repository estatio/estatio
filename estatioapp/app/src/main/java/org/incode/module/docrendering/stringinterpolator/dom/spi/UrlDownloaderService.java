package org.incode.module.docrendering.stringinterpolator.dom.spi;

import java.io.IOException;
import java.net.URL;

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

public interface UrlDownloaderService {

    @Programmatic
    public byte[] download(URL url) throws IOException;

    boolean canDownload(URL url);

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SimpleUsingGuava implements UrlDownloaderService {

        @Override
        public byte[] download(final URL url) throws IOException {
            final ByteSource byteSource = Resources.asByteSource(url);
            return byteSource.read();
        }

        @Override
        public boolean canDownload(final URL url) {
            return true;
        }
    }

}
