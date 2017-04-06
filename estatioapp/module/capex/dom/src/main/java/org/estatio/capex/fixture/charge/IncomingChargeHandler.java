package org.estatio.capex.fixture.charge;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.FixtureAwareRowHandler;

import org.estatio.capex.dom.charge.IncomingCharge;
import org.estatio.capex.dom.charge.IncomingChargeRepository;

import lombok.Getter;
import lombok.Setter;

public class IncomingChargeHandler implements FixtureAwareRowHandler<IncomingChargeHandler> {

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String parent;

    @Getter @Setter
    private String atPath;

    /**
     * To allow for usage within fixture scripts also.
     */
    @Setter
    private FixtureScript.ExecutionContext executionContext;

    /**
     * To allow for usage within fixture scripts also.
     */
    @Setter
    private ExcelFixture2 excelFixture2;


    @Override
    public void handleRow(final IncomingChargeHandler previousRow) {

        if(executionContext != null && excelFixture2 != null) {
            executionContext.addResult(excelFixture2, this.handle(previousRow));
        }

    }

    private IncomingCharge handle(final IncomingChargeHandler previousRow) {
        final IncomingCharge parentObj = incomingChargeRepository.findByName(parent);

        return incomingChargeRepository.findOrCreate(name, parentObj, atPath);
    }

    @Inject
    IncomingChargeRepository incomingChargeRepository;

}

