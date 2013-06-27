package org.estatio.dom;

import org.apache.isis.applib.util.ObjectContracts.ToStringEvaluator;

public interface WithTitleGetter {

    public String getTitle();
    
    public static class ToString {
        private ToString() {}
        public static ToStringEvaluator evaluator() {
            return new ToStringEvaluator() {
                @Override
                public boolean canEvaluate(Object o) {
                    return o instanceof WithTitleGetter;
                }
                
                @Override
                public String evaluate(Object o) {
                    return ((WithTitleGetter)o).getTitle();
                }
            };
        }
    }

}
