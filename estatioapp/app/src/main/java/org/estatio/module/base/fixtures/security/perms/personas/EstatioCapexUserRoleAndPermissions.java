package org.estatio.module.base.fixtures.security.perms.personas;

import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.security.dom.permission.ApplicationPermissionMode;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRule;
import org.isisaddons.module.security.seed.scripts.AbstractRoleAndPermissionsFixtureScript;

import org.estatio.module.base.dom.EstatioRole;

@Programmatic
public class EstatioCapexUserRoleAndPermissions extends AbstractRoleAndPermissionsFixtureScript {

    public static final String ROLE_NAME = EstatioRole.CAPEX_USER.getRoleName();

    public EstatioCapexUserRoleAndPermissions() {
        super(ROLE_NAME, "Estatio Capex user");
    }

    @Override
    protected void execute(final ExecutionContext executionContext) {
        newViewingPermission("org.estatio");
        newViewingPermission("org.estatio.app");
        newViewingPermission("org.estatio.dom");
        newViewingPermission("org.estatio.services");
        newViewingPermission("org.incode");
        newViewingPermission("org.estatio.webapp.services.other");
        newChangingPermission("org.estatio.module.capex");
        newChangingPermission("org.estatio.module.application.app.dashboard");
        newChangingPermission("org.estatio.module.asset.app.PropertyMenu#findProperties");
        newChangingPermission("org.estatio.module.charge.app.ChargeMenu#findCharge");
        newChangingPermission("org.estatio.module.party.app.PartyMenu#findParties");
        newChangingPermission("org.estatio.module.party.app.PersonMenu#findPerson");
    }

    private void newChangingPermission(String packageSuffix) {
        newPackagePermissions(
                ApplicationPermissionRule.ALLOW,
                ApplicationPermissionMode.CHANGING,
                packageSuffix);
    }

    private void newViewingPermission(String packageSuffix) {
        newPackagePermissions(
                ApplicationPermissionRule.ALLOW,
                ApplicationPermissionMode.VIEWING,
                packageSuffix);
    }
}
