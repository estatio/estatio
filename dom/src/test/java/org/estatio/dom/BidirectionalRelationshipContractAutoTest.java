package org.estatio.dom;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;

import org.hamcrest.Matchers;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

import org.apache.isis.core.commons.lang.StringUtils;

public class BidirectionalRelationshipContractAutoTest {

    private Reflections reflections;

    @Before
    public void setUp() throws Exception {
        reflections = new Reflections(Constants.packagePrefix);
    }
    
    @Ignore
    @Test
    public void searchAndTest() throws Exception {
        
        Set<Class<?>> entityTypes = 
                reflections.getTypesAnnotatedWith(PersistenceCapable.class);
        
        for (Class<?> entityType : entityTypes) {
            process(entityType);
        }
    }

    static class Parent {
        Class<?> entityType;
        Field childrenField;
        String mappedBy;
        Method getMethod;
        Method addToMethod;
        Method removeFromMethod;
        public Collection<?> getChildren(Object parent) {
            return null;
        }
        public void addToChildren(Object parent, Object child) {
        }
        public void removeFromChildren(Object parent1, Object object) {
        }
    }
    
    static class Child {
        Class<?> entityType;
        Field parentField;
        Method getMethod;
        Method modifyMethod;
        Method clearMethod;
        public Object getParent(Object child) {
            return null;
        }
        public void modifyParent(Object child, Object parent) {
        }
        public void clearParent(Object child1) {
        }
    }

    private void process(Class<?> entityType) {
        final Set<Field> mappedByFields = Reflections.getAllFields(entityType, persistentMappedBy);
        for (Field mappedByField : mappedByFields) {
            final Parent p = new Parent();
            p.entityType = entityType;
            p.childrenField = mappedByField;
            process(p);
        }
    }
    
    private void process(Parent p) {
        

        // mappedBy
        final Persistent persistentAnnotation = p.childrenField.getAnnotation(Persistent.class);
        p.mappedBy = persistentAnnotation.mappedBy();

        // getMethod
        final String getMethod = "get" + StringUtils.capitalize(p.childrenField.getName());
        final Set<Method> getMethods = Reflections.getAllMethods(p.entityType, Predicates.and(ReflectionUtils.withName(getMethod), new Predicate<Method>() {
           public boolean apply(Method m) {
               final Class<?> returnType = m.getReturnType();
               return Collection.class.isAssignableFrom(returnType);
           }
        }));
        assertThat(p.entityType.getName()+"#"+p.childrenField.getName()+ ": could not locate getter", getMethods.size(), is(1));
        p.getMethod = firstIn(getMethods);
        
        // addToMethod
        final String addToMethod = "addTo" + StringUtils.capitalize(p.childrenField.getName());
        final Set<Method> addToMethods = Reflections.getAllMethods(p.entityType, 
                Predicates.and(ReflectionUtils.withName(addToMethod), ReflectionUtils.withParametersCount(1), withEntityParameter()));
        if(addToMethods.size() != 1) {
            // just skip
            return;
        }
        p.addToMethod = firstIn(addToMethods);

        // removeFromMethod
        final String removeFromMethod = "removeFrom" + StringUtils.capitalize(p.childrenField.getName());
        final Set<Method> removeFromMethods = Reflections.getAllMethods(p.entityType, 
                Predicates.and(ReflectionUtils.withName(removeFromMethod), ReflectionUtils.withParametersCount(1), withEntityParameter()));
        if(removeFromMethods.size() != 1) {
            // just skip
            return;
        }
        p.removeFromMethod = firstIn(removeFromMethods);


        // child's entityType
        final Class<?> addToParameterType = p.addToMethod.getParameterTypes()[0];
        final Class<?> removeFromParameterType = p.removeFromMethod.getParameterTypes()[0];
        
        assertThat(p.entityType.getName() + "#" + p.addToMethod.getName() + " and " + p.removeFromMethod.getName() + " should have the same parameter type",
                addToParameterType == removeFromParameterType, is(true));

        final Child c = new Child();
        c.entityType = addToParameterType;

        process(p, c);
    }

    private void process(Parent p, Child c) {
        
        // mappedBy field
        final Set<Field> parentFields = Reflections.getAllFields(c.entityType, Predicates.and(Reflections.withName(p.mappedBy), ReflectionUtils.withType(p.entityType)));

        assertThat(c.entityType.getName()+  ": could not locate '" + p.mappedBy + "' field (as per @Persistent(mappedBy=...) in parent)", parentFields.size(), is(1));
        c.parentField = firstIn(parentFields);

        // getter
        String getterMethod = "get" + StringUtils.capitalize(c.parentField.getName());
        final Set<Method> getterMethods = Reflections.getAllMethods(c.entityType, 
                Predicates.and(Reflections.withName(getterMethod), ReflectionUtils.withParametersCount(0), ReflectionUtils.withReturnType(p.entityType)));
        assertThat(c.entityType.getName() + "#" + c.parentField.getName() +  ": could not locate getter", getterMethods.size(), is(1));
        c.getMethod = firstIn(getterMethods);

        // modify
        String modifyMethod = "modify" + StringUtils.capitalize(c.parentField.getName());
        final Set<Method> modifyMethods = Reflections.getAllMethods(c.entityType, 
                Predicates.and(Reflections.withName(modifyMethod), ReflectionUtils.withParametersCount(1), ReflectionUtils.withParameters(p.entityType)));
        if(modifyMethods.size() != 1) {
            // just skip
            return;
        }
        c.modifyMethod = firstIn(modifyMethods);
        
        // clear
        String clearMethod = "clear" + StringUtils.capitalize(c.parentField.getName());
        final Set<Method> clearMethods = Reflections.getAllMethods(c.entityType, 
                Predicates.and(Reflections.withName(modifyMethod), ReflectionUtils.withParametersCount(0)));
        if(clearMethods.size() != 1) {
            // just skip
            return;
        }
        c.clearMethod = firstIn(modifyMethods);

        exercise(p, c);
    }

    private static <T> T firstIn(final Set<T> set) {
        return set.iterator().next();
    }
    

    private static final Predicate<? super Field> persistentMappedBy = new Predicate<Field>() {
        public boolean apply(Field f) {
            final Persistent annotation = f.getAnnotation(Persistent.class);
            return annotation!=null && !Strings.isNullOrEmpty(annotation.mappedBy());
        }
    };

    private static Predicate<? super Method> withEntityParameter() {
        return new Predicate<Method>() {
            public boolean apply(Method m) {
                final Class<?> parameterType = m.getParameterTypes()[0];
                return parameterType.isAnnotationPresent(PersistenceCapable.class);
            }
        };
    }

    private void exercise(Parent p, Child c) {
        addAtParent(p, c);
        addAtParentWhenAlreadyChild(p, c);
        addAtParentWhenNull(p, c);
        addAtChild(p, c);
        addAtChildWhenAlreadyParent(p, c);
        addAtChildWhenNull(p, c);
        
        removeAtParent(p, c);
        removeAtParentWhenNotAssociated(p, c);
        removeAtParentWhenNull(p, c);
        removeAtChild(p, c);
        
        move(p, c);
        moveWhenAlreadyAssociated(p, c);
    }

    
    private void addAtParent(Parent p, Child c) {
        
        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);
        

        // when
        p.addToChildren(parent1, child1);

        // then
        assertThat(p.getChildren(parent1), Matchers.contains(child1));
        assertThat(c.getParent(child1), is(parent1));
    }

    private void addAtParentWhenAlreadyChild(Parent p, Child c) {
        
        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);

        // given
        p.addToChildren(parent1, child1);
        
        // when
        p.addToChildren(parent1, child1);

        // then
        assertThat(p.getChildren(parent1), Matchers.contains(child1));
        assertThat(c.getParent(child1), is(parent1));
    }
    
    private void addAtParentWhenNull(Parent p, Child c) {

        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);

        // when
        p.addToChildren(parent1, null);

        // then
        assertThat(p.getChildren(parent1).isEmpty(), is(true));
    }
    
    private void addAtChild(Parent p, Child c) {

        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);

        // when
        c.modifyParent(child1, parent1);

        // then
        assertThat(p.getChildren(parent1), Matchers.contains(child1));
        assertThat(c.getParent(child1), is(parent1));
    }

    private void addAtChildWhenAlreadyParent(Parent p, Child c) {
        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);

        // given
        c.modifyParent(child1, parent1);
        
        // when
        c.modifyParent(child1, parent1);

        // then
        assertThat(p.getChildren(parent1), Matchers.contains(child1));
        assertThat(c.getParent(child1), is(parent1));
    }


    private void addAtChildWhenNull(Parent p, Child c) {
        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);

        // when
        c.modifyParent(child1, null);
        
        // then
        assertThat(c.getParent(child1), is(nullValue()));
    }
    
    private void removeAtParent(Parent p, Child c) {
        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);
        
        // given
        p.addToChildren(parent1, child1);
        
        // when
        p.removeFromChildren(parent1, child1);

        // then
        assertThat(p.getChildren(parent1).isEmpty(), is(true));
        assertThat(c.getParent(child1), is(nullValue()));
    }

    private void removeAtParentWhenNull(Parent p, Child c) {
        
        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);
        
        // given
        p.addToChildren(parent1, child1);
        
        // when
        p.removeFromChildren(parent1, null);

        // then
        assertThat(p.getChildren(parent1), Matchers.contains(child1));
        assertThat(c.getParent(child1), is(parent1));
    }
    
    private void removeAtParentWhenNotAssociated(Parent p, Child c) {
        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);
        
        // given
        p.addToChildren(parent1, child1);
        
        // when
        p.removeFromChildren(parent1, child2);
        
        // then
        assertThat(p.getChildren(parent1), Matchers.contains(child1));
        assertThat(c.getParent(child1), is(parent1));
    }
    
    private void removeAtChild(Parent p, Child c) {
        
        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);
        
        // given
        p.addToChildren(parent1, child1);
        
        // when
        c.clearParent(child1);

        // then
        assertThat(p.getChildren(parent1).isEmpty(), is(true));
        assertThat(c.getParent(child1), is(nullValue()));
    }

    private void move(Parent p, Child c) {
        
        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);
        
        // given
        p.addToChildren(parent1, child1);
        p.addToChildren(parent2, child2);
        
        // when
        c.modifyParent(child1, parent2);

        // then
        assertThat(p.getChildren(parent1).isEmpty(), is(true));
        assertThat(p.getChildren(parent2), Matchers.contains(child1, child2));
        assertThat(c.getParent(child1), is(parent2));
        assertThat(c.getParent(child2), is(parent2));
    }
    
    private void moveWhenAlreadyAssociated(Parent p, Child c) {
        
        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);
        
        // given
        p.addToChildren(parent1, child1);
        p.addToChildren(parent2, child2);
        
        // when
        c.modifyParent(child1, parent1);
        
        // then
        assertThat(p.getChildren(parent1), Matchers.contains(child1));
        assertThat(p.getChildren(parent2), Matchers.contains(child2));
        assertThat(c.getParent(child1), is(parent1));
        assertThat(c.getParent(child2), is(parent2));
    }

    protected Object newParent(Parent p) {
        return null;
    }

    protected Object newChild(Child c) {
        return null;
    }


}
