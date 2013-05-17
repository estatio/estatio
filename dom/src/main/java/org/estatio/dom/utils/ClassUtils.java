package org.estatio.dom.utils;

public final class ClassUtils {
    
    private ClassUtils(){}

    @SuppressWarnings("unchecked")
    public static <T> Class<? extends T> load(String clsName, Class<T> cls)  {
        Class<?> clsx;
        try {
            clsx = Thread.currentThread().getContextClassLoader().loadClass(clsName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class '" + clsName + "' not found"); 
        }
        if (!cls.isAssignableFrom(clsx)) { 
            throw new IllegalArgumentException("Class '" + clsName + "' not a subclass of " + cls.getName()); 
        } 
        return (Class<? extends T>) clsx;
    }

}
