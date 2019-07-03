package org.estatio.module.turnover.contributions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverReportingConfig;
import org.estatio.module.turnover.dom.TurnoverReportingConfigRepository;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnover.imports.TurnoverImport;

@Mixin
public class Occupancy_downloadTurnovers {

    private static String fileSuffix = ".xlsx";

    private final Occupancy occupancy;

    public Occupancy_downloadTurnovers(Occupancy occupancy) {
        this.occupancy = occupancy;
    }

    @Action()
    public Blob $$(final int year, final Type type, @Nullable final String filename) {
        TurnoverReportingConfig config = turnoverReportingConfigRepository.findUnique(occupancy, type);
        if (config == null) return null;
        final List<Turnover> turnovers = turnoverRepository.findByConfig(config).stream().filter(t->t.getDate().getYear() == year).collect(Collectors.toList());
        List<TurnoverImport> lines = new ArrayList<>();
        turnovers.forEach(t->{
            lines.add(new TurnoverImport(t));
        });
        return excelService.toExcel(lines, TurnoverImport.class, "TurnoverImport", filename == null ? filename() : filename.concat(fileSuffix));
    }

    private String filename(){
        StringBuffer buffer = new StringBuffer();
        buffer.append(occupancy.getLease().getReference());
        buffer.append(" - ");
        buffer.append(occupancy.getUnit().getReference());
        buffer.append(fileSuffix);
        return buffer.toString();
    }

    @Inject
    TurnoverRepository turnoverRepository;

    @Inject
    TurnoverReportingConfigRepository turnoverReportingConfigRepository;

    @Inject
    ExcelService excelService;

}
