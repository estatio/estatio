package org.incode.platform.dom.classification.integtests.app.services;

import com.google.common.eventbus.Subscribe;

import org.axonframework.eventhandling.annotation.EventHandler;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.classification.dom.impl.category.Category;

public class CustomSubscriberForTesting {

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class CustomTitleSubscriber extends Category.TitleSubscriber {
        @EventHandler
        @Subscribe
        public void on(Category.TitleUiEvent ev) {
            if (ev.getTitle() != null) {
                return;
            }
            ev.setTitle("Holtkamp");
        }

    }

    @DomainService
    public static class CustomIconSubscriber extends Category.IconSubscriber {
        @EventHandler
        @Subscribe
        public void on(Category.IconUiEvent ev) {
            if (ev.getIconName() != null) {
                return;
            }
            ev.setIconName("Jodekoek.png");
        }
    }

    @DomainService
    public static class CustomCssClassSubscriber extends Category.CssClassSubscriber {
        @EventHandler
        @Subscribe
        public void on(Category.CssClassUiEvent ev) {
            if (ev.getCssClass() != null) {
                return;
            }
            ev.setCssClass("Enkhuizer.css");
        }
    }
    //endregion

}
