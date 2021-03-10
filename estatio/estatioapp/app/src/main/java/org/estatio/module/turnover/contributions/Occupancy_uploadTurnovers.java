package org.estatio.module.turnover.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.util.Mode;

import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.imports.TurnoverImport;

@Mixin
public class Occupancy_uploadTurnovers {

    private static String fileSuffix = ".xlsx";

    private final Occupancy occupancy;

    public Occupancy_uploadTurnovers(Occupancy occupancy) {
        this.occupancy = occupancy;
    }

    @Action()
    public Occupancy $$(final Blob sheet) {
        List<TurnoverImport> importLines = excelService.fromExcel(sheet, TurnoverImport.class, "TurnoverImport", Mode.STRICT);
        importLines.forEach(l->{
            l.importData(null);
        });
        return occupancy;
    }

    @Inject
    TurnoverRepository turnoverRepository;

    @Inject
    TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject
    ExcelService excelService;

}
