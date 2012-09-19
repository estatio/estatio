package com.eurocommercialproperties.estatio.viewer.wicket.app;

import com.google.inject.Singleton;

import org.apache.isis.viewer.wicket.ui.components.widgets.entitylinkselect2.EntityLinkSelect2Factory;
import org.apache.isis.viewer.wicket.ui.components.widgets.valuechoices.ValueChoicesComponentFactory;
import org.apache.isis.viewer.wicket.ui.components.widgets.valuechoicesselect2.ValueChoicesSelect2ComponentFactory;
import org.apache.isis.viewer.wicket.viewer.registries.components.ComponentFactoryRegistrarDefault;

@Singleton
public class ComponentFactoryRegistrarForEstatio extends ComponentFactoryRegistrarDefault {

    @Override
    public void addComponentFactories(ComponentFactoryList componentFactories) {
        super.addComponentFactories(componentFactories);
        componentFactories.replace(new EntityLinkSelect2Factory());
        componentFactories.replace(ValueChoicesComponentFactory.class, new ValueChoicesSelect2ComponentFactory());
    }
}
