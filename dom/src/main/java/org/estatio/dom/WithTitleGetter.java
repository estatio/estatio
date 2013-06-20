package org.estatio.dom;

import com.google.common.base.Objects;

import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.util.ObjectContracts.ToStringEvaluator;

public interface WithTitleGetter {

    public String getTitle();
    
    public static class ToString {
        private ToString() {}
        public static String of(WithTitleGetter p) {
            return Objects.toStringHelper(p)
                    .add("title", p.getTitle())
                    .toString();
        }
        public static ObjectContracts evaluatorFor(ObjectContracts objectContracts) {
            objectContracts.with(new ToStringEvaluator() {
                @Override
                public boolean canEvaluate(Object o) {
                    return o instanceof WithTitleGetter;
                }
                
                @Override
                public String evaluate(Object o) {
                    return ((WithTitleGetter)o).getTitle();
                }
            });
            return objectContracts;
        }
    }

}
