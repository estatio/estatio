package org.estatio.dom;

import org.apache.isis.applib.util.ObjectContracts.ToStringEvaluator;

public interface WithDescriptionGetter {

    public String getDescription();
    
    public static class ToString {
        private ToString() {}
        public static ToStringEvaluator evaluator() {
            return new ToStringEvaluator() {
                @Override
                public boolean canEvaluate(Object o) {
                    return o instanceof WithDescriptionGetter;
                }
                
                @Override
                public String evaluate(Object o) {
                    return ((WithDescriptionGetter)o).getDescription();
                }
            };
        }
    }

}
