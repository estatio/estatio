package org.estatio.dom;

public interface Titled<T extends Titled<T>> extends Comparable<T> {
    
    String title();

}
