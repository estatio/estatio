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

import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SirenServiceTest {
    public static String COMPANY_QUERY = "APPLE FRANCE";
    public static String COMPANY_NAME = "APPLE FRANCE";
    public static String COMPANY_CODE = "322120916";

    @Test
    @Ignore //This test can hinder a build when the service is down temporarily or returns no results
    public void company_query_should_return_company_code() throws Exception {
        // given
        SirenService sirenService = new SirenService();
        sirenService.bearerToken = sirenService.getBearerTokenFromKeyAndSecret("foo", "foo"); // this will fail, but test is ignored anyway

        // when
        List<SirenResult> results = null;
        try {
            results = sirenService.getChamberOfCommerceCodes(COMPANY_QUERY);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        }

        // then
        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(1);
        SirenResult result = results.get(0);
        assertThat(result.getChamberOfCommerceCode()).isEqualTo(COMPANY_CODE);
    }

    @Test
    @Ignore //This test can hinder a build when the service is down temporarily or returns no results
    public void company_code_should_return_company_name() throws Exception {
        // given
        SirenService sirenService = new SirenService();
        sirenService.bearerToken = sirenService.getBearerTokenFromKeyAndSecret("foo", "foo"); // this will fail, but test is ignored anyway

        // when
        SirenResult result = null;
        try {
            result = sirenService.getCompanyName(COMPANY_CODE);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        }

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCompanyName()).isEqualTo(COMPANY_NAME);
    }

    @Test
    @Ignore //This test can hinder a build when the service is down temporarily or returns no results
    public void full_circle() throws Exception {
        // given
        SirenService sirenService = new SirenService();
        sirenService.bearerToken = sirenService.getBearerTokenFromKeyAndSecret("foo", "foo"); // this will fail, but test is ignored anyway

        // when
        List<SirenResult> codeResults = null;
        try {
            codeResults = sirenService.getChamberOfCommerceCodes(COMPANY_QUERY);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        }

        // then
        assertThat(codeResults.size()).isEqualTo(1);
        SirenResult codeResult = codeResults.get(0);
        assertThat(codeResult.getChamberOfCommerceCode()).isEqualTo(COMPANY_CODE);

        // when
        SirenResult companyNameResult = null;
        try {
            companyNameResult = sirenService.getCompanyName(codeResult.getChamberOfCommerceCode());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        }

        // then
        assertThat(companyNameResult.getCompanyName()).isEqualTo(COMPANY_NAME);
    }
}
