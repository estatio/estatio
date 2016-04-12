package org.estatio.app.services.properties;

import com.google.common.collect.ComparisonChain;

import org.apache.isis.applib.annotation.ViewModel;

import lombok.Getter;
import lombok.Setter;

@ViewModel
public class ApplicationProperty implements Comparable<ApplicationProperty> {

    public ApplicationProperty(){}

    public ApplicationProperty(final String key, final String value){
        this.key = key;
        this.value = value;
    }

    @Getter @Setter
    private String key;

    @Getter @Setter
    private String value;

    @Override
    public int compareTo(final ApplicationProperty o) {
        return ComparisonChain.start()
                .compare(getKey(), o.getKey())
        .result();
    }
}
