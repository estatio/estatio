package org.estatio.dom;

import java.util.Set;

import org.junit.Test;
import org.reflections.Reflections;


/**
 * Automatically tests all enums implementing {@link TitledEnum}.
 */
public class TitledEnumContractTestAll_title{

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void searchAndTest() {
        Reflections reflections = new Reflections(Constants.packagePrefix);
        
        Set<Class<? extends TitledEnum>> subtypes = 
                reflections.getSubTypesOf(TitledEnum.class);
        for (Class<? extends TitledEnum> subtype : subtypes) {
            if(!Enum.class.isAssignableFrom(subtype)) {
                continue; // ignore non-enums
            }
            Class<? extends Enum> enumType = (Class<? extends Enum>) subtype;
            new TitledEnumContractTester(enumType).test();
        }
    }

}
