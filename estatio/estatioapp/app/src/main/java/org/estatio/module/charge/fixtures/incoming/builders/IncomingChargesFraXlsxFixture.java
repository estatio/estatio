package org.estatio.module.charge.fixtures.incoming.builders;

import java.util.List;

import javax.inject.Inject;

import com.google.common.io.Resources;

import org.apache.isis.applib.annotation.DomainObject;

import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.WorksheetSpec;
import org.isisaddons.module.excel.dom.util.Mode;

import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;

@DomainObject(
        objectType = "org.estatio.module.charge.fixtures.incoming.builders.CapexChargeHierarchyXlsxFixture"
)
public class IncomingChargesFraXlsxFixture extends ExcelFixture2 {

    @Inject
    ChargeRepository chargeRepository;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        Charge before = chargeRepository.findByReference("WORKS");
        List<Charge> beforeAll = chargeRepository.listAll();

        setExcelResource(Resources.getResource(getClass(), "IncomingChargesFra.xlsx"));

        setMatcher(sheetName -> {
            if(sheetName.startsWith("ChargeHierarchy")) {
                return new WorksheetSpec(
                        rowFactoryFor(IncomingChargeHandler.class, executionContext),
                        sheetName,
                        Mode.STRICT);
            }
            else {
                return null;
            }
        });


        super.execute(executionContext);

        Charge after = chargeRepository.findByReference("WORKS");
        List<Charge> afterAll = chargeRepository.listAll();

    }




}
