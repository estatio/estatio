package com.eurocommercialproperties.estatio.dom.contactmechanism;

import java.util.List;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.QueryOnly;

public interface CommunicationChannelTypes {

	@QueryOnly
	@MemberOrder(sequence = "1")
	public CommunicationChannelType newCommunicationChannelType(
			String fullyQualifiedClassName);

	List<CommunicationChannel> allInstances();

}
