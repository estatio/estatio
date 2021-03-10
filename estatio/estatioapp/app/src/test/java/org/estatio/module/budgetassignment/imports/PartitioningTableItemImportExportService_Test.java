package org.estatio.module.budgetassignment.imports;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class PartitioningTableItemImportExportService_Test {

    @Test
    public void allLinesHaveSameKeyTableName() {

        // given
        PartitioningTableItemImportExportService service = new PartitioningTableItemImportExportService();
        List<KeyItemImportExportLine> lines = new ArrayList<>();

        // when, then
        Assertions.assertThat(service.allLinesHaveSameKeyTableName(lines)).isTrue();

        // when
        KeyItemImportExportLine l1 = new KeyItemImportExportLine();
        l1.setKeyTableName(null);
        lines.add(l1);
        // then
        Assertions.assertThat(service.allLinesHaveSameKeyTableName(lines)).isFalse();

        // when
        l1.setKeyTableName("T1");
        // then
        Assertions.assertThat(service.allLinesHaveSameKeyTableName(lines)).isTrue();

        // when
        KeyItemImportExportLine l2 = new KeyItemImportExportLine();
        l2.setKeyTableName("T1");
        lines.add(l2);
        // then
        Assertions.assertThat(service.allLinesHaveSameKeyTableName(lines)).isTrue();

        // when
        KeyItemImportExportLine l3 = new KeyItemImportExportLine();
        l3.setKeyTableName("TXX");
        lines.add(l3);
        // then
        Assertions.assertThat(service.allLinesHaveSameKeyTableName(lines)).isFalse();

        // when
        l3.setKeyTableName(null);
        // then
        Assertions.assertThat(service.allLinesHaveSameKeyTableName(lines)).isFalse();

    }
}