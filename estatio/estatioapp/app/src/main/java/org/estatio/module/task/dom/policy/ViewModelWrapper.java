package org.estatio.module.task.dom.policy;

/**
 * A view model that wraps an underlying (entity) domain object.
 */
public interface ViewModelWrapper<T> {

    T getDomainObject();

}
