package org.estatio.dom;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;

import com.google.common.collect.Maps;


public class EstatioInteractionCache {

    private static ThreadLocal<EstatioInteractionCache> instance = new ThreadLocal<EstatioInteractionCache>(){};

    static class CacheKey {
        private final Class<?> callingClass;
        private final String methodName;
        private final Object[] keys;
        
        public CacheKey(Class<?> callingClass, String methodName, Object... keys) {
            this.callingClass = callingClass;
            this.methodName = methodName;
            this.keys = keys;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CacheKey other = (CacheKey) obj;
            if (callingClass == null) {
                if (other.callingClass != null)
                    return false;
            } else if (!callingClass.equals(other.callingClass))
                return false;
            if (!Arrays.equals(keys, other.keys))
                return false;
            if (methodName == null) {
                if (other.methodName != null)
                    return false;
            } else if (!methodName.equals(other.methodName))
                return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((callingClass == null) ? 0 : callingClass.hashCode());
            result = prime * result + Arrays.hashCode(keys);
            result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
            return result;
        }
    }
    
    public static class CacheValue {
        public CacheValue(Object object) {
            this.object = object;
        }
        Object object;
    }
    
    private final Map<CacheKey, CacheValue> cache = Maps.newHashMap();

    public static boolean startInteraction() {
        if(get() == null) {
            EstatioInteractionCache.instance.set(new EstatioInteractionCache());
            return true;
        } else {
            return false;
        }
    }
    
    
    public static <T> T execute(Callable<T> callable, Class<?> callingClass, String methodName, Object... keys) {
        try {
            if(get() != null) {
                return get().executeOnInstance(callable, callingClass, methodName, keys);
            } else {
                return callable.call();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @SuppressWarnings("unchecked")
    private <T> T executeOnInstance(Callable<T> callable, Class<?> callingClass, String methodName, Object... keys) throws Exception {
        CacheKey ck = new CacheKey(callingClass, methodName, keys);
        CacheValue cv = cache.get(ck);
        if(cv != null) { 
            return (T) cv.object;
        }
        // cache miss, so get the result, and cache
        T result = callable.call();
        cache.put(ck, new CacheValue(result));
        return result;
    }

    public static void endInteraction() {
        endInteraction(true);
    }

    public static void endInteraction(boolean wasStarted) {
        if(wasStarted) {
            EstatioInteractionCache.instance.set(null);
        }
    }

    static EstatioInteractionCache get() {
        return EstatioInteractionCache.instance.get();
    }

}
