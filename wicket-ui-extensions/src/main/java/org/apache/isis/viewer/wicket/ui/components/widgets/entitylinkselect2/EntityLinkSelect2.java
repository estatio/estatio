/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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

package org.apache.isis.viewer.wicket.ui.components.widgets.entitylinkselect2;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.vaynberg.wicket.select2.ChoiceProvider;
import com.vaynberg.wicket.select2.Select2Choice;
import com.vaynberg.wicket.select2.TextChoiceProvider;

import org.apache.wicket.markup.html.form.LabeledWebMarkupContainer;

import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.bookmarks.Bookmark;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.adapter.mgr.AdapterManager.ConcurrencyChecking;
import org.apache.isis.core.metamodel.adapter.oid.OidMarshaller;
import org.apache.isis.core.metamodel.adapter.oid.RootOid;
import org.apache.isis.core.metamodel.adapter.oid.RootOidDefault;
import org.apache.isis.core.metamodel.facets.object.autocomplete.AutoCompleteFacet;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.viewer.wicket.model.mementos.ObjectAdapterMemento;
import org.apache.isis.viewer.wicket.model.models.EntityModel;
import org.apache.isis.viewer.wicket.model.models.ModelAbstract;
import org.apache.isis.viewer.wicket.model.models.EntityModel.RenderingHint;
import org.apache.isis.viewer.wicket.model.util.Mementos;
import org.apache.isis.viewer.wicket.ui.components.scalars.ScalarPanelAbstract;
import org.apache.isis.viewer.wicket.ui.components.scalars.ScalarPanelAbstract.Rendering;
import org.apache.isis.viewer.wicket.ui.components.widgets.entitylink.EntityLink;

public class EntityLinkSelect2 extends EntityLink {

    private static final long serialVersionUID = 1L;
    private static final String ID_AUTO_COMPLETE = "autoComplete";

    private Select2Choice<ObjectAdapterMemento> autoCompleteField;

    
    
    public EntityLinkSelect2(String id, EntityModel entityModel) {
        super(id, entityModel);
    }

    
    protected void doSyncWithInputWhenChoices() {
        permanentlyHide(ID_AUTO_COMPLETE);
    }

    @Override
    protected void doSyncWithInputOther() {
        if(hasAutoComplete()){

            // serializable, referenced by the AutoCompletionChoicesProvider below  
            final EntityModel entityModel = getEntityModel();
            
            ChoiceProvider<ObjectAdapterMemento> provider = new TextChoiceProvider<ObjectAdapterMemento>() {

                private static final long serialVersionUID = 1L;

                @Override
                protected String getDisplayText(ObjectAdapterMemento choice) {
                    return choice.getObjectAdapter(ConcurrencyChecking.NO_CHECK).titleString();
                }

                @Override
                protected Object getId(ObjectAdapterMemento choice) {
                    return choice.toString();
                }

                @Override
                public void query(String term, int page, com.vaynberg.wicket.select2.Response<ObjectAdapterMemento> response) {
                    final ObjectSpecification typeOfSpecification = entityModel.getTypeOfSpecification();
                    final AutoCompleteFacet autoCompleteFacet = typeOfSpecification.getFacet(AutoCompleteFacet.class);
                    final List<ObjectAdapter> results = autoCompleteFacet.execute(term);
                    List<ObjectAdapterMemento> mementos = Lists.transform(results, Mementos.fromAdapter());
                    response.addAll(mementos);
                }

                @Override
                public Collection<ObjectAdapterMemento> toChoices(Collection<String> ids) {
                    Function<String, ObjectAdapterMemento> function = new Function<String, ObjectAdapterMemento>() {

                        @Override
                        public ObjectAdapterMemento apply(String input) {
                            
                            final RootOid oid = RootOidDefault.deString(input, ObjectAdapterMemento.getOidMarshaller());
                            return ObjectAdapterMemento.createPersistent(oid);
                        }
                    };
                    return Collections2.transform(ids, function);
                }

            };
            final ModelAbstract<ObjectAdapterMemento> model = new ModelAbstract<ObjectAdapterMemento>(){

                private static final long serialVersionUID = 1L;
                
                @Override
                protected ObjectAdapterMemento load() {
                    return ObjectAdapterMemento.createOrNull(getPendingElseCurrentAdapter());
                }
                
            };
            autoCompleteField = new Select2Choice<ObjectAdapterMemento>(ID_AUTO_COMPLETE, model, provider);
            addOrReplace(autoCompleteField);
            
            // no need for link, since can see in drop-down
            permanentlyHide(ID_ENTITY_ICON_AND_TITLE);

            // no need for the 'null' title, since if there is no object yet
            // can represent this fact in the drop-down
            permanentlyHide(ID_ENTITY_TITLE_NULL);
            
            // hide links
            permanentlyHide(ID_FIND_USING);
        } else {
            permanentlyHide(ID_AUTO_COMPLETE);
        }
    }

    
    @Override
    protected void doSyncVisibilityAndUsability(boolean mutability) {
        if(autoCompleteField != null) {
            autoCompleteField.setEnabled(mutability);
        }

        if(hasAutoComplete()) {
            permanentlyHide(ID_ENTITY_ICON_AND_TITLE);
            
            if(getEntityModel().isEditMode()) {
                // TODO: haven't figured out how to keep in sync..
                permanentlyHide(ID_ENTITY_DETAILS_LINK);
            }
        }
    }
    
    @Override
    protected void doConvertInput() {
        if(getEntityModel().isEditMode() && hasAutoComplete()) {
            // flush changes to pending
            onSelected(autoCompleteField.getConvertedInput().getObjectAdapter(ConcurrencyChecking.NO_CHECK));
        }
    }
    
    private boolean hasAutoComplete() {
        
        // doesn't apply in compact rendering contexts (ie tables)
        if(getEntityModel().getRenderingHint() == RenderingHint.COMPACT) {
            return false;
        }
        
        final ObjectSpecification typeOfSpecification = getEntityModel().getTypeOfSpecification();
        final AutoCompleteFacet autoCompleteFacet = 
                (typeOfSpecification != null)? typeOfSpecification.getFacet(AutoCompleteFacet.class):null;
        return autoCompleteFacet != null;
    }


    
    
}
