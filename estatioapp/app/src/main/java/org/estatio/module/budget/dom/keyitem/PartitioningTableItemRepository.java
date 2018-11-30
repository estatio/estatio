/*
 * Copyright 2012-2015 Eurocommercial Properties NV
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.estatio.module.budget.dom.keyitem;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.budget.dom.keytable.PartitioningTable;

@DomainService(repositoryFor = KeyItem.class, nature = NatureOfService.DOMAIN)
@DomainServiceLayout()
public class PartitioningTableItemRepository extends UdoDomainRepositoryAndFactory<PartitioningTableItem> {

    public PartitioningTableItemRepository() {
        super(PartitioningTableItemRepository.class, PartitioningTableItem.class);
    }

    @Programmatic
    public PartitioningTableItem findByPartitioningTableAndUnit(PartitioningTable keyTable, Unit unit){
        return uniqueMatch("findByPartitioningTableAndUnit", "partitioningTable", keyTable, "unit", unit);
    }


}
