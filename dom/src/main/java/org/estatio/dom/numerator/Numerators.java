package org.estatio.dom.numerator;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.filter.Filter;

@Named("Numerators")
public class Numerators extends AbstractFactoryAndRepository {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "numerators";
    }

    public String iconName() {
        return "Numerators";
    }

    // }}

    @Hidden
    public Numerator create(NumeratorType type){
        Numerator numerator = type.create(getContainer());
        persist(numerator);
        return numerator;
    }
    
    //TODO: This naive implementation doesn't find the numerator, the JDO implementation does.
    public Numerator find(final NumeratorType type) {
        Numerator numerator = firstMatch(Numerator.class, new Filter<Numerator>() {
            @Override
            public boolean accept(final Numerator n) {
                return n.getType().equals(type);
            }
        });
        return numerator;
    }

    public Numerator findOrCreate(NumeratorType type){
        Numerator numerator = find(type);
        return numerator == null ? create(type) : numerator;
    }
    
    
    // {{ allNumerators
    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<Numerator> allNumerators() {
        List<Numerator> allInstances = allInstances(Numerator.class);
        return allInstances;
    }
    // }}

}
