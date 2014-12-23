/**
 * Defines {@link org.estatio.dom.communicationchannel.CommunicationChannel} as an abstraction of a means for
 * two parties to interact with each other, along with a number of concrete implementations: 
 * {@link org.estatio.dom.communicationchannel.PhoneOrFaxNumber}, 
 * {@link org.estatio.dom.communicationchannel.EmailAddress} and 
 * {@link org.estatio.dom.communicationchannel.PostalAddress}. 
 * 
 * <p>
 * Every channel has a {@link org.estatio.dom.communicationchannel.CommunicationChannel#getType() associated}
 * {@link org.estatio.dom.communicationchannel.CommunicationChannelType type}, this acting as a power-type.
 *  
 * <p>
 * Every channel also has an {@link org.estatio.dom.communicationchannel.CommunicationChannel#getOwner() associated}
 * {@link org.estatio.dom.communicationchannel.CommunicationChannelOwner owner}; this is a polymorphic association
 * with the implementations being <tt>FixedAsset</tt> and <tt>Party</tt>.
 * 
 */
package org.estatio.dom.communicationchannel;