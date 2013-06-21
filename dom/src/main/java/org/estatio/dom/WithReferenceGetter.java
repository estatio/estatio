package org.estatio.dom;

import org.apache.isis.applib.util.ObjectContracts.ToStringEvaluator;

public interface WithReferenceGetter {

    public String getReference();
    
    public static class ToString {
        private ToString() {}
        public static ToStringEvaluator evaluator() {
            return new ToStringEvaluator() {
                @Override
                public boolean canEvaluate(Object o) {
                    return o instanceof WithReferenceGetter;
                }
                
                @Override
                public String evaluate(Object o) {
                    return ((WithReferenceGetter)o).getReference();
                }
            };
        }
    }

}
