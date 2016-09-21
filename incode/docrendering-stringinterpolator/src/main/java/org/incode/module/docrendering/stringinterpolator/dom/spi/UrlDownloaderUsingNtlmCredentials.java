package org.incode.module.docrendering.stringinterpolator.dom.spi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.config.ConfigurationService;

@DomainService(nature = NatureOfService.DOMAIN, menuOrder = "100")
public class UrlDownloaderUsingNtlmCredentials implements UrlDownloaderService {

    public static final String PREFIX = "incode.module.docrendering.stringinterpolator.UrlDownloaderUsingNtlmCredentials.";

    private String user;
    private String password;
    private String ntDomain;

    private String host;

    String workstation;

    CloseableHttpClient httpclient;
    CredentialsProvider credsProvider;

    @PostConstruct
    public void init() throws UnknownHostException {

        final String fullyQualifiedUser = configurationService.getProperty(PREFIX + "user");
        final List<String> userParts = Splitter.on(slash()).splitToList(fullyQualifiedUser);
        this.ntDomain = userParts.get(0);
        this.user = userParts.get(1);

        password = configurationService.getProperty(PREFIX + "password");
        host = configurationService.getProperty(PREFIX + "host");

        workstation = InetAddress.getLocalHost().getHostName();

        // thread-safe according to HTTP Client
        httpclient = HttpClientBuilder.create().build();
        // implementations are required to be thread-safe, apparently
        credsProvider = new BasicCredentialsProvider();

        // immutable, so okay to reuse
        final NTCredentials credentials = new NTCredentials(user, password, workstation, ntDomain);

        credsProvider.setCredentials(AuthScope.ANY, credentials);
    }

    private static CharMatcher slash() {
        return new CharMatcher() {
            @Override
            public boolean matches(final char c) {
                return c == '\\' || c == '/';
            }
        };
    }

    @PreDestroy
    public void destroy() {
        try {
            httpclient.close();
        } catch (IOException e) {
            // ignore
        }
    }

    @Override
    public boolean canDownload(final URL url) {
        return isConfigured() && Objects.equals(host, url.getHost());
    }

    private boolean isConfigured() {
        return user != null && password != null && ntDomain != null && host != null;
    }

    @Override
    public byte[] download(final URL url) throws IOException {

        HttpHost target = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());

        // Make sure the same context is used to execute logically related requests
        // (not thread-safe, so need a new one each time)
        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);

        HttpGet httpGet = new HttpGet(url.getFile());
        try (final CloseableHttpResponse httpResponse = httpclient.execute(target, httpGet, context)) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            httpResponse.getEntity().writeTo(baos);
            return baos.toByteArray();
        }
    }


    @Inject
    ConfigurationService configurationService;
}
