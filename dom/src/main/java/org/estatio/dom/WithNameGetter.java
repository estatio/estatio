package org.estatio.dom;

import com.google.common.base.Objects;

import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.util.ObjectContracts.ToStringEvaluator;

public interface WithNameGetter {

    public String getName();
    
    public static class ToString {
        private ToString() {}
        public static String of(WithNameGetter p) {
            return Objects.toStringHelper(p)
                    .add("name", p.getName())
                    .toString();
        }
        public static ObjectContracts evaluatorFor(ObjectContracts objectContracts) {
            objectContracts.with(new ToStringEvaluator() {
                @Override
                public boolean canEvaluate(Object o) {
                    return o instanceof WithNameGetter;
                }
                
                @Override
                public String evaluate(Object o) {
                    return ((WithNameGetter)o).getName();
                }
            });
            return objectContracts;
        }
    }

}
