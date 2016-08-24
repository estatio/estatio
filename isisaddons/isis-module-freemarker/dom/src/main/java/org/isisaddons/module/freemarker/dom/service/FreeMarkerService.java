/*
 *  Copyright 2016 Dan Haywood
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.isisaddons.module.freemarker.dom.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.config.ConfigurationService;

import org.isisaddons.module.freemarker.dom.spi.FreeMarkerTemplateLoader;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

@DomainService(nature = NatureOfService.DOMAIN)
public class FreeMarkerService {

    private Configuration cfg;

    @PostConstruct
    public void init() {
        cfg = new Configuration(Configuration.VERSION_2_3_25);
        cfg.setDefaultEncoding("UTF-8");

        final String deploymentType = configurationService.getProperty("isis.deploymentType");
        final boolean isPrototyping = deploymentType == null || deploymentType.contains("prototyping");
        final TemplateExceptionHandler handler = isPrototyping
                        ? TemplateExceptionHandler.HTML_DEBUG_HANDLER
                        : TemplateExceptionHandler.RETHROW_HANDLER;
        cfg.setTemplateExceptionHandler(handler);

        cfg.setTemplateLoader(new TemplateLoaderDelegatingToInjectedLoaders(freemarkerFreeMarkerTemplateLoaders));

        cfg.setLogTemplateExceptions(false);
    }

    public String process(final String templateReference, String templateAtPath, final Map<String, String> properties)
            throws IOException, TemplateException {
        final String templateName = join(templateReference, templateAtPath);
        final Template template = cfg.getTemplate(templateName);
        final StringWriter sw = new StringWriter();
        template.process(properties, sw);
        return sw.toString();
    }

    //region > join, split (helpers)

    /**
     * separates the reference from the atPath
     */
    private static String SEPARATOR = ":";

    private static String join(final String templateReference, final String templateAtPath) {
        // empty string at the end means that the "_en_GB" appended to template name is separated out.
        return Joiner.on(SEPARATOR).join(templateReference, templateAtPath, "");
    }

    static String[] split(final String templateName) {
        return Splitter.on(SEPARATOR).splitToList(templateName).toArray(new String[]{});
    }

    //endregion

    //region > injected services
    @Inject
    ConfigurationService configurationService;

    @Inject
    List<FreeMarkerTemplateLoader> freemarkerFreeMarkerTemplateLoaders;
    //endregion

}



