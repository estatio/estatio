package org.incode.platform.dom.alias.integtests.dom.alias.dom.ui;

import com.google.common.eventbus.Subscribe;

import org.axonframework.eventhandling.annotation.EventHandler;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.alias.dom.impl.Alias;
import org.incode.platform.dom.alias.integtests.dom.alias.dom.spiimpl.aliastype.AliasTypeDemoEnum;

@DomainService(nature = NatureOfService.DOMAIN )
public class AliasUiSubscriber extends AbstractSubscriber {

    @EventHandler
    @Subscribe
    public void on(Alias.TitleUiEvent ev) {
        Alias alias = ev.getSource();
        if(isType(alias, AliasTypeDemoEnum.DOCUMENT_MANAGEMENT)) {
            ev.setTitle("DocMgmt  [" + alias.getAliasTypeId() + "] " + alias.getReference());
        }
    }

    @EventHandler
    @Subscribe
    public void on(Alias.IconUiEvent ev) {
        Alias alias = ev.getSource();
        if(isType(alias, AliasTypeDemoEnum.DOCUMENT_MANAGEMENT)) {
            ev.setIconName("Alias-docMgmt");
        } else if (isType(alias, AliasTypeDemoEnum.GENERAL_LEDGER)) {
            ev.setIconName("Alias-GL");
        }
    }

    @EventHandler
    @Subscribe
    public void on(Alias.CssClassUiEvent ev) {
        Alias alias = ev.getSource();
        ev.setCssClass("Alias" + alias.getAtPath().replace("/", "-"));
    }

    private static boolean isType(final Alias alias, final AliasTypeDemoEnum aliasType) {
        return alias.getAliasTypeId().equals(aliasType.getId());
    }
}
