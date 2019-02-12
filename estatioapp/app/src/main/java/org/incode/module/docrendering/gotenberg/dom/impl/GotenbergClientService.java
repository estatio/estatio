package org.incode.module.docrendering.gotenberg.dom.impl;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

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
import org.apache.isis.applib.annotation.Programmatic;

@DomainService(nature = NatureOfService.DOMAIN)
public class GotenbergClientService {

    private String url;

    @PostConstruct
    public void init(Map<String,String> properties) {
        url = properties.getOrDefault(
                "estatio.application.gotenbergUrl", "http://gotenberg:3000/convert/office");
    }

    @Programmatic
    public byte[] convertToPdf(final byte[] docxBytes) {
        return convertToPdf(docxBytes, url);
    }

    @Programmatic
    public byte[] convertToPdf(
            final byte[] docxBytes,
            final String url) {

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final HttpPost httpPost = new HttpPost(url);

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
}
