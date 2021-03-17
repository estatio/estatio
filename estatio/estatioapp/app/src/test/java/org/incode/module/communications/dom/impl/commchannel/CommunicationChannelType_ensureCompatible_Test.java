package org.incode.module.communications.dom.impl.commchannel;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class CommunicationChannelType_ensureCompatible_Test {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void happy_case() throws Exception {
        CommunicationChannelType.POSTAL_ADDRESS.ensureCompatible(PostalAddress.class);
        CommunicationChannelType.PHONE_NUMBER.ensureCompatible(PhoneOrFaxNumber.class);
        CommunicationChannelType.FAX_NUMBER.ensureCompatible(PhoneOrFaxNumber.class);
        CommunicationChannelType.EMAIL_ADDRESS.ensureCompatible(EmailAddress.class);
    }

    @Test
    public void POSTAL_ADDRESS_not_compatible_with_PhoneOrFaxNumber() throws Exception {

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Class 'PhoneOrFaxNumber' is not compatible with type of 'POSTAL_ADDRESS'");

        CommunicationChannelType.POSTAL_ADDRESS.ensureCompatible(PhoneOrFaxNumber.class);
    }

    @Test
    public void POSTAL_ADDRESS_not_compatible_with_EmailAddress() throws Exception {

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Class 'EmailAddress' is not compatible with type of 'POSTAL_ADDRESS'");

        CommunicationChannelType.POSTAL_ADDRESS.ensureCompatible(EmailAddress.class);
    }

    @Test
    public void PHONE_NUMBER_not_compatible_with_PostalAddress() throws Exception {

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Class 'PostalAddress' is not compatible with type of 'PHONE_NUMBER'");

        CommunicationChannelType.PHONE_NUMBER.ensureCompatible(PostalAddress.class);
    }


}