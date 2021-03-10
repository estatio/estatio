package org.incode.module.docrendering.gotenberg.fixture.fake;

import java.util.Map;

import javax.annotation.PostConstruct;

import com.google.common.io.Resources;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.docrendering.gotenberg.dom.impl.GotenbergClientService;

import lombok.SneakyThrows;

@DomainService(
        nature = NatureOfService.DOMAIN,
        menuOrder = "" + (Integer.MAX_VALUE - 200 )
)
public class FakeGotenbergClientService extends GotenbergClientService {

    @PostConstruct
    public void init(Map<String,String> properties) {
    }

    @Programmatic
    public byte[] convertToPdf(final byte[] docxBytes) {
        return readDummyBytes();
    }

    @Programmatic
    public byte[] convertToPdf(
            final byte[] docxBytes,
            final String url) {

        return readDummyBytes();
    }

    @SneakyThrows
    private byte[] readDummyBytes() {
        return Resources.toByteArray(Resources.getResource(getClass(), "dummy.pdf"));
    }

}
