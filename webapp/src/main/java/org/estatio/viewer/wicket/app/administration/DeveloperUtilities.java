package org.estatio.viewer.wicket.app.administration;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.value.Clob;
import org.apache.isis.core.metamodel.services.devutils.DeveloperUtilitiesServiceDefault;

public class DeveloperUtilities extends DeveloperUtilitiesServiceDefault {

    /**
     * 'Move' the action underneath the 'administration' menu item. 
     */
    @MemberOrder(name="Administration", sequence="90")
    @Override
    @ActionSemantics(Of.SAFE)
    public Clob downloadMetaModel() {
        return super.downloadMetaModel();
    }
}
