package org.estatio.dom.budgeting.keytable;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.asset.Property;

/**
 * Created by jodo on 14/09/15.
 */
@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(menuBar = DomainServiceLayout.MenuBar.PRIMARY, named = "Budgets")
public class KeyTableMenu {

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public KeyTable newKeyTable(
            final @ParameterLayout(named = "Property") Property property,
            final @ParameterLayout(named = "Name") String name,
            final @ParameterLayout(named = "Start Date") LocalDate startDate,
            final @ParameterLayout(named = "End Date") LocalDate endDate,
            final @ParameterLayout(named = "Foundation Value Type") FoundationValueType foundationValueType,
            final @ParameterLayout(named = "Key Value Method") KeyValueMethod keyValueMethod,
            final @ParameterLayout(named = "Number Of Digits") Integer numberOfDigits) {
        return keyTables.newKeyTable(property, name, startDate, endDate, foundationValueType, keyValueMethod, numberOfDigits);
    }

    public String validateNewKeyTable(
            final Property property,
            final String name,
            final LocalDate startDate,
            final LocalDate endDate,
            final FoundationValueType foundationValueType,
            final KeyValueMethod keyValueMethod,
            final Integer numberOfDigits) {
        return keyTables.validateNewKeyTable(property, name, startDate, endDate, foundationValueType, keyValueMethod, numberOfDigits);
    }

    @Action(restrictTo = RestrictTo.PROTOTYPING, semantics = SemanticsOf.SAFE)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<KeyTable> allKeyTables() {
        return keyTables.allKeyTables();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<KeyTable> findBudgetKeyTableByProperty(Property property) {
        return keyTables.findByProperty(property);
    }

    @Inject
    private KeyTables keyTables;
}
