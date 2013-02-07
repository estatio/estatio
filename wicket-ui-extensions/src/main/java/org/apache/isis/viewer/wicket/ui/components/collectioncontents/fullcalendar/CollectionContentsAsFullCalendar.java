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

package org.apache.isis.viewer.wicket.ui.components.collectioncontents.fullcalendar;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ftlines.wicket.fullcalendar.Config;
import net.ftlines.wicket.fullcalendar.Event;
import net.ftlines.wicket.fullcalendar.EventNotFoundException;
import net.ftlines.wicket.fullcalendar.EventProvider;
import net.ftlines.wicket.fullcalendar.EventSource;
import net.ftlines.wicket.fullcalendar.FullCalendar;
import net.ftlines.wicket.fullcalendar.selector.EventSourceSelector;

import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.util.time.Duration;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.spec.feature.ObjectAssociation;
import org.apache.isis.viewer.wicket.model.models.EntityCollectionModel;
import org.apache.isis.viewer.wicket.ui.panels.PanelAbstract;

/**
 * {@link PanelAbstract Panel} that represents a {@link EntityCollectionModel
 * collection of entity}s rendered using {@link AjaxFallbackDefaultDataTable}.
 */
public class CollectionContentsAsFullCalendar extends PanelAbstract<EntityCollectionModel> {

    private static final long serialVersionUID = 1L;

    private static final String ID_SELECTOR = "selector";
    private static final String ID_FULL_CALENDAR = "fullCalendar";
    private static final String ID_FEEDBACK = "feedback";

    private final static String[] COLORS = {
        "#63BA68", "#B1ADAC", "#E6CC7F"
    };
    
    public CollectionContentsAsFullCalendar(final String id, final EntityCollectionModel model) {
        super(id, model);

        buildGui();
    }

    private void buildGui() {

        final EntityCollectionModel model = getModel();
        
        final ObjectSpecification elementSpec = model.getTypeOfSpecification();

        final FeedbackPanel feedback = new FeedbackPanel(ID_FEEDBACK);
        feedback.setOutputMarkupId(true);
        add(feedback);

        
        final Config config = new Config();
        config.setSelectable(true);
        config.setSelectHelper(false);
        
        List<ObjectAssociation> dateAssociations = elementSpec.getAssociations(CollectionContentsAsFullCalendarFactory.OF_TYPE_DATE);
        
        
        int i=0;
        for (ObjectAssociation dateAssociation : dateAssociations) {
            final EventSource association = new EventSource();
            association.setTitle(dateAssociation.getName());
            association.setEventsProvider(new DateAssociationEventsProvider(model, dateAssociation));
            association.setEditable(true);
            String color = COLORS[i++ % COLORS.length];
            association.setBackgroundColor(color);
            association.setBorderColor(color);
            config.add(association);
        }

        config.setAspectRatio(2.5f);
        
        config.getHeader().setLeft("prevYear,prev,next,nextYear, today");
        config.getHeader().setCenter("title");
        config.getHeader().setRight("");

        config.setLoading("function(bool) { if (bool) $(\"#loading\").show(); else $(\"#loading\").hide(); }");

        config.setAllDaySlot(true);
        
        final FullCalendar calendar = new FullCalendarWithEventHandling(ID_FULL_CALENDAR, config, feedback);
        calendar.setMarkupId(ID_FULL_CALENDAR);
        add(calendar);

        add(new EventSourceSelector(ID_SELECTOR, calendar));
    }
}
