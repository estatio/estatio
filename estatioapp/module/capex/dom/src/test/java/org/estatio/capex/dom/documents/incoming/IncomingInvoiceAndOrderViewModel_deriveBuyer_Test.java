package org.estatio.capex.dom.documents.incoming;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.documents.order.IncomingOrderViewModel;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.OwnershipType;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.ownership.FixedAssetOwnership;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Party;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoiceAndOrderViewModel_deriveBuyer_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    PaperclipRepository mockPaperclipRepository;

    @Mock
    Document mockDocument;


    @Test
    public void deriveBuyer_works_when_fixedAsset_has_owner(){

        // given
        Party owner = new Organisation();
        FixedAsset asset = new Property();
        FixedAssetOwnership ownership = new FixedAssetOwnership();
        ownership.setOwnershipType(OwnershipType.FULL);
        ownership.setOwner(owner);
        asset.getOwners().add(ownership);

        // expecting
        context.checking(new Expectations() {{
            allowing(mockPaperclipRepository).paperclipAttaches(mockDocument, FixedAsset.class);
            will(returnValue(asset));
        }});

        // when
        IncomingOrderViewModel vm = new IncomingOrderViewModel(mockDocument);
        vm.paperclipRepository = mockPaperclipRepository;
        vm.init();

        // then
        assertThat(vm.getBuyer()).isEqualTo(owner);
    }

}