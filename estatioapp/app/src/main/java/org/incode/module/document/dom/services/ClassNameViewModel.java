package org.incode.module.document.dom.services;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Title;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "incodeDocuments.ClassNameViewModel"
)
public class ClassNameViewModel {

    public ClassNameViewModel() {
    }

    public ClassNameViewModel(final Class<?> cls) {
        this.simpleClassName = cls.getSimpleName();
        this.fullyQualifiedClassName = cls.getName();
    }

    @Title
    @Getter @Setter
    private String simpleClassName;
    @Getter @Setter
    private String fullyQualifiedClassName;

}
