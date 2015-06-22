package org.estatio.dom.project;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.NotContributed.As;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.asset.Property;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.StringUtils;

@DomainService(menuOrder = "35")
public class Projects extends EstatioDomainService<Project> {

    public Projects() {
        super(Projects.class, Project.class);
    }

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name = "Projects", sequence = "1")
    public Project newProject(
            final @Named("Reference") String reference,
            final @Named("Name") @Optional String name,
            final @Named("Start date") @Optional LocalDate startDate,
            final @Named("End date") @Optional LocalDate endDate,
            final @Named("Property") @Optional Property property,
            final @Named("Responsible") Party responsible) {
        // Create project instance
        Project project = getContainer().newTransientInstance(Project.class);
        // Set values
        project.setReference(reference);
        project.setName(name);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        project.setProperty(property);
        project.setResponsible(responsible);
        // Persist it
        persist(project);
        // Return it
        return project;
    }

    @ActionSemantics(Of.SAFE)
    public List<Project> allProjects() {
        return allInstances();
    }

    @ActionSemantics(Of.SAFE)
    public List<Project> findProject(final @Named("Name or reference") String searchStr) {
        return allMatches("findByReferenceOrName", "matcher", StringUtils.wildcardToCaseInsensitiveRegex(searchStr));
    }

    @NotInServiceMenu
    @NotContributed(As.ACTION)
    @Render(Type.EAGERLY)
    @MemberOrder(name = "Projects", sequence = "1")
    @ActionSemantics(Of.SAFE)
    public List<Project> projects(final Property property) {
        return allMatches("findByProperty", "property", property);
    }

    @NotInServiceMenu
    @NotContributed(As.ACTION)
    @Render(Type.EAGERLY)
    @MemberOrder(name = "Projects", sequence = "1")
    @ActionSemantics(Of.SAFE)
    public List<Project> projects(final Party party) {
        return allMatches("findByResponsible", "responsible", party);
    }

}
