package org.incode.module.docrendering.gotenberg.dom.impl;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.document.dom.impl.renderers.RendererFromBytesToBytes;
import org.incode.module.document.dom.impl.types.DocumentType;

public abstract class RendererForGotenbergDocxToPdfAbstract implements RendererFromBytesToBytes {

    /**
     * Expected to return a <code>.docx</code>.
     */
    private final RendererFromBytesToBytes renderBytesToBytes;

    protected RendererForGotenbergDocxToPdfAbstract(final RendererFromBytesToBytes renderBytesToBytes) {
        this.renderBytesToBytes = renderBytesToBytes;
    }

    @Override
    public byte[] renderBytesToBytes(
            final DocumentType documentType,
            final String variant,
            final String atPath,
            final long templateVersion,
            final byte[] templateBytes,
            final Object dataModel) throws IOException {
        final byte[] docx = renderBytesToBytes.renderBytesToBytes(documentType, variant, atPath, templateVersion, templateBytes, dataModel);
        return convertToPdf(docx);
    }

    protected byte[] convertToPdf(final byte[] docxBytes) throws RuntimeException {
        final String uri = config.getUrl();

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final HttpPost httpPost = new HttpPost(uri);

            ContentBody bin = new ByteArrayBody(docxBytes, "dummy.docx");

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("files", bin)
                    .build();

            httpPost.setEntity(reqEntity);

            try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
                HttpEntity resEntity = response.getEntity();
                return resEntity != null ? EntityUtils.toByteArray(resEntity) : null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Inject
    Config config;

    @Inject
    public void setServiceRegistry2(final ServiceRegistry2 serviceRegistry2) {
        this.serviceRegistry2 = serviceRegistry2;
        this.serviceRegistry2.injectServicesInto(this.renderBytesToBytes);
    }
    ServiceRegistry2 serviceRegistry2;


    @DomainService(nature = NatureOfService.DOMAIN)
    public static class Config {

        @PostConstruct
        public void init(Map<String,String> properties) {
            url = properties
                    .getOrDefault("estatio.application.gotenbergUrl", "http://gotenberg:3000/convert/office");
        }
        String url;

        public String getUrl() {
            return url;
        }
    }

}
