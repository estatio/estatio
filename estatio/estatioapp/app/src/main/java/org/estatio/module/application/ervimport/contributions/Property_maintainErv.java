package org.estatio.module.application.ervimport.contributions;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.application.ervimport.ErvImportManager;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.erv.Type;

@Mixin
public class Property_maintainErv {

    private final Property property;

    public Property_maintainErv(Property property) {
        this.property = property;
    }

    @Action()
    public ErvImportManager $$(final Type type, final LocalDate date) {
        return new ErvImportManager(property, type, date);
    }

}
