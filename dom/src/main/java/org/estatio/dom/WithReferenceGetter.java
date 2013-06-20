package org.estatio.dom;

import com.google.common.base.Objects;

import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.util.ObjectContracts.ToStringEvaluator;

public interface WithReferenceGetter {

    public String getReference();
    
    public static class ToString {
        private ToString() {}
        public static String of(WithReferenceGetter p) {
            return Objects.toStringHelper(p)
                    .add("reference", p.getReference())
                    .toString();
        }
        public static ObjectContracts evaluatorFor(ObjectContracts objectContracts) {
            objectContracts.with(new ToStringEvaluator() {
                @Override
                public boolean canEvaluate(Object o) {
                    return o instanceof WithReferenceGetter;
                }
                
                @Override
                public String evaluate(Object o) {
                    return ((WithReferenceGetter)o).getReference();
                }
            });
            return objectContracts;
        }
    }

}
