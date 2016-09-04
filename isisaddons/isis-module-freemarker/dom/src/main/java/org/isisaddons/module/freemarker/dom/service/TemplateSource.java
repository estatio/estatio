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

public class TemplateSource {

    private final String templateName;
    private final String templateChars;
    private final long version;

    public TemplateSource(final String templateName, final long version, final String templateChars) {
        this.templateName = templateName;
        this.templateChars = templateChars;
        this.version = version;
    }

    public String getTemplateName() {
        return templateName;
    }

    public long getVersion() {
        return version;
    }

    public String getChars() {
        return templateChars;
    }


}
