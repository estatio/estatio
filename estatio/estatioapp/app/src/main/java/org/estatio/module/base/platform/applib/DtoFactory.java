package org.estatio.module.base.platform.applib;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.isis.applib.annotation.Programmatic;

/**
 * Chain of responsibility pattern.
 */
public interface DtoFactory {

    @Programmatic
    boolean accepts(
            final Object object,
            final List<MediaType> acceptableMediaTypes);

    /**
     * Only called if {@link #accepts(Object, List)} returns true.
     */
    @Programmatic
    Object newDto(Object object, List<MediaType> acceptableMediaTypes);

}
