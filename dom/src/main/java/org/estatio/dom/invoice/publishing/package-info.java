/**
 * Defines the content to be included when an {@link org.estatio.dom.invoice.Invoice} is published
 * through Isis' {@link org.apache.isis.applib.services.publish.PublishingService publishing service}.
 * 
 * <p>
 * Specifically, the {@link org.estatio.dom.invoice.Invoice}'s {@link org.estatio.dom.invoice.Invoice#getItems() items}
 * are included in the published message.
 */
package org.estatio.dom.invoice.publishing;