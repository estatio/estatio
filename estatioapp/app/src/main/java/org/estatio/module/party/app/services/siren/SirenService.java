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

import java.net.ConnectException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SirenService {
    public List<SirenResult> getChamberOfCommerceCodes(String query) throws ConnectException {
        UriBuilder uriBuilder = UriBuilder
                .fromUri("https://data.opendatasoft.com")
                .path("api/records/1.0/search/")
                .queryParam("dataset", "sirene@public")
                .queryParam("q", "{query}");
        URI uri = uriBuilder.build("\"" + query + "\"");
        Response response = sendGetRequest(uri);

        return getSirenResultsFromResponse(response);
    }

    public SirenResult getCompanyName(String chamberOfCommerceCode) throws ConnectException{
        UriBuilder uriBuilder = UriBuilder
                .fromUri("https://data.opendatasoft.com")
                .path("api/records/1.0/search/")
                .queryParam("dataset", "sirene@public")
                .queryParam("refine.siren", "{code}");
        URI uri = uriBuilder.build(chamberOfCommerceCode);
        Response response = sendGetRequest(uri);

        List<SirenResult> results = getSirenResultsFromResponse(response);
        return results.size() > 0 ? results.get(0) : null;
    }

    private Response sendGetRequest(final URI uri) {
        final Client client = ClientBuilder.newBuilder().build();
        try {
            final WebTarget webTarget = client.target(uri);
            return webTarget.request().get();
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (Exception ex) {
                    // ignore
                }
            }
        }
    }

    private List<SirenResult> getSirenResultsFromResponse(Response response) {
        JsonArray records = getRecordsArray(response);

        Set<SirenResult> resultsSet = new TreeSet<>();
        Iterator<JsonElement> recordsIterator = records.iterator();
        while(recordsIterator.hasNext()) {
            try {
                JsonElement element = recordsIterator.next();
                JsonObject object = element.getAsJsonObject();
                object = object.getAsJsonObject("fields");
                String code = object.getAsJsonPrimitive("siren").getAsString();
                String name = object.getAsJsonPrimitive("l1_normalisee").getAsString();
                SirenResult result = new SirenResult(code, name);
                resultsSet.add(result);
            } catch(Exception e) {
                // ignore
            }
        }
        return Lists.newArrayList(resultsSet);
    }

    private JsonArray getRecordsArray(Response response) {
        String responseJson = response.readEntity(String.class);
        JsonElement jsonElement = new JsonParser().parse(responseJson);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return jsonObject.getAsJsonArray("records");
    }
}
