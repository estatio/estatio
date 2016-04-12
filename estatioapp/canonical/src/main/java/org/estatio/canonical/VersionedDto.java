package org.estatio.canonical;

public interface VersionedDto {

    /**
     * This value should be fixed, and only incremented whenever there is a change to the namespace.
     */
    String getMajorVersion();

    /**
     * This value should be incremented whenever the DTO is updated in a non-breaking, backwardly compatible fashion.
     */
    String getMinorVersion();

}
