/*
 *  Copyright 2014 Dan Haywood
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
package org.estatio.fixture.security.tenancy;

public class AllEstatioPartitions extends AbstractEstatioPartitionFixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        executionContext.executeChild(this, new GlobalEstatioPartition());

        executionContext.executeChild(this, new EstatioPartitionForIta());
        executionContext.executeChild(this, new EstatioPartitionForItaGra());
        executionContext.executeChild(this, new EstatioPartitionForFra());
        executionContext.executeChild(this, new EstatioPartitionForFraViv());
        executionContext.executeChild(this, new EstatioPartitionForSwe());
        executionContext.executeChild(this, new EstatioPartitionForSweHan());
        executionContext.executeChild(this, new EstatioPartitionForNld());
        executionContext.executeChild(this, new EstatioPartitionForNldKal());
        executionContext.executeChild(this, new EstatioPartitionForGbr());
        executionContext.executeChild(this, new EstatioPartitionForGbrOxf());
    }

}
