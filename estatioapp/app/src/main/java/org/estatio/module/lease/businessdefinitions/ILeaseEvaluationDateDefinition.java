package org.estatio.module.lease.businessdefinitions;

import org.joda.time.LocalDate;

import org.estatio.module.lease.dom.Lease;

public interface ILeaseEvaluationDateDefinition {

    LocalDate leaseEvaluationDateFor(final Lease lease, final LocalDate date);

    ILeaseEvaluationDateDefinition NOOP = new ILeaseEvaluationDateDefinition() {
        @Override
        public LocalDate leaseEvaluationDateFor(final Lease lease, final LocalDate date) {
            return null;
        }
    };

}
