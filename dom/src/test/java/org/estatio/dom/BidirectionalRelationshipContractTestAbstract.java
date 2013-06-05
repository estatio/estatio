package org.estatio.dom;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

import org.apache.isis.core.commons.lang.StringUtils;

public abstract class BidirectionalRelationshipContractTestAbstract {

    private final Map<Class<?>, Instantiator> instantiators;
    private final Reflections reflections;
    private IndentPrinter out;
    
    protected BidirectionalRelationshipContractTestAbstract(
            final String packagePrefix, 
            ImmutableMap<Class<?>,Instantiator> instantiators) {
        reflections = new Reflections(Constants.packagePrefix);
        this.instantiators = Maps.newHashMap(instantiators);
        out = new IndentPrinter(new PrintWriter(ByteStreams.nullOutputStream()));
    }

    public BidirectionalRelationshipContractTestAbstract withLoggingTo(Writer out) {
        this.out = new IndentPrinter(out);
        return this;
    }
    
    public BidirectionalRelationshipContractTestAbstract withLoggingTo(PrintStream out) {
        this.out = new IndentPrinter(new PrintWriter(out));
        return this;
    }

    @Test
    public void searchAndTest() throws Exception {
        
        Set<Class<?>> entityTypes =
                Sets.newTreeSet(new Comparator<Class<?>>() {

                    @Override
                    public int compare(Class<?> o1, Class<?> o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
        entityTypes.addAll(reflections.getTypesAnnotatedWith(PersistenceCapable.class));
        
        for (Class<?> entityType : entityTypes) {
            out.println(entityType.getName());
            out.incrementIndent();
            try {
                process(entityType);
            } finally {
                out.decrementIndent();
            }
        }
        out.println("DONE");
        out.flush();
    }

    static class Parent {
        Class<?> entityType;
        Field childField;
        String mappedBy;
        Method getMethod;
        
        // for 1:m
        Method addToMethod;
        Method removeFromMethod;
        
        // if 1:1
        public Method modifyMethod;
        public Method clearMethod;
        
        public Collection<?> getChildren(Object parent) throws RuntimeException {
            try {
                return (Collection<?>) getMethod.invoke(parent);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        public void addToChildren(Object parent, Object child) {
            try {
                addToMethod.invoke(parent, child);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        public void removeFromChildren(Object parent, Object child) {
            try {
                removeFromMethod.invoke(parent, child);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        public Object getChild(Object parent) {
            try {
                return getMethod.invoke(parent);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        public void modifyChild(Object parent, Object child) {
            try {
                modifyMethod.invoke(parent, child);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        public void clearChild(Object parent) {
            try {
                clearMethod.invoke(parent);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    static class Child {
        Class<?> entityType;
        Field parentField;
        Method getMethod;
        Method modifyMethod;
        Method clearMethod;
        public Object getParent(Object child) {
            try {
                return getMethod.invoke(child);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        public void modifyParent(Object child, Object parent) {
            try {
                modifyMethod.invoke(child, parent);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        public void clearParent(Object child) {
            try {
                clearMethod.invoke(child);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void process(Class<?> entityType) {
        final Set<Field> mappedByFields = Reflections.getAllFields(entityType, persistentMappedBy);
        for (Field mappedByField : mappedByFields) {
            final Parent p = new Parent();
            p.entityType = entityType;
            p.childField = mappedByField;
            try {
                out.println("processing " + p.entityType.getSimpleName() + "#" + p.childField.getName());
                out.incrementIndent();
                process(p);
            } finally {
                out.decrementIndent();
            }
        }
    }
    
    private void process(Parent p) {

        // mappedBy
        final Persistent persistentAnnotation = p.childField.getAnnotation(Persistent.class);
        p.mappedBy = persistentAnnotation.mappedBy();

        // getMethod
        final String getMethod = "get" + StringUtils.capitalize(p.childField.getName());
        final Set<Method> getMethods = Reflections.getAllMethods(p.entityType, ReflectionUtils.withName(getMethod));
        assertThat(p.entityType.getName()+"#"+p.childField.getName()+ ": could not locate getter", getMethods.size(), is(1));
        p.getMethod = firstIn(getMethods);
        
        final Child c = new Child();

        final Class<?> returnType = p.getMethod.getReturnType();
        if(Collection.class.isAssignableFrom(returnType)) {
            // addToMethod
            final String addToMethod = "addTo" + StringUtils.capitalize(p.childField.getName());
            final Set<Method> addToMethods = Reflections.getAllMethods(p.entityType, 
                    Predicates.and(ReflectionUtils.withName(addToMethod), ReflectionUtils.withParametersCount(1), withEntityParameter()));
            if(addToMethods.size() != 1) {
                // just skip
                out.println("no addToXxx() method");
                return;
            }
            p.addToMethod = firstIn(addToMethods);

            // removeFromMethod
            final String removeFromMethod = "removeFrom" + StringUtils.capitalize(p.childField.getName());
            final Set<Method> removeFromMethods = Reflections.getAllMethods(p.entityType, 
                    Predicates.and(ReflectionUtils.withName(removeFromMethod), ReflectionUtils.withParametersCount(1), withEntityParameter()));
            if(removeFromMethods.size() != 1) {
                // just skip
                out.println("no removeFromXxx() method");
                return;
            }
            p.removeFromMethod = firstIn(removeFromMethods);

            // child's entityType
            final Class<?> addToParameterType = p.addToMethod.getParameterTypes()[0];
            final Class<?> removeFromParameterType = p.removeFromMethod.getParameterTypes()[0];
            
            assertThat(p.entityType.getName() + "#" + p.addToMethod.getName() + " and " + p.removeFromMethod.getName() + " should have the same parameter type",
                    addToParameterType == removeFromParameterType, is(true));
        
            c.entityType = addToParameterType;
        } else {
            
            // modify
            String modifyMethod = "modify" + StringUtils.capitalize(p.childField.getName());
            final Set<Method> modifyMethods = Reflections.getAllMethods(p.entityType, 
                    Predicates.and(Reflections.withName(modifyMethod), ReflectionUtils.withParametersCount(1), withEntityParameter()));
            if(modifyMethods.size() != 1) {
                // just skip
                out.println("no modifyXxx() method");
                return;
            }
            p.modifyMethod = firstIn(modifyMethods);
            
            // clear
            String clearMethod = "clear" + StringUtils.capitalize(p.childField.getName());
            final Set<Method> clearMethods = Reflections.getAllMethods(p.entityType, 
                    Predicates.and(Reflections.withName(clearMethod), ReflectionUtils.withParametersCount(0)));
            if(clearMethods.size() != 1) {
                // just skip
                out.println("no clearXxx() method");
                return;
            }
            p.clearMethod = firstIn(clearMethods);

            // child's entityType
            c.entityType = p.modifyMethod.getParameterTypes()[0];
        }
        
        final Instantiator parentInstantiator = instantiatorFor(p.entityType);
        if(parentInstantiator == null) {
            out.println("no instantiator for " + p.entityType.getName());
            // just skip
            return;
        }
        final Instantiator childInstantiator = instantiatorFor(c.entityType);
        if(childInstantiator == null) {
            out.println("no instantiator for " + c.entityType.getName());
            // just skip
            return;
        }
        
        process(p, c);
    }

    private Instantiator instantiatorFor(final Class<?> cls) {
        Instantiator instantiator = instantiators.get(cls);
        if(instantiator != null) {
            return instantiator;
        }
        
        instantiator = doInstantiatorFor(cls);
        
        if(instantiator != null) {
            // check it works instantiator
            try {
                @SuppressWarnings("unused")
                final Object dummy = instantiator.instantiate();
            } catch(RuntimeException ex) {
                instantiator = Instantiator.NOOP;
            }
        } else {
            instantiator = Instantiator.NOOP;
        }
        
        instantiators.put(cls, instantiator);
        return instantiator != Instantiator.NOOP? instantiator: null;
    }

    /**
     * Default just tries to use the {@link InstantiatorSimple};
     * subclasses can override with more sophisticated implementations if required.
     */
    protected Instantiator doInstantiatorFor(final Class<?> cls) {
        return new InstantiatorSimple(cls);
    }

    public static <T> Predicate<Field> withTypeAssignableFrom(final Class<T> type) {
        return new Predicate<Field>() {
            public boolean apply(Field input) {
                return input != null && input.getType().isAssignableFrom(type);
            }
        };
    }

    public static <T> Predicate<Method> withReturnTypeAssignableFrom(final Class<T> type) {
        return new Predicate<Method>() {
            public boolean apply(Method input) {
                return input != null && input.getReturnType().isAssignableFrom(type);
            }
        };
    }

    public static Predicate<Method> withParametersAssignableFrom(final Class<?>... types) {
        return new Predicate<Method>() {
            public boolean apply(Method input) {
                if (input != null) {
                    Class<?>[] parameterTypes = input.getParameterTypes();
                    if (parameterTypes.length == types.length) {
                        for (int i = 0; i < parameterTypes.length; i++) {
                            if (!parameterTypes[i].isAssignableFrom(types[i])) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
        };
    }

    private void process(Parent p, Child c) {
        
        // mappedBy field
        final Set<Field> parentFields = Reflections.getAllFields(c.entityType, Predicates.and(Reflections.withName(p.mappedBy), withTypeAssignableFrom(p.entityType)));

        assertThat(c.entityType.getName()+  ": could not locate '" + p.mappedBy + "' field, returning supertype of " + p.entityType.getSimpleName() +", (as per @Persistent(mappedBy=...) in parent "+ p.entityType.getSimpleName()+")", parentFields.size(), is(1));
        c.parentField = firstIn(parentFields);

        // getter
        String getterMethod = "get" + StringUtils.capitalize(c.parentField.getName());
        final Set<Method> getterMethods = Reflections.getAllMethods(c.entityType, 
                Predicates.and(Reflections.withName(getterMethod), ReflectionUtils.withParametersCount(0), withReturnTypeAssignableFrom(p.entityType)));
        assertThat(c.entityType.getName() + "#" + c.parentField.getName() +  ": could not locate getter " + getterMethod + "() returning supertype of " + p.entityType.getSimpleName(), getterMethods.size(), is(1));
        c.getMethod = firstIn(getterMethods);

        // modify
        String modifyMethod = "modify" + StringUtils.capitalize(c.parentField.getName());
        final Set<Method> modifyMethods = Reflections.getAllMethods(c.entityType, 
                Predicates.and(Reflections.withName(modifyMethod), ReflectionUtils.withParametersCount(1), withParametersAssignableFrom(p.entityType)));
        if(modifyMethods.size() != 1) {
            // just skip
            return;
        }
        c.modifyMethod = firstIn(modifyMethods);
        
        // clear
        String clearMethod = "clear" + StringUtils.capitalize(c.parentField.getName());
        final Set<Method> clearMethods = Reflections.getAllMethods(c.entityType, 
                Predicates.and(Reflections.withName(clearMethod), ReflectionUtils.withParametersCount(0)));
        if(clearMethods.size() != 1) {
            // just skip
            return;
        }
        c.clearMethod = firstIn(clearMethods);

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

    private Predicate<? super Method> withEntityParameter() {
        return new Predicate<Method>() {
            public boolean apply(Method m) {
                final Class<?> parameterType = m.getParameterTypes()[0];
                return parameterType.isAnnotationPresent(PersistenceCapable.class);
            }
        };
    }

    private Object newParent(Parent p) {
        return newInstance(p.entityType);
    }
    private Object newChild(Child c) {
        return newInstance(c.entityType);
    }

    private Object newInstance(final Class<?> entityType) {
        final Instantiator instantiator = instantiatorFor(entityType);
        return instantiator.instantiate();
    }

    private void exercise(Parent p, Child c) {
        final boolean oneToMany = p.addToMethod != null;
        out.println("exercising " + p.entityType.getSimpleName() + "#" + p.childField.getName() + " 1:" + (oneToMany?"m":"1") + " "+ c.entityType.getSimpleName() + "#" + c.parentField.getName());
        out.incrementIndent();
        try {
            if(oneToMany) {
                // 1:m
                
                // add
                oneToManyParentAddTo(p, c);
                oneToManyParentAddToWhenAlreadyChild(p, c);
                oneToManyParentAddToWhenNull(p, c);
                oneToManyChildModify(p, c);
                oneToManyChildModifyWhenAlreadyParent(p, c);
                oneToManyChildModifyWhenNull(p, c);
                
                // move (update)
                oneToManyChildModifyToNewParent(p, c);
                oneToManyChildModifyToExistingParent(p, c);
                
                // delete
                oneToManyParentRemoveFrom(p, c);
                oneToManyParentRemoveFromWhenNotAssociated(p, c);
                oneToManyParentRemoveFromWhenNull(p, c);
                oneToManyChildClear(p, c);
                oneToManyChildClearWhenNotAssociated(p, c);
            } else {
                // 1:1

                // add
                oneToOneParentModify(p, c);
                oneToOneParentModifyWhenAlreadyChild(p, c);
                oneToOneParentModifyWhenNull(p, c);
                oneToOneChildModify(p, c);
                oneToOneChildModifyWhenAlreadyParent(p, c);
                oneToOneChildModifyWhenNull(p, c);
                
                // move (update)
                oneToOneChildModifyToNewParent(p, c);
                oneToOneChildModifyToExistingParent(p, c);
                
                // delete
                oneToOneParentClear(p, c);
                oneToOneChildClear(p, c);
                oneToOneChildClearWhenNotAssociated(p, c);
            }
            
        } finally {
            out.decrementIndent();
        }

    }

    ////////////////
    // 1:m
    ////////////////
    
    private void oneToManyParentAddTo(Parent p, Child c) {

        out.println("oneToManyParentAddTo");
        
        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);
        

        // when
        p.addToChildren(parent1, child1);

        // then
        assertThat("parent contains child", p.getChildren(parent1), Matchers.containsInAnyOrder(child1));
        assertThat("child references parent", c.getParent(child1), is(parent1));
    }

    private void oneToManyParentAddToWhenAlreadyChild(Parent p, Child c) {

        out.println("oneToManyParentAddToWhenAlreadyChild");

        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);

        // given
        p.addToChildren(parent1, child1);
        
        // when
        p.addToChildren(parent1, child1);

        // then
        assertThat("parent still contains child", p.getChildren(parent1), Matchers.containsInAnyOrder(child1));
        assertThat("child still references parent", c.getParent(child1), is(parent1));
    }
    
    private void oneToManyParentAddToWhenNull(Parent p, Child c) {

        out.println("oneToManyParentAddToWhenNull");

        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);

        // when
        p.addToChildren(parent1, null);

        // then
        assertThat("parent does not have any children", p.getChildren(parent1).isEmpty(), is(true));
    }
    
    private void oneToManyChildModify(Parent p, Child c) {

        out.println("oneToManyChildModify");

        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);

        // when
        c.modifyParent(child1, parent1);

        // then
        assertThat("parent contains child", p.getChildren(parent1), Matchers.containsInAnyOrder(child1));
        assertThat("child references parent", c.getParent(child1), is(parent1));
    }

    private void oneToManyChildModifyWhenAlreadyParent(Parent p, Child c) {

        out.println("oneToManyChildModifyWhenAlreadyParent");

        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);

        // given
        c.modifyParent(child1, parent1);
        
        // when
        c.modifyParent(child1, parent1);

        // then
        assertThat("parent still contains child", p.getChildren(parent1), Matchers.containsInAnyOrder(child1));
        assertThat("child still references parent", c.getParent(child1), is(parent1));
    }


    private void oneToManyChildModifyWhenNull(Parent p, Child c) {

        out.println("oneToManyChildModifyWhenNull");

        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);

        // when
        c.modifyParent(child1, null);
        
        // then
        assertThat("child does not reference any parent", c.getParent(child1), is(nullValue()));
    }
    

    private void oneToManyChildModifyToNewParent(Parent p, Child c) {

        out.println("oneToManyChildModifyToNewParent");

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
        assertThat("parent 1 no longer has any children", p.getChildren(parent1).isEmpty(), is(true));
        assertThat("parent 2 now has both children", p.getChildren(parent2), Matchers.containsInAnyOrder(child1, child2));
        assertThat("child 1 now references parent 2", c.getParent(child1), is(parent2));
        assertThat("child 2 still references parent 2", c.getParent(child2), is(parent2));
    }
    
    private void oneToManyChildModifyToExistingParent(Parent p, Child c) {

        out.println("oneToManyChildModifyToExistingParent");

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
        assertThat("parent 1 still contains child 1", p.getChildren(parent1), Matchers.containsInAnyOrder(child1));
        assertThat("parent 2 still contains child 2", p.getChildren(parent2), Matchers.containsInAnyOrder(child2));
        assertThat("child 1 still references parent 1", c.getParent(child1), is(parent1));
        assertThat("child 2 still references parent 2", c.getParent(child2), is(parent2));
    }

    private void oneToManyParentRemoveFrom(Parent p, Child c) {

        out.println("oneToManyParentRemoveFrom");

        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);
        
        // given
        p.addToChildren(parent1, child1);
        
        // when
        p.removeFromChildren(parent1, child1);

        // then
        assertThat("parent no longer contains child", p.getChildren(parent1).isEmpty(), is(true));
        assertThat("child no longer references parent", c.getParent(child1), is(nullValue()));
    }

    private void oneToManyParentRemoveFromWhenNull(Parent p, Child c) {

        out.println("oneToManyParentRemoveFromWhenNull");

        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);
        
        // given
        p.addToChildren(parent1, child1);
        
        // when
        p.removeFromChildren(parent1, null);

        // then
        assertThat("parent still contains child", p.getChildren(parent1), Matchers.containsInAnyOrder(child1));
        assertThat("child still references parent", c.getParent(child1), is(parent1));
    }
    
    private void oneToManyParentRemoveFromWhenNotAssociated(Parent p, Child c) {

        out.println("oneToManyParentRemoveFromWhenNotAssociated");

        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);
        
        // given
        p.addToChildren(parent1, child1);
        
        // when
        p.removeFromChildren(parent1, child2);
        
        // then
        assertThat("parent still contains child", p.getChildren(parent1), Matchers.containsInAnyOrder(child1));
        assertThat("child still references parent", c.getParent(child1), is(parent1));
    }
    
    private void oneToManyChildClear(Parent p, Child c) {
        
        out.println("oneToManyChildClear");

        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);
        
        // given
        p.addToChildren(parent1, child1);
        
        // when
        c.clearParent(child1);

        // then
        assertThat("parent no longer contains child", p.getChildren(parent1).isEmpty(), is(true));
        assertThat("child no longer references parent", c.getParent(child1), is(nullValue()));
    }

    private void oneToManyChildClearWhenNotAssociated(Parent p, Child c) {

        out.println("oneToManyChildClearWhenNotAssociated");

        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);
        
        // when
        c.clearParent(child1);

        // then
        assertThat("parent still does not reference child", p.getChildren(parent1).isEmpty(), is(true));
        assertThat("child still does not reference parent", c.getParent(child1), is(nullValue()));
    }


    
    ////////////////
    // 1:1
    ////////////////
    
    private void oneToOneParentModify(Parent p, Child c) {
        
        out.println("oneToOneParentModify");

        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);
        

        // when
        p.modifyChild(parent1, child1);

        // then
        assertThat("parent references child", p.getChild(parent1), is(child1));
        assertThat("child references parent", c.getParent(child1), is(parent1));
    }

    private void oneToOneParentModifyWhenAlreadyChild(Parent p, Child c) {
        
        out.println("oneToOneParentModifyWhenAlreadyChild");

        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);

        // given
        p.modifyChild(parent1, child1);
        
        // when
        p.modifyChild(parent1, child1);

        // then
        assertThat("parent still references child", p.getChild(parent1), is(child1));
        assertThat("child still references parent", c.getParent(child1), is(parent1));
    }
    
    private void oneToOneParentModifyWhenNull(Parent p, Child c) {

        out.println("oneToOneParentModifyWhenNull");

        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);

        // when
        p.modifyChild(parent1, null);

        // then
        assertThat("parent still references child", p.getChild(parent1), is(nullValue()));
    }
    
    private void oneToOneChildModify(Parent p, Child c) {

        out.println("oneToOneChildModify");
        
        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);

        // when
        c.modifyParent(child1, parent1);

        // then
        assertThat("parent references child", p.getChild(parent1), is(child1));
        assertThat("child references parent", c.getParent(child1), is(parent1));
    }

    private void oneToOneChildModifyWhenAlreadyParent(Parent p, Child c) {

        out.println("oneToOneChildModifyWhenAlreadyParent");

        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);

        // given
        c.modifyParent(child1, parent1);
        
        // when
        c.modifyParent(child1, parent1);

        // then
        assertThat("parent still references child", p.getChild(parent1), is(child1));
        assertThat("child still references parent", c.getParent(child1), is(parent1));
    }


    private void oneToOneChildModifyWhenNull(Parent p, Child c) {

        out.println("oneToOneChildModifyWhenNull");

        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);

        // when
        c.modifyParent(child1, null);
        
        // then
        assertThat("child still references parent", c.getParent(child1), is(nullValue()));
    }
    

    private void oneToOneChildModifyToNewParent(Parent p, Child c) {
        
        out.println("oneToOneChildModifyToNewParent");

        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);
        
        // given
        p.modifyChild(parent1, child1);
        p.modifyChild(parent2, child2);
        
        // when
        c.modifyParent(child1, parent2);

        // then
        assertThat("parent 1 no longer references child 1", p.getChild(parent1), is(nullValue()));
        assertThat("parent 2 now references child 1", p.getChild(parent2), is(child1));
        assertThat("child 1 now references parent 2", c.getParent(child1), is(parent2));
        assertThat("child 2, as a side-effect, no longer references parent 2", c.getParent(child2), is(nullValue()));
    }
    
    private void oneToOneChildModifyToExistingParent(Parent p, Child c) {
        
        out.println("oneToOneChildModifyToExistingParent");

        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);
        
        // given
        p.modifyChild(parent1, child1);
        p.modifyChild(parent2, child2);
        
        // when
        c.modifyParent(child1, parent1);
        
        // then
        assertThat("parent 1 still references child 1", p.getChild(parent1), is(child1));
        assertThat("parent 2 still references child 2", p.getChild(parent2), is(child2));
        assertThat("child 1 still references parent 1", c.getParent(child1), is(parent1));
        assertThat("child 2 still references parent 2", c.getParent(child2), is(parent2));
    }

    private void oneToOneParentClear(Parent p, Child c) {

        out.println("oneToOneParentClear");

        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);
        
        // given
        p.modifyChild(parent1, child1);
        
        // when
        p.clearChild(parent1);

        // then
        assertThat("parent no longer references child", p.getChild(parent1), is(nullValue()));
        assertThat("child no longer references parent", c.getParent(child1), is(nullValue()));
    }

    
    private void oneToOneChildClear(Parent p, Child c) {
        
        out.println("oneToOneChildClear");

        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);
        
        // given
        p.modifyChild(parent1, child1);
        
        // when
        c.clearParent(child1);

        // then
        assertThat("parent no longer references child", p.getChild(parent1), is(nullValue()));
        assertThat("child no longer references parent", c.getParent(child1), is(nullValue()));
    }

    private void oneToOneChildClearWhenNotAssociated(Parent p, Child c) {
        
        out.println("oneToOneChildClearWhenNotAssociated");

        Object parent1 = newParent(p);
        Object parent2 = newParent(p);
        
        Object child1 = newChild(c);
        Object child2 = newChild(c);
        
        // when
        c.clearParent(child1);

        // then
        assertThat("parent still does not reference child", p.getChild(parent1), is(nullValue()));
        assertThat("child still does not reference parent", c.getParent(child1), is(nullValue()));
    }

}
