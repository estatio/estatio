package org.estatio.module.capex.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.user.UserService;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.asset.dom.role.FixedAssetRoleRepository;
import org.estatio.module.base.dom.EstatioRole;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalConfigurationUtil;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;

/**
 * ECP-1346
 */
@DomainService(nature = NatureOfService.DOMAIN, objectType = "capex.app.ExternalUserService")
public class ExternalUserService {

    public boolean isForExternalUser(){
        return EstatioRole.EXTERNAL_APPROVER.isApplicableFor(userService.getUser());
    }

    public List<Property> getPropertiesForExternalUser() {
        final Person meAsPerson = personRepository.me();
        if (meAsPerson !=null) {
            return fixedAssetRoleRepository.findByParty(meAsPerson)
                    .stream()
                    .map(FixedAssetRole::getAsset)
                    .filter(fixedAsset -> fixedAsset instanceof Property)
                    .map(fixedAsset -> (Property) fixedAsset)
                    .distinct()
                    .collect(Collectors.toList());
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public List<Project> getProjectsForExternalUser(){
        final Person meAsPerson = personRepository.me();
        if (meAsPerson !=null) {
            final List<String> projectRefsFound = new ArrayList<>();
            for (Property property : getPropertiesForExternalUser()){
                IncomingInvoiceApprovalConfigurationUtil.PROPERTY_REF_EXTERNAL_PROJECT_REF_MAP.forEach((k,v)->{
                    if (k.equals(property.getReference())) projectRefsFound.add(v);
                });
            }
            return projectRefsFound.stream().map(ref->projectRepository.findByReference(ref)).collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }

    public List<Party> getBuyersForExternalUser() {

        final List<Party> result = new ArrayList<>();

        for (Property property : getPropertiesForExternalUser()) {

           final List<Party> currentOwners = property.ownerCandidates().stream()
                   .filter(oc -> oc.isCurrent())
                   .map(oc -> oc.getParty())
                   .distinct()
                   .collect(Collectors.toList());

           if (currentOwners.size() == 1) result.add(currentOwners.get(0));

        }

       return result;
    }

    @Inject UserService userService;

    @Inject PersonRepository personRepository;

    @Inject FixedAssetRoleRepository fixedAssetRoleRepository;

    @Inject ProjectRepository projectRepository;
}
