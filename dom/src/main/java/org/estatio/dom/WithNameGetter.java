package org.estatio.dom;

import org.apache.isis.applib.util.ObjectContracts.ToStringEvaluator;

public interface WithNameGetter {

    public String getName();
    
    public static class ToString {
        private ToString() {}
        public static ToStringEvaluator evaluator() {
            return new ToStringEvaluator() {
                @Override
                public boolean canEvaluate(Object o) {
                    return o instanceof WithNameGetter;
                }
                
                @Override
                public String evaluate(Object o) {
                    return ((WithNameGetter)o).getName();
                }
            };
        }
    }

}
