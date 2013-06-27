package org.estatio.dom.contracttests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.util.Set;

import javax.jdo.annotations.Unique;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;

import org.junit.Test;
import org.reflections.Reflections;

import org.estatio.services.exceprecog.ExceptionRecognizersForEstatio;


public abstract class WithFieldUniqueContractTestAllAbstract<T> {

    private Class<T> interfaceType;
    private String fieldName;

    public WithFieldUniqueContractTestAllAbstract(Class<T> interfaceType, String fieldName) {
        this.interfaceType = interfaceType;
        this.fieldName = fieldName;
    }
    
    @Test
    public void searchAndTest() {
        
        final ExceptionRecognizersForEstatio exceptionRecognizersForEstatio = new ExceptionRecognizersForEstatio();
        
        Reflections reflections = new Reflections(Constants.packagePrefix);

        Set<Class<? extends T>> domainObjectClasses = reflections.getSubTypesOf(interfaceType);
        for (final Class<? extends T> subtype : domainObjectClasses) {
            if(subtype.isInterface() || subtype.isAnonymousClass() || subtype.isLocalClass() || subtype.isMemberClass()) {
                // skip (probably a testing class)
                continue;
            }
            
            System.out.println("checking " + subtype.getName());
            try {
                Predicate<? super Field> injectorFields = new Predicate<Field>() {
                    public boolean apply(Field f) {
                        return f.getName().equals(fieldName) && 
                               f.getType() == String.class && 
                               f.getAnnotation(Unique.class) != null;
                    }
                };
                final Set<Field> fields = Reflections.getAllFields(subtype, injectorFields);
                assertThat(subtype.getName() + " should have a field '" + fieldName + "', of type String, and annotated with javax.jdo.annotations.Unique", fields.size(), is(1));
                
                final Field field = fields.iterator().next();
                final Unique uniqueAnnotation = field.getAnnotation(Unique.class);
                final String constraintName = uniqueAnnotation.name();
                
                assertThat(subtype.getName() + "#" + fieldName + "'s @Unique constraint name must be explicitly set and must be registered in ExceptionRecognizersForEstatio", 
                        Strings.isNullOrEmpty(constraintName), is(false));

                assertThat(subtype.getName() + "#" + fieldName + "'s @Unique constraint name '" + constraintName + "' must be registered in ExceptionRecognizersForEstatio", 
                        exceptionRecognizersForEstatio.getRecognizedConstraintNames().contains(constraintName), is(true));
            } catch (Exception e) {
                continue;
            }
        }
    }

}
