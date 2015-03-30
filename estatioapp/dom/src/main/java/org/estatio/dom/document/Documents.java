/*
 *  Copyright 2015 Eurocommercial Properties NV
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
package org.estatio.dom.document;

import java.util.List;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.utils.StringUtils;

@DomainService()
@DomainServiceLayout(named = "Other", menuBar = DomainServiceLayout.MenuBar.PRIMARY, menuOrder = "80.10")
public class Documents extends UdoDomainRepositoryAndFactory<Document> {

    public Documents()
    {
        super(Documents.class, Document.class);
    }

    public String getId() {
        return "documents";
    }

    public String iconName() {
        return "Document";
    }

    @MemberOrder(sequence = "2")
    public List<Document> allDocuments() {
        return allInstances(Document.class);
    }

    public List<Document> findByName(final String pattern) {
        return allMatches("findByName", "pattern", StringUtils.wildcardToCaseInsensitiveRegex(pattern));
    }

}
