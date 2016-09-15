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
package org.incode.module.documents.dom.impl.docs;

public class DataModels {

    public static DataModels createContentAndSubject(Object content, Object subject) {
        return new DataModels(content, subject);
    }
    public static DataModels createContentOnly(Object content) {
        return new DataModels(content, null);
    }

    private final Object content;
    private final Object subject;

    private DataModels(final Object content, final Object subject) {
        this.content = content;
        this.subject = subject;
    }

    public Object getContent() {
        return content;
    }

    public Object getSubject() {
        return subject;
    }

}
