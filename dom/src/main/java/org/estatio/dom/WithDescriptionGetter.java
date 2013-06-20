package org.estatio.dom;

import com.google.common.base.Objects;

import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.util.ObjectContracts.ToStringEvaluator;

public interface WithDescriptionGetter {

    public String getDescription();
    
    public static class ToString {
        private ToString() {}
        public static String of(WithDescriptionGetter p) {
            return Objects.toStringHelper(p)
                    .add("description", p.getDescription())
                    .toString();
        }
        public static ObjectContracts evaluatorFor(ObjectContracts objectContracts) {
            objectContracts.with(new ToStringEvaluator() {
                @Override
                public boolean canEvaluate(Object o) {
                    return o instanceof WithDescriptionGetter;
                }
                
                @Override
                public String evaluate(Object o) {
                    return ((WithDescriptionGetter)o).getDescription();
                }
            });
            return objectContracts;
        }
    }

}
