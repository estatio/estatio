/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.module.index.fixtures.builders;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.index.dom.Index;
import org.estatio.module.index.dom.IndexBase;
import org.estatio.module.index.dom.IndexValue;
import org.estatio.module.index.dom.api.IndexCreator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import static org.incode.module.base.integtests.VT.ld;


@EqualsAndHashCode(of={"reference", "applicationTenancy"}, callSuper = false)
@ToString(of={"reference", "applicationTenancy"})
@Accessors(chain = true)
public class IndexBuilder extends BuilderScriptAbstract<Index, IndexBuilder> {

    @Getter @Setter
    ApplicationTenancy applicationTenancy;
    @Getter @Setter
    String reference;
    @Getter @Setter
    String name;

    @Getter @Setter
    List<Base> bases;

    @AllArgsConstructor
    @Data
    public static class Base {
        final int year;
        final double factor;
        Value[] values = new Value[0];
    }

    @AllArgsConstructor
    @Data
    public static class Value {
        final int year;
        final double[] values;
        final double averageUNUSED;
    }
    @Getter
    private Index object;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        checkParam("reference", executionContext, String.class);
        checkParam("name", executionContext, String.class);

        final Index index = createIndex(applicationTenancy, reference, name, executionContext);
        for (final Base base : bases) {
            final IndexBase indexBase = createIndexBase(index, base.year, base.factor, executionContext);
            for (final Value value : base.values) {
                createIndexValues(indexBase, value.year, value.values, executionContext);
            }
        }
        object = index;
    }

    private Index createIndex(
            final ApplicationTenancy applicationTenancy,
            final String reference,
            final String name,
            final ExecutionContext executionContext) {
        final Index index = indexRepository.findOrCreateIndex(applicationTenancy, reference, name);
        return executionContext.addResult(this, index.getReference(), index);
    }

    private IndexBase createIndexBase(
            final Index index, final int year, final double factor,
            final ExecutionContext executionContext) {
        final IndexBase indexBase = index.findOrCreateBase(ld(year, 1, 1), BigDecimal.valueOf(factor));
        return executionContext.addResult(this, indexBase);
    }

    private void createIndexValues(
            final IndexBase indexBase, final int year, final double[] values,
            final ExecutionContext executionContext) {
        int i = 0;
        for (final double value : values) {
            final IndexValue indexValue = indexBase.newIndexValue(ld(year, i + 1, 1), BigDecimal.valueOf(value));
            executionContext.addResult(this, indexValue);
            i++;
        }
    }

    @Inject
    IndexCreator indexRepository;

}
