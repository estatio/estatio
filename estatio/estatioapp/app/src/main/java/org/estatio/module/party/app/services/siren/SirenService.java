/**
 * Copyright 2015-2016 Eurocommercial Properties NV
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.estatio.module.party.app.services.siren;

import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.glassfish.tyrus.core.uri.internal.UriComponent;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "siren.SirenService")
public class SirenService {

    private static final String BASE_URI = "https://api.insee.fr/entreprises/sirene/V3/siren";
    private static final String MEMBER_KEY_SEARCH_BY_ORGANISATION_NAME = "unitesLegales";
    private static final String MEMBER_KEY_SEARCH_BY_SIREN = "uniteLegale";

    String bearerToken;

    @PostConstruct
    public void init(final Map<String, String> properties) throws ClientProtocolException {
        final String consumerKey = properties.get("estatio.siren.consumerKey");
        final String consumerSecret = properties.get("estatio.siren.consumerSecret");

        bearerToken = (consumerKey != null && consumerSecret != null) ?
                getBearerTokenFromKeyAndSecret(consumerKey, consumerSecret) :
                null;
    }

    String getBearerTokenFromKeyAndSecret(final String consumerKey, final String consumerSecret) throws ClientProtocolException {
        final String authString = Base64.getEncoder().encodeToString((consumerKey + ":" + consumerSecret).getBytes());

        URI uri = UriBuilder.fromUri("https://api.insee.fr/token").build();

        RequestBuilder builder = RequestBuilder.post()
                .setHeader(HttpHeaders.AUTHORIZATION, "Basic ".concat(authString))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .setEntity(new StringEntity("grant_type=client_credentials", ContentType.APPLICATION_FORM_URLENCODED));

        try {
            JsonObject jsonObject = executeForRequestBuilderAndUri(builder, uri).getAsJsonObject();
            final String tokenType = jsonObject.get("token_type").getAsString();
            final String accessToken = jsonObject.get("access_token").getAsString();

            return tokenType.concat(" ").concat(accessToken);
        } catch (ClientProtocolException e) {
            throw e;
        } catch (Exception e) {
            // too bad
            return null;
        }
    }

    public List<SirenResult> getChamberOfCommerceCodes(String query) throws ClientProtocolException {
        // should not happen, but let's safeguard anyway
        if (bearerToken == null) {
            throw new ClientProtocolException("Bearer token for Siren API authorization is null");
        }

        UriBuilder uriBuilder = UriBuilder
                .fromUri(BASE_URI)
                .queryParam(
                        "q",
                        "periode(denominationUniteLegale:\"" +
                                UriComponent.encode(query, UriComponent.Type.QUERY_PARAM_SPACE_ENCODED) +
                                "\")"
                )
                .queryParam( // just return organisation name and siren (chamber of commerce) code
                        "champs",
                        "denominationUniteLegale,siren"
                );

        URI uri = uriBuilder.build();

        try {
            RequestBuilder builder = RequestBuilder.get()
                    .setHeader(HttpHeaders.AUTHORIZATION, bearerToken);

            JsonArray jsonArray = executeForRequestBuilderAndUri(builder, uri)
                    .getAsJsonArray(MEMBER_KEY_SEARCH_BY_ORGANISATION_NAME);

            return getSirenResultsFromJsonArray(jsonArray);
        } catch (ClientProtocolException e) {
            // throw for message service to warn user
            throw e;
        } catch (Exception e) {
            // failed for whatever reason, ignore
            return Collections.emptyList();
        }
    }

    public SirenResult getCompanyName(String chamberOfCommerceCode) throws ClientProtocolException {
        // should not happen, but let's safeguard anyway
        if (bearerToken == null) {
            throw new ClientProtocolException("Bearer token for Siren API authorization is null");
        }

        UriBuilder uriBuilder = UriBuilder
                .fromUri(BASE_URI)
                .path(chamberOfCommerceCode)
                .queryParam( // just return organisation name and siren (chamber of commerce) code
                        "champs",
                        "denominationUniteLegale,siren"
                );

        URI uri = uriBuilder.build();

        try {
            RequestBuilder builder = RequestBuilder.get()
                    .setHeader(HttpHeaders.AUTHORIZATION, bearerToken);

            JsonObject jsonObject = executeForRequestBuilderAndUri(builder, uri)
                    .getAsJsonObject(MEMBER_KEY_SEARCH_BY_SIREN);

            return jsonObjectToSirenResult(jsonObject);
        } catch (ClientProtocolException e) {
            // throw for message service to warn user
            throw e;
        } catch (Exception e) {
            // failed for whatever reason, ignore
            return null;
        }
    }

    private JsonObject executeForRequestBuilderAndUri(RequestBuilder builder, URI uri) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial((chain, authType) -> true).build();

        SSLConnectionSocketFactory sslConnectionSocketFactory =
                new SSLConnectionSocketFactory(sslContext, new String[]
                        { "SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2" }, null,
                        NoopHostnameVerifier.INSTANCE);

        CloseableHttpClient client = HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build();

        HttpUriRequest request = builder.setUri(uri).build();

        HttpResponse response = client.execute(request);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            // failed for whatever reason
            return new JsonObject();
        }

        HttpEntity entity = response.getEntity();
        String jsonAsString = EntityUtils.toString(entity);

        JsonElement jsonElement = new JsonParser().parse(jsonAsString);
        return jsonElement.getAsJsonObject();
    }

    private List<SirenResult> getSirenResultsFromJsonArray(JsonArray jsonArray) {
        Set<SirenResult> resultsSet = new TreeSet<>();
        Iterator<JsonElement> recordsIterator = jsonArray.iterator();

        recordsIterator.forEachRemaining(record -> {
            try {
                SirenResult result = jsonObjectToSirenResult(record.getAsJsonObject());
                resultsSet.add(result);
            } catch (Exception e) {
                // ignore
            }
        });

        return Lists.newArrayList(resultsSet);
    }

    private SirenResult jsonObjectToSirenResult(JsonObject object) {
        String code = object.getAsJsonPrimitive("siren").getAsString();
        JsonArray array = object.getAsJsonArray("periodesUniteLegale");
        object = array.get(0).getAsJsonObject(); // first element contains most recent change (or first entry in registry)
        String name = object.get("denominationUniteLegale").getAsString();
        LocalDate date = LocalDate.parse(object.get("dateDebut").getAsString());

        return new SirenResult(code, name, date);
    }
}
