package org.estatio.module.capex.dom.invoice.approval;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationChecker;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.project.ProjectRoleTypeEnum;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.IPartyRoleType;
import org.estatio.module.party.dom.role.PartyRole;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;
import org.estatio.module.task.dom.state.AdvancePolicy;
import org.estatio.module.task.dom.state.NextTransitionSearchStrategy;
import org.estatio.module.task.dom.state.StateTransitionEvent;
import org.estatio.module.task.dom.state.StateTransitionRepository;
import org.estatio.module.task.dom.state.StateTransitionServiceSupportAbstract;
import org.estatio.module.task.dom.state.StateTransitionType;
import org.estatio.module.task.dom.state.TaskAssignmentStrategy;

import lombok.Getter;
import static org.estatio.module.capex.dom.util.CountryUtil.isItalian;

@Getter
public enum IncomingInvoiceApprovalStateTransitionType
        implements StateTransitionType<
        IncomingInvoice,
        IncomingInvoiceApprovalStateTransition,
        IncomingInvoiceApprovalStateTransitionType,
        IncomingInvoiceApprovalState> {

    // a "pseudo" transition type; won't ever see this persisted as a state transition
    INSTANTIATE(
            (IncomingInvoiceApprovalState) null,
            IncomingInvoiceApprovalState.NEW,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL),
    REJECT(
            Lists.newArrayList(
                    IncomingInvoiceApprovalState.COMPLETED,
                    IncomingInvoiceApprovalState.MONITORED,
                    IncomingInvoiceApprovalState.APPROVED,
                    IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
                    IncomingInvoiceApprovalState.PENDING_BANK_ACCOUNT_CHECK,
                    IncomingInvoiceApprovalState.PAYABLE,
                    IncomingInvoiceApprovalState.APPROVED_BY_CORPORATE_MANAGER,
                    IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK,
                    IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER,
                    IncomingInvoiceApprovalState.PENDING_ADVISE,
                    IncomingInvoiceApprovalState.PAYABLE_BYPASSING_APPROVAL
            ),
            IncomingInvoiceApprovalState.NEW,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL) {
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice, final ServiceRegistry2 serviceRegistry2) {
            // exclude italian invoices that are in a state of payable
            if (incomingInvoice.getApprovalState() == IncomingInvoiceApprovalState.PAYABLE && isItalian(incomingInvoice))
                return false;
            return true;
        }
    },
    INSTANTIATE_BYPASSING_APPROVAL(
            (IncomingInvoiceApprovalState) null,
            IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.AUTOMATIC) {
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice, final ServiceRegistry2 serviceRegistry2) {
            /** just to double check; this logic is also present in {@link IncomingInvoiceRepository#create} */
            final boolean isPaid = incomingInvoice.getPaidDate() != null;
            final boolean approvalNeededForPaymentMethodOrNoPaymentMethod = incomingInvoice.getPaymentMethod() == null || !incomingInvoice.getPaymentMethod().requiresNoApprovalInItaly();
            if (isItalian(incomingInvoice) && isPaid && approvalNeededForPaymentMethodOrNoPaymentMethod)
                return true;
            return false;
        }
    },
    INSTANTIATE_TO_PAYABLE((IncomingInvoiceApprovalState) null,
            IncomingInvoiceApprovalState.PAYABLE_BYPASSING_APPROVAL,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.AUTOMATIC) {
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice, final ServiceRegistry2 serviceRegistry2) {
            final boolean noApprovalNeededForPaymentMethod = incomingInvoice.getPaymentMethod() != null && incomingInvoice.getPaymentMethod().requiresNoApprovalInItaly();
            if (isItalian(incomingInvoice) && noApprovalNeededForPaymentMethod)
                return true;
            return false;
        }

    },
    AUTO_TRANSITION_TO_PENDING_CODA_BOOKS(
            IncomingInvoiceApprovalState.NEW,
            IncomingInvoiceApprovalState.PAYABLE_BYPASSING_APPROVAL,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.AUTOMATIC
    ) {
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice, final ServiceRegistry2 serviceRegistry2) {
            final boolean noApprovalNeededForPaymentMethod = incomingInvoice.getPaymentMethod() != null && incomingInvoice.getPaymentMethod().requiresNoApprovalInItaly();
            if (isItalian(incomingInvoice) && noApprovalNeededForPaymentMethod)
                return true;
            return false;
        }
    },
    COMPLETE(
            Lists.newArrayList(
                    IncomingInvoiceApprovalState.NEW,
                    IncomingInvoiceApprovalState.SUSPENDED
            ),
            IncomingInvoiceApprovalState.COMPLETED,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            null, // task assignment strategy overridden below
            AdvancePolicy.MANUAL) {
        @Override
        public TaskAssignmentStrategy getTaskAssignmentStrategy() {
            return (TaskAssignmentStrategy<
                    IncomingInvoice,
                    IncomingInvoiceApprovalStateTransition,
                    IncomingInvoiceApprovalStateTransitionType,
                    IncomingInvoiceApprovalState>) (incomingInvoice, serviceRegistry2) -> {

                if (isItalian(incomingInvoice)) {
                    if (
                            incomingInvoice.getProperty() != null
                            &&
                            hasPropertyInvoiceManager(incomingInvoice.getProperty())
                            &&
                            (
                                (
                                        incomingInvoice.getType() == IncomingInvoiceType.ITA_RECOVERABLE &&  IncomingInvoiceApprovalConfigurationUtil.hasRecoverableCompletedByPropertyInvoiceManager(incomingInvoice)
                                ) ||
                                        IncomingInvoiceApprovalConfigurationUtil.hasAllTypesCompletedByPropertyInvoiceManager(incomingInvoice)
                            )
                    ) {
                        return Collections.singletonList(FixedAssetRoleTypeEnum.PROPERTY_INV_MANAGER);
                    }
                    return Collections.singletonList(PartyRoleTypeEnum.INCOMING_INVOICE_MANAGER);
                }

                final boolean hasProperty = incomingInvoice.getProperty() != null;
                if (hasProperty) {
                    return Collections.singletonList(PartyRoleTypeEnum.INCOMING_INVOICE_MANAGER);
                }
                // guard since EST-1508 type can be not set
                if (incomingInvoice.getType() == null)
                    return null;
                switch (incomingInvoice.getType()) {
                    case CAPEX:
                    case PROPERTY_EXPENSES:
                    case SERVICE_CHARGES:
                        // this case should not be hit, because the upstream document categorisation process
                        // should have also set a property in this case, so the previous check would have been satisfied
                        // just adding this case in the switch stmt "for completeness"
                        return Collections.singletonList(PartyRoleTypeEnum.INCOMING_INVOICE_MANAGER);
                    case LOCAL_EXPENSES:
                        return Collections.singletonList(PartyRoleTypeEnum.OFFICE_ADMINISTRATOR);
                    case CORPORATE_EXPENSES:
                        return Collections.singletonList(PartyRoleTypeEnum.CORPORATE_ADMINISTRATOR);
                }
                // REVIEW: for other types, we haven't yet established a business process, so no task will be created
                return null;
            };
        }

        @Override
        public boolean isMatch(
                final IncomingInvoice domainObject, final ServiceRegistry2 serviceRegistry2) {
            // counterpart of isMatch under MARK_PAYABLE_WHEN_NOT_BANK_PAYMENT_FOR_ITA
            if (isItalian(domainObject) && domainObject.getPaymentMethod() != null && domainObject.getPaymentMethod() != PaymentMethod.BANK_TRANSFER)
                return false;
            return getTaskAssignmentStrategy().getAssignTo(domainObject, serviceRegistry2) != null;
        }

        @Override
        public String reasonGuardNotSatisified(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            final boolean missingChamberOfCommerceCode = !isItalian(incomingInvoice) && incomingInvoice.getSeller() instanceof Organisation && ((Organisation) incomingInvoice.getSeller()).getChamberOfCommerceCode() == null;

            return missingChamberOfCommerceCode ?
                    "Supplier is missing chamber of commerce code" :
                    incomingInvoice.reasonIncomplete();
        }

    },
    MONITOR(IncomingInvoiceApprovalState.COMPLETED,
            IncomingInvoiceApprovalState.PRE_MONITORED,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            null, // task assignment strategy overridden below
            AdvancePolicy.MANUAL
            ){
        @Override
        public boolean isMatch(
                final IncomingInvoice domainObject, final ServiceRegistry2 serviceRegistry2) {
            return isInvoiceWithMonitoring(domainObject);
        }

        @Override
        public TaskAssignmentStrategy getTaskAssignmentStrategy() {
            return (TaskAssignmentStrategy<
                    IncomingInvoice,
                    IncomingInvoiceApprovalStateTransition,
                    IncomingInvoiceApprovalStateTransitionType,
                    IncomingInvoiceApprovalState>) (incomingInvoice, serviceRegistry2) -> Arrays.asList(FixedAssetRoleTypeEnum.PROPERTY_MANAGER);
        }
    },
    SUSPEND(
            Lists.newArrayList(
                    IncomingInvoiceApprovalState.NEW,
                    IncomingInvoiceApprovalState.SUSPENDED
            ),
            IncomingInvoiceApprovalState.SUSPENDED,
            NextTransitionSearchStrategy.none(),
            null,
            AdvancePolicy.MANUAL
    ) {
        @Override
        public boolean isMatch(
                final IncomingInvoice domainObject, final ServiceRegistry2 serviceRegistry2) {
            return isItalian(domainObject);
        }
    },
    APPROVE(
            Lists.newArrayList(
                    IncomingInvoiceApprovalState.COMPLETED,
                    IncomingInvoiceApprovalState.MONITORED),
            IncomingInvoiceApprovalState.APPROVED,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            null, // task assignment strategy overridden below
            AdvancePolicy.AUTOMATIC) {
        @Override
        public TaskAssignmentStrategy getTaskAssignmentStrategy() {
            return (TaskAssignmentStrategy<
                    IncomingInvoice,
                    IncomingInvoiceApprovalStateTransition,
                    IncomingInvoiceApprovalStateTransitionType,
                    IncomingInvoiceApprovalState>) (incomingInvoice, serviceRegistry2) -> {
                if (incomingInvoice.getBuyer() != null && hasPreferredManagerAndDirector(incomingInvoice.getBuyer())) {
                    return Collections.singletonList(PartyRoleTypeEnum.PREFERRED_MANAGER);
                }
                if (isItalian(incomingInvoice) && incomingInvoice.getProperty() == null && !IncomingInvoiceApprovalConfigurationUtil.hasAllTypesApprovedByAssetManager(incomingInvoice))
                    return Collections.singletonList(PartyRoleTypeEnum.CORPORATE_MANAGER);
                // guard since EST-1508 type can be not set
                if (incomingInvoice.getType() == null)
                    return null;

                if (isItalian(incomingInvoice))
                    return Arrays.asList(FixedAssetRoleTypeEnum.ASSET_MANAGER, PartyRoleTypeEnum.TECHNICIAN);

                switch (incomingInvoice.getType()) {
                case CAPEX:
                        return Collections.singletonList(ProjectRoleTypeEnum.PROJECT_MANAGER);
                    case PROPERTY_EXPENSES:
                    case SERVICE_CHARGES:
                    case ITA_MANAGEMENT_COSTS:
                    case ITA_RECOVERABLE:
                        return Arrays.asList(FixedAssetRoleTypeEnum.ASSET_MANAGER);
                }
                return null;
            };
        }

        @Override
        public boolean isMatch(
                final IncomingInvoice domainObject, final ServiceRegistry2 serviceRegistry2) {
            if (isItalian(domainObject) && domainObject.getType() != null && domainObject.getProperty() != null) {
                // case where center manager italy has to approve first (APPROVE_AS_CENTER_MANAGER)
                if (IncomingInvoiceApprovalConfigurationUtil.isInvoiceForExternalCenterManager(domainObject)) return false;
            }
            if (isInvoiceWithMonitoring(domainObject) && domainObject.getApprovalState()!=IncomingInvoiceApprovalState.MONITORED) return false;

            return getTaskAssignmentStrategy().getAssignTo(domainObject, serviceRegistry2) != null;
        }

        @Override
        public String reasonGuardNotSatisified(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            return incomingInvoice.reasonIncomplete();
        }

        @Override
        public boolean isAutoGuardSatisfied(
                final IncomingInvoice domainObject, final ServiceRegistry2 serviceRegistry2) {
            return domainObject.isApprovedFully();
        }
    },
    // ECP-1208: this auto transition is a kind of "hack"
    // for APPROVAL transition, when 'peeking' and using isMatch the state of an invoice having monitoring could still be COMPLETED
    // when not using this auto transition
    AUTO_TRANSITION_TO_MONITORED(
            IncomingInvoiceApprovalState.PRE_MONITORED,
            IncomingInvoiceApprovalState.MONITORED,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            null,
            AdvancePolicy.AUTOMATIC) {
        // ECP-1208: we take a "FRANCE only" taskassignment strategy copy from Approve here
        @Override
        public TaskAssignmentStrategy getTaskAssignmentStrategy() {
            return (TaskAssignmentStrategy<
                    IncomingInvoice,
                    IncomingInvoiceApprovalStateTransition,
                    IncomingInvoiceApprovalStateTransitionType,
                    IncomingInvoiceApprovalState>) (incomingInvoice, serviceRegistry2) -> {
                if (incomingInvoice.getType() == null)
                    return null;

                switch (incomingInvoice.getType()) {
                case CAPEX:
                    return Collections.singletonList(ProjectRoleTypeEnum.PROJECT_MANAGER);
                case PROPERTY_EXPENSES:
                case SERVICE_CHARGES:
                    return Arrays.asList(FixedAssetRoleTypeEnum.ASSET_MANAGER);
                }
                return null;
            };
        }

        @Override
        public boolean isMatch(
                final IncomingInvoice domainObject, final ServiceRegistry2 serviceRegistry2) {
            return true;
        }

        @Override
        public boolean isAutoGuardSatisfied(
                final IncomingInvoice domainObject, final ServiceRegistry2 serviceRegistry2) {
            return true;
        }
    },
    APPROVE_LOCAL_AS_COUNTRY_DIRECTOR(
            IncomingInvoiceApprovalState.COMPLETED,
            IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.to(PartyRoleTypeEnum.COUNTRY_DIRECTOR),
            AdvancePolicy.AUTOMATIC) {
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            if (isItalian(incomingInvoice))
                return false;
            // guard since EST-1508 type can be not set
            if (incomingInvoice.getType() == null)
                return false;
            return incomingInvoice.getType() == IncomingInvoiceType.LOCAL_EXPENSES;
        }

        @Override
        public boolean isAutoGuardSatisfied(
                final IncomingInvoice domainObject, final ServiceRegistry2 serviceRegistry2) {
            return domainObject.isApprovedFully();
        }
    },
    APPROVE_AS_CORPORATE_MANAGER(
            IncomingInvoiceApprovalState.COMPLETED,
            IncomingInvoiceApprovalState.APPROVED_BY_CORPORATE_MANAGER,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.to(PartyRoleTypeEnum.CORPORATE_MANAGER),
            AdvancePolicy.AUTOMATIC) {
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            if (isItalian(incomingInvoice))
                return false;
            // guard since EST-1508 type can be not set
            if (incomingInvoice.getType() == null)
                return false;
            return incomingInvoice.getType() == IncomingInvoiceType.CORPORATE_EXPENSES;
        }

        @Override
        public boolean isAutoGuardSatisfied(
                final IncomingInvoice domainObject, final ServiceRegistry2 serviceRegistry2) {
            return domainObject.isApprovedFully();
        }

    },
    APPROVE_AS_CENTER_MANAGER(
            Lists.newArrayList(
                    IncomingInvoiceApprovalState.COMPLETED,
                    IncomingInvoiceApprovalState.MONITORED),
            IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.to(FixedAssetRoleTypeEnum.CENTER_MANAGER),
            AdvancePolicy.AUTOMATIC) {
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {

            // applies to italian invoices only
            if (!isItalian(incomingInvoice))
                return false;

            if (incomingInvoice.getType() == null || incomingInvoice.getProperty() == null)
                return false;
            if (isInvoiceWithMonitoring(incomingInvoice)) return false;
            return IncomingInvoiceApprovalConfigurationUtil.isInvoiceForExternalCenterManager(incomingInvoice);
        }

        @Override
        public String reasonGuardNotSatisified(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            return incomingInvoice.reasonIncomplete();
        }

        @Override
        public boolean isAutoGuardSatisfied(
                final IncomingInvoice domainObject, final ServiceRegistry2 serviceRegistry2) {
            return domainObject.isApprovedFully();
        }
    },
    APPROVE_WHEN_APPROVED_BY_CENTER_MANAGER(
            IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER,
            IncomingInvoiceApprovalState.APPROVED,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.to(FixedAssetRoleTypeEnum.ASSET_MANAGER),
            AdvancePolicy.AUTOMATIC) {
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            if (!isItalian(incomingInvoice))
                return false; // superfluous but just to be explicit
            //ECP-1298 NO LONGER
            // applies to invoices under threshold only
//            if (hasGrossAmountAboveThreshold(incomingInvoice)) {
//                return false;
//            }
            return true;
        }

        @Override
        public boolean isAutoGuardSatisfied(
                final IncomingInvoice domainObject, final ServiceRegistry2 serviceRegistry2) {
            return domainObject.isApprovedFully();
        }
    },
    APPROVE_AS_MARKETING_MANAGER(
            IncomingInvoiceApprovalState.APPROVED,
            IncomingInvoiceApprovalState.APPROVED_BY_MARKETING_MANAGER,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.to(PartyRoleTypeEnum.MARKETING_MANAGER),
            AdvancePolicy.AUTOMATIC){

        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            if (isItalian(incomingInvoice))
                return false; // superfluous but just to be explicit

            if (incomingInvoice.getType()!=null){

                switch (incomingInvoice.getType()){
                case PROPERTY_EXPENSES:
                case SERVICE_CHARGES:
                    if (IncomingInvoiceApprovalConfigurationUtil.hasItemWithChargeMarketingNR(incomingInvoice)) return true;
                    break;
                }

            }
            return false;
        }

        @Override
        public boolean isAutoGuardSatisfied(
                final IncomingInvoice domainObject, final ServiceRegistry2 serviceRegistry2) {
            return domainObject.isApprovedFully();
        }

    },
    APPROVE_AS_COUNTRY_DIRECTOR(
            Lists.newArrayList(
                    IncomingInvoiceApprovalState.APPROVED,
                    IncomingInvoiceApprovalState.APPROVED_BY_MARKETING_MANAGER
                    //ECP-1298
//                    ,
//                    IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER
            ),
            IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.to(PartyRoleTypeEnum.COUNTRY_DIRECTOR),
            AdvancePolicy.AUTOMATIC) {
        @Override
        public TaskAssignmentStrategy getTaskAssignmentStrategy() {
            return (TaskAssignmentStrategy<
                    IncomingInvoice,
                    IncomingInvoiceApprovalStateTransition,
                    IncomingInvoiceApprovalStateTransitionType,
                    IncomingInvoiceApprovalState>) (incomingInvoice, serviceRegistry2) -> {

                if (incomingInvoice.getBuyer() != null && hasPreferredManagerAndDirector(incomingInvoice.getBuyer())) {
                    return Collections.singletonList(PartyRoleTypeEnum.PREFERRED_DIRECTOR);
                }

                // guard since EST-1508 type can be not set
                if (incomingInvoice.getType() == null)
                    return null;
                //ECP-1298: no longer needed. With new center managers for COL, IGIGL we need Asset Manager And Center Manager to sign both when > 100.000
                // for an recoverable (ita) invoice of a property that has a center manager, take the invoice approval director (of that property)
//                if (incomingInvoice.getProperty() != null && incomingInvoice.getType() == IncomingInvoiceType.ITA_RECOVERABLE && hasCenterManager(incomingInvoice.getProperty()))
//                    return Collections.singletonList(FixedAssetRoleTypeEnum.INV_APPROVAL_DIRECTOR);
                return Collections.singletonList(PartyRoleTypeEnum.COUNTRY_DIRECTOR);
            };
        }

        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            if (isItalian(incomingInvoice) && !hasGrossAmountAboveThreshold(incomingInvoice)) {
                return false;
            }
            return true;
        }

        @Override
        public boolean isAutoGuardSatisfied(
                final IncomingInvoice domainObject, final ServiceRegistry2 serviceRegistry2) {
            return domainObject.isApprovedFully();
        }
    },
    CHECK_IN_CODA_BOOKS_WHEN_APPROVED(
            IncomingInvoiceApprovalState.APPROVED,
            IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.AUTOMATIC) {
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            // applies to italian invoices only with net amount under threshold
            if (!isItalian(incomingInvoice))
                return false;
            if (hasGrossAmountAboveThreshold(incomingInvoice))
                return false;
            return true;
        }
    },
    CHECK_IN_CODA_BOOKS(
            IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
            IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.AUTOMATIC) {
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            // applies to italian invoices only
            if (!isItalian(incomingInvoice))
                return false;
            return true;
        }
    },
    CHECK_BANK_ACCOUNT(
            Lists.newArrayList(
                    IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
                    IncomingInvoiceApprovalState.APPROVED_BY_CORPORATE_MANAGER
            ),
            IncomingInvoiceApprovalState.PENDING_BANK_ACCOUNT_CHECK,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.AUTOMATIC) {
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            // applies to NON-italian invoices only
            return !isItalian(incomingInvoice);
        }
    },
    CONFIRM_BANK_ACCOUNT_VERIFIED(
            IncomingInvoiceApprovalState.PENDING_BANK_ACCOUNT_CHECK,
            IncomingInvoiceApprovalState.PAYABLE,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.AUTOMATIC) {
        @Override
        public boolean isGuardSatisfied(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {

            final BankAccountVerificationChecker bankAccountVerificationChecker =
                    serviceRegistry2.lookupService(BankAccountVerificationChecker.class);

            return bankAccountVerificationChecker.isBankAccountVerifiedFor(incomingInvoice) ||
                    Arrays.asList(PaymentMethod.DIRECT_DEBIT, PaymentMethod.MANUAL_PROCESS, PaymentMethod.CREDIT_CARD, PaymentMethod.REFUND_BY_SUPPLIER).contains(incomingInvoice.getPaymentMethod());
        }
    },
    CONFIRM_IN_CODA_BOOKS(
            IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK,
            IncomingInvoiceApprovalState.PAYABLE,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.AUTOMATIC
    ) {
        @Override
        public boolean isGuardSatisfied(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            return incomingInvoice.isPostedToCodaBooks();
        }
    },
    PAY_BY_IBP(
            IncomingInvoiceApprovalState.PAYABLE,
            IncomingInvoiceApprovalState.PAID,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL) {
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            if (isItalian(incomingInvoice))
                return false;
            return incomingInvoice.getPaymentMethod() == PaymentMethod.BANK_TRANSFER;
        }
    },
    PAY_BY_IBP_MANUAL(
            IncomingInvoiceApprovalState.PAYABLE,
            IncomingInvoiceApprovalState.PAID,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.AUTOMATIC) {
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            if (isItalian(incomingInvoice))
                return false;
            return incomingInvoice.getPaymentMethod() == PaymentMethod.MANUAL_PROCESS;
        }
    },
    PAY_BY_DD(
            IncomingInvoiceApprovalState.PAYABLE,
            IncomingInvoiceApprovalState.PAID,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL) {
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            if (isItalian(incomingInvoice))
                return false;
            return incomingInvoice.getPaymentMethod() == PaymentMethod.DIRECT_DEBIT;
        }
    },
    CHECK_PAYMENT(
            IncomingInvoiceApprovalState.PAYABLE,
            IncomingInvoiceApprovalState.PAID,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.to(PartyRoleTypeEnum.TREASURER),
            AdvancePolicy.MANUAL) {
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            if (isItalian(incomingInvoice))
                return false;
            return incomingInvoice.getPaymentMethod() == PaymentMethod.CREDIT_CARD || incomingInvoice.getPaymentMethod() == PaymentMethod.REFUND_BY_SUPPLIER;
        }
    },
    ADVISE(
            IncomingInvoiceApprovalState.COMPLETED,
            IncomingInvoiceApprovalState.PENDING_ADVISE,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL
    ) {
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            return isItalian(incomingInvoice);
        }
    },
    ADVISE_TO_APPROVE(
            IncomingInvoiceApprovalState.PENDING_ADVISE,
            IncomingInvoiceApprovalState.ADVISE_POSITIVE,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.to(PartyRoleTypeEnum.ADVISOR),
            AdvancePolicy.MANUAL
    ) {
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            return isItalian(incomingInvoice);
        }
    },
    // the advice positive state is only there to capture the signature (when gathering 'signatures' the toState of transitions is inspected on isApproval())
    // so once captured it can transition right away to completed and allow for even more advise ...
    AUTO_TRANSITION_WHEN_ADVISED_TO_APPROVE(
            IncomingInvoiceApprovalState.ADVISE_POSITIVE,
            IncomingInvoiceApprovalState.COMPLETED,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.AUTOMATIC
    ) {
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            return isItalian(incomingInvoice);
        }
    },
    NO_ADVISE(
            IncomingInvoiceApprovalState.PENDING_ADVISE,
            IncomingInvoiceApprovalState.COMPLETED,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL
    ) {
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            return isItalian(incomingInvoice);
        }
    },
    PAID_IN_CODA(
            Lists.newArrayList(
                    IncomingInvoiceApprovalState.NEW,
                    IncomingInvoiceApprovalState.SUSPENDED,
                    IncomingInvoiceApprovalState.COMPLETED,
                    IncomingInvoiceApprovalState.PENDING_ADVISE,
                    IncomingInvoiceApprovalState.ADVISE_POSITIVE,
                    IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER,
                    IncomingInvoiceApprovalState.APPROVED,
                    IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
                    IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK,
                    IncomingInvoiceApprovalState.PAYABLE,
                    IncomingInvoiceApprovalState.PAYABLE_BYPASSING_APPROVAL
            ),
            IncomingInvoiceApprovalState.PAID,
            NextTransitionSearchStrategy.none(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.AUTOMATIC
    ) {
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            return isItalian(incomingInvoice);
        }

        @Override
        public boolean isGuardSatisfied(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            return isPaidInCoda(incomingInvoice);
        }
    },
    DISCARD(
            IncomingInvoiceApprovalState.NEW,
            IncomingInvoiceApprovalState.DISCARDED,
            NextTransitionSearchStrategy.none(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL),
    ;

    private final List<IncomingInvoiceApprovalState> fromStates;
    private final IncomingInvoiceApprovalState toState;
    private final NextTransitionSearchStrategy nextTransitionSearchStrategy;
    private final TaskAssignmentStrategy taskAssignmentStrategy;
    private final AdvancePolicy advancePolicy;

    IncomingInvoiceApprovalStateTransitionType(
            final List<IncomingInvoiceApprovalState> fromState,
            final IncomingInvoiceApprovalState toState,
            final NextTransitionSearchStrategy nextTransitionSearchStrategy,
            final TaskAssignmentStrategy taskAssignmentStrategy,
            final AdvancePolicy advancePolicy) {
        this.fromStates = fromState;
        this.toState = toState;
        this.nextTransitionSearchStrategy = nextTransitionSearchStrategy;
        this.taskAssignmentStrategy = taskAssignmentStrategy;
        this.advancePolicy = advancePolicy;
    }

    IncomingInvoiceApprovalStateTransitionType(
            final IncomingInvoiceApprovalState fromState,
            final IncomingInvoiceApprovalState toState,
            final NextTransitionSearchStrategy nextTransitionSearchStrategy,
            final TaskAssignmentStrategy taskAssignmentStrategy,
            final AdvancePolicy advancePolicy) {
        this(fromState != null
                        ? Collections.singletonList(fromState)
                        : null,
                toState, nextTransitionSearchStrategy, taskAssignmentStrategy,
                advancePolicy);
    }

    public static class TransitionEvent
            extends StateTransitionEvent<
            IncomingInvoice,
            IncomingInvoiceApprovalStateTransition,
            IncomingInvoiceApprovalStateTransitionType,
            IncomingInvoiceApprovalState> {
        public TransitionEvent(
                final IncomingInvoice domainObject,
                final IncomingInvoiceApprovalStateTransition stateTransitionIfAny,
                final IncomingInvoiceApprovalStateTransitionType transitionType) {
            super(domainObject, stateTransitionIfAny, transitionType);
        }
    }

    @Override
    public NextTransitionSearchStrategy getNextTransitionSearchStrategy() {
        return nextTransitionSearchStrategy;
    }

    @Override
    public TransitionEvent newStateTransitionEvent(
            final IncomingInvoice domainObject,
            final IncomingInvoiceApprovalStateTransition transitionIfAny) {
        return new TransitionEvent(domainObject, transitionIfAny, this);
    }

    @Override
    public AdvancePolicy advancePolicyFor(
            final IncomingInvoice domainObject,
            final ServiceRegistry2 serviceRegistry2) {
        return advancePolicy;
    }

    @Override
    public IncomingInvoiceApprovalStateTransition createTransition(
            final IncomingInvoice domainObject,
            final IncomingInvoiceApprovalState fromState,
            final IPartyRoleType assignToIfAny,
            final Person personToAssignToIfAny,
            final String taskDescriptionIfAny,
            final ServiceRegistry2 serviceRegistry2) {

        final IncomingInvoiceApprovalStateTransition.Repository repository =
                serviceRegistry2.lookupService(IncomingInvoiceApprovalStateTransition.Repository.class);

        final String taskDescription = Util.taskDescriptionUsing(taskDescriptionIfAny, this);

        return repository.create(domainObject, this, fromState, assignToIfAny, personToAssignToIfAny, taskDescription);
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends StateTransitionServiceSupportAbstract<
            IncomingInvoice, IncomingInvoiceApprovalStateTransition, IncomingInvoiceApprovalStateTransitionType, IncomingInvoiceApprovalState> {

        public SupportService() {
            super(IncomingInvoiceApprovalStateTransitionType.class, IncomingInvoiceApprovalStateTransition.class
            );
        }

        @Override
        protected StateTransitionRepository<
                IncomingInvoice,
                IncomingInvoiceApprovalStateTransition,
                IncomingInvoiceApprovalStateTransitionType,
                IncomingInvoiceApprovalState
                > getRepository() {
            return repository;
        }

        @Inject
        IncomingInvoiceApprovalStateTransition.Repository repository;

    }

    static boolean isPaidInCoda(final IncomingInvoice incomingInvoice) {
        return incomingInvoice.getPaidDate() != null;
    }

    static boolean hasGrossAmountAboveThreshold(final IncomingInvoice incomingInvoice) {
        if (IncomingInvoiceApprovalConfigurationUtil.hasHighSingleSignatureThreshold(incomingInvoice)){
            return incomingInvoice.getGrossAmount() != null && incomingInvoice.getGrossAmount().compareTo(IncomingInvoiceApprovalConfigurationUtil.singleSignatureThresholdHigh) > 0;
        }
        return incomingInvoice.getGrossAmount() != null && incomingInvoice.getGrossAmount().compareTo(IncomingInvoiceApprovalConfigurationUtil.singleSignatureThresholdNormal) > 0;
    }

    static boolean hasPropertyInvoiceManager(final Property property) {
        return Lists.newArrayList(property.getRoles()).stream()
                .filter(x -> x.getType() == FixedAssetRoleTypeEnum.PROPERTY_INV_MANAGER)
                .anyMatch(FixedAssetRole::isCurrent);
    }

    public static boolean hasPreferredManagerAndDirector(Party buyer) {
        for (PartyRole role : buyer.getRoles()) {
            if (role.getRoleType().getKey().equals(IncomingInvoiceRoleTypeEnum.ECP_MGT_COMPANY.getKey()))
                return true;
        }
        return false;
    }

    public static boolean isInvoiceWithMonitoring(final IncomingInvoice incomingInvoice){
        return IncomingInvoiceApprovalConfigurationUtil.hasMonitoring(incomingInvoice);
    }

}

