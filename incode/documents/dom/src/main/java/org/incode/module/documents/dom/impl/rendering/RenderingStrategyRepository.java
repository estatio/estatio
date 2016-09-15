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
package org.incode.module.documents.dom.impl.rendering;

import java.util.List;

import javax.inject.Inject;

import org.datanucleus.query.typesafe.TypesafeQuery;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.documents.dom.impl.docs.DocumentNature;
import org.incode.module.documents.dom.impl.renderers.Renderer;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = RenderingStrategy.class
)
public class RenderingStrategyRepository {

    public String getId() {
        return "incodeDocuments.RenderingStrategyRepository";
    }


    @Programmatic
    public RenderingStrategy create(
            final String reference,
            final String name,
            final DocumentNature inputNature,
            final DocumentNature outputNature,
            final Class<? extends Renderer> rendererClass) {
        final RenderingStrategy renderingStrategy =
                new RenderingStrategy(reference, name, inputNature, outputNature, rendererClass);
        repositoryService.persist(renderingStrategy);
        return renderingStrategy;
    }

    @Programmatic
    public RenderingStrategy findByReference(
            final String reference) {
        final QRenderingStrategy cand = newCandidate();
        return newQuery()
                .filter(
                        cand.reference.eq(reference))
                .executeUnique();
    }

    @Programmatic
    public List<RenderingStrategy> findForUseWithSubjectText() {
        return findByInputNatureAndOutputNature(DocumentNature.CHARACTERS, DocumentNature.CHARACTERS);
    }

    @Programmatic
    public List<RenderingStrategy> findByInputNatureAndOutputNature(
            final DocumentNature inputNature, final DocumentNature outputNature) {
        final QRenderingStrategy cand = newCandidate();
        return newQuery()
                .filter(
                        cand.inputNature.eq(inputNature)
                .and(
                        cand.outputNature.eq(outputNature)))
                .orderBy(
                        cand.rendererClassName.asc())
                .executeList();
    }

    @Programmatic
    public List<RenderingStrategy> allStrategies() {
        return repositoryService.allInstances(RenderingStrategy.class);
    }

    //region > helpers

    private TypesafeQuery<RenderingStrategy> newQuery() {
        return isisJdoSupport.newTypesafeQuery(RenderingStrategy.class);
    }

    private static QRenderingStrategy newCandidate() {
        return QRenderingStrategy.candidate();
    }

    //endregion


    //region > injected services
    @Inject
    RepositoryService repositoryService;
    @Inject
    IsisJdoSupport isisJdoSupport;
    //endregion

}
