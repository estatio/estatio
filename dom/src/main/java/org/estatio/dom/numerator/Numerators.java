package org.estatio.dom.numerator;

import java.math.BigInteger;
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
    public Numerator retrieve(final String key, String description) {
        Numerator numerator = firstMatch(Numerator.class, new Filter<Numerator>() {
            @Override
            public boolean accept(final Numerator t) {
                return t.getKey() == key;
            }
        });
        return numerator == null ? create(key, description) : numerator;
    }

    @Hidden
    public Numerator create(String key, String description) {
        Numerator numerator = newTransientInstance(Numerator.class);
        numerator.setKey(key);
        numerator.setLastIncrement(BigInteger.ZERO);
        numerator.setDescription(description);
        persist(numerator);
        return numerator;
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
