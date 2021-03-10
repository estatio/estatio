package org.estatio.module.lease.dom.occupancy.tags;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@DomainObject(nature = Nature.VIEW_MODEL, objectType = "org.estatio.module.lease.dom.occupancy.tags.BrandGroupViewModel")
public class BrandGroupViewModel {

    public String title() {
        return group;
    }

    @Getter @Setter
    private String group;

}
