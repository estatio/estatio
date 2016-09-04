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
import java.io.Reader;
import java.io.StringReader;

import freemarker.template.TemplateException;

class TemplateLoaderFromThreadLocal implements freemarker.cache.TemplateLoader {

    private static final ThreadLocal<TemplateSource> templateSource = new ThreadLocal<>();

    static interface Block {
        String exec(final TemplateSource templateSource) throws IOException, TemplateException;
    }

    static String withTemplateSource(
            final String templateName,
            final long version,
            final String templateChars,
            final Block block)
            throws IOException, TemplateException {
        return withTemplateSource(new TemplateSource(templateName, version, templateChars), block);
    }

    static String withTemplateSource(final TemplateSource templateSource, final Block block)
            throws IOException, TemplateException {
        TemplateLoaderFromThreadLocal.templateSource.set(templateSource);
        try {
            return block.exec(templateSource);
        } finally {
            TemplateLoaderFromThreadLocal.templateSource.set(null);
        }
    }

    @Override
    public Object findTemplateSource(final String templateName) throws IOException {
        final TemplateSource templateSource = TemplateLoaderFromThreadLocal.templateSource.get();
        if(templateSource == null) {
            throw new IllegalStateException("Not called within withTemplateSource(...) block");
        }
        return templateSource;
    }

    @Override
    public Reader getReader(final Object templateSourceAsObj, final String encoding) throws IOException {
        final TemplateSource templateSource = (TemplateSource) templateSourceAsObj;
        return new StringReader(templateSource.getChars());
    }

    @Override
    public void closeTemplateSource(final Object o) throws IOException {
        // no-op
    }

    @Override
    public long getLastModified(final Object templateSourceAsObj) {
        final TemplateSource templateSource = (TemplateSource) templateSourceAsObj;
        return templateSource.getVersion();
    }

}
