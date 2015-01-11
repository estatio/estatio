package org.estatio.app.interactivemap;

import com.google.common.base.Objects;

public class Color {
    
    final private String color;
    
    final private String label;
    
    public Color(String color, String label) {
        this.color = color;
        this.label = label;
    }

    public String getColor() {
        return color;
    }
    
    public String getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object other) {
        return Objects.equal(this, other);
    }

    @Override
    public int hashCode() {
        int result = color.hashCode();
        result = 31 * result + label.hashCode();
        return result;
    }
}
