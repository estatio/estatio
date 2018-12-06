package org.estatio.module.capex.dom.invoice.approval;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationChecker;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.project.ProjectRoleTypeEnum;
import org.estatio.module.capex.dom.state.AdvancePolicy;
import org.estatio.module.capex.dom.state.NextTransitionSearchStrategy;
import org.estatio.module.capex.dom.state.StateTransitionEvent;
import org.estatio.module.capex.dom.state.StateTransitionRepository;
import org.estatio.module.capex.dom.state.StateTransitionServiceSupportAbstract;
import org.estatio.module.capex.dom.state.StateTransitionType;
import org.estatio.module.capex.dom.state.TaskAssignmentStrategy;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.IPartyRoleType;
import org.estatio.module.party.dom.role.PartyRole;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;

import lombok.Getter;

@Getter
public enum IncomingInvoiceApprovalStateTransitionType
        implements StateTransitionType<
                                        IncomingInvoice,
                IncomingInvoiceApprovalStateTransition,
                IncomingInvoiceApprovalStateTransitionType,
                IncomingInvoiceApprovalState> {

    // a "pseudo" transition type; won't ever see this persisted as a state transition
    INSTANTIATE(
            (IncomingInvoiceApprovalState)null,
            IncomingInvoiceApprovalState.NEW,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL),
    INSTANTIATE_AS_AUTO_PAYABLE(
            (IncomingInvoiceApprovalState)null,
            IncomingInvoiceApprovalState.AUTO_PAYABLE,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL){
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice, final ServiceRegistry2 serviceRegistry2) {
            /** just to double check; this logic is also present in {@link IncomingInvoiceRepository#create} */
            final boolean isPaid = incomingInvoice.getPaidDate() != null;
            final boolean hasPaymentMethodOtherThanBankTransfer = incomingInvoice.getPaymentMethod() != null && incomingInvoice.getPaymentMethod() != PaymentMethod.BANK_TRANSFER;
            if (isItalian(incomingInvoice) && (isPaid || hasPaymentMethodOtherThanBankTransfer)) return true;
            return false;
        }
    },
    REJECT(
            Lists.newArrayList(
                    IncomingInvoiceApprovalState.COMPLETED,
                    IncomingInvoiceApprovalState.APPROVED,
                    IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
                    IncomingInvoiceApprovalState.PENDING_BANK_ACCOUNT_CHECK,
                    IncomingInvoiceApprovalState.PAYABLE,
                    IncomingInvoiceApprovalState.APPROVED_BY_CORPORATE_MANAGER,
                    IncomingInvoiceApprovalState.PENDING_CODA_BOOKS_CHECK,
                    IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER,
                    IncomingInvoiceApprovalState.PENDING_ADVISE,
                    IncomingInvoiceApprovalState.ADVISE_POSITIVE
            ),
            IncomingInvoiceApprovalState.NEW,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL){
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice, final ServiceRegistry2 serviceRegistry2) {
            // exclude italian invoices that are in a state of payable
            if (incomingInvoice.getApprovalState() == IncomingInvoiceApprovalState.PAYABLE && isItalian(incomingInvoice)) return false;
            return true;
        }
    },
    COMPLETE(
            IncomingInvoiceApprovalState.NEW,
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
                    if (incomingInvoice.getProperty()!=null && hasPropertyInvoiceManager(incomingInvoice.getProperty())) return FixedAssetRoleTypeEnum.PROPERTY_INV_MANAGER;
                    return PartyRoleTypeEnum.INCOMING_INVOICE_MANAGER;
                }

                final boolean hasProperty = incomingInvoice.getProperty() != null;
                if (hasProperty) {
                    return PartyRoleTypeEnum.INCOMING_INVOICE_MANAGER;
                }
                // guard since EST-1508 type can be not set
                if (incomingInvoice.getType()==null) return null;
                switch (incomingInvoice.getType()) {
                case CAPEX:
                case PROPERTY_EXPENSES:
                case SERVICE_CHARGES:
                    // this case should not be hit, because the upstream document categorisation process
                    // should have also set a property in this case, so the previous check would have been satisfied
                    // just adding this case in the switch stmt "for completeness"
                    return PartyRoleTypeEnum.INCOMING_INVOICE_MANAGER;
                case LOCAL_EXPENSES:
                    return PartyRoleTypeEnum.OFFICE_ADMINISTRATOR;
                case CORPORATE_EXPENSES:
                    return PartyRoleTypeEnum.CORPORATE_ADMINISTRATOR;
                }
                // REVIEW: for other types, we haven't yet established a business process, so no task will be created
                return null;
            };
        }
        @Override
        public boolean isMatch(
                final IncomingInvoice domainObject, final ServiceRegistry2 serviceRegistry2) {
            // counterpart of isMatch under MARK_PAYABLE_WHEN_NOT_BANK_PAYMENT_FOR_ITA
            if (isItalian(domainObject) && domainObject.getPaymentMethod()!=null && domainObject.getPaymentMethod()!=PaymentMethod.BANK_TRANSFER) return false;
            return getTaskAssignmentStrategy().getAssignTo(domainObject, serviceRegistry2) != null;
        }

        @Override
        public String reasonGuardNotSatisified(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            return incomingInvoice.reasonIncomplete();
        }

    },
    APPROVE(
            Lists.newArrayList(
                    IncomingInvoiceApprovalState.COMPLETED,
                    IncomingInvoiceApprovalState.ADVISE_POSITIVE
            ),
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
                    return PartyRoleTypeEnum.PREFERRED_MANAGER;
                }
                if (isItalian(incomingInvoice) && incomingInvoice.getProperty()==null) return PartyRoleTypeEnum.CORPORATE_MANAGER;
                // guard since EST-1508 type can be not set
                if (incomingInvoice.getType()==null) return null;

                switch (incomingInvoice.getType()) {
                case CAPEX:
                    if (isItalian(incomingInvoice)) return FixedAssetRoleTypeEnum.ASSET_MANAGER;
                    return ProjectRoleTypeEnum.PROJECT_MANAGER;
                case PROPERTY_EXPENSES:
                case SERVICE_CHARGES:
                case ITA_MANAGEMENT_COSTS:
                case ITA_RECOVERABLE:
                    return FixedAssetRoleTypeEnum.ASSET_MANAGER;
                }
                return null;
            };
        }
        @Override
        public boolean isMatch(
                final IncomingInvoice domainObject, final ServiceRegistry2 serviceRegistry2) {
            if (isItalian(domainObject) && domainObject.getType()!=null && domainObject.getProperty()!=null) {
                // case where center manager italy has to approve first (APPROVE_AS_CENTER_MANAGER)
                // the isItalian part above may be superfluous, because type ITA_RECOVERABLE should imply this
                if (domainObject.getType() == IncomingInvoiceType.ITA_RECOVERABLE && hasCenterManager(domainObject.getProperty())) return false;
            }
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
            if (isItalian(incomingInvoice)) return false;
            // guard since EST-1508 type can be not set
            if (incomingInvoice.getType()==null) return false;
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
            if (isItalian(incomingInvoice)) return false;
            // guard since EST-1508 type can be not set
            if (incomingInvoice.getType()==null) return false;
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
                    IncomingInvoiceApprovalState.ADVISE_POSITIVE
            ),
            IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.to(FixedAssetRoleTypeEnum.CENTER_MANAGER),
            AdvancePolicy.AUTOMATIC) {

        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {

            // applies to italian invoices only
            if (!isItalian(incomingInvoice)) return false;

            if (incomingInvoice.getType()==null || incomingInvoice.getProperty()==null) return false;
            return incomingInvoice.getType() == IncomingInvoiceType.ITA_RECOVERABLE && hasCenterManager(incomingInvoice.getProperty());
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
            if (!isItalian(incomingInvoice)) return false; // superfluous but just to be explicit
            // applies to invoices under threshold only
            if (hasNetAmountAboveThreshold(incomingInvoice)) {
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
    APPROVE_AS_COUNTRY_DIRECTOR(
            Lists.newArrayList(
                    IncomingInvoiceApprovalState.APPROVED,
                    IncomingInvoiceApprovalState.APPROVED_BY_CENTER_MANAGER
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
                    return PartyRoleTypeEnum.PREFERRED_DIRECTOR;
                }

                // guard since EST-1508 type can be not set
                if (incomingInvoice.getType()==null) return null;
                // for an recoverable (ita) invoice of a property that has a center manager, take the invoice approval director (of that property)
                if (incomingInvoice.getProperty()!=null && incomingInvoice.getType() == IncomingInvoiceType.ITA_RECOVERABLE && hasCenterManager(incomingInvoice.getProperty())) return FixedAssetRoleTypeEnum.INV_APPROVAL_DIRECTOR;
                return PartyRoleTypeEnum.COUNTRY_DIRECTOR;
            };
        }
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            if (isItalian(incomingInvoice) && !hasNetAmountAboveThreshold(incomingInvoice)) {
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
            if (!isItalian(incomingInvoice)) return false;
            if (hasNetAmountAboveThreshold(incomingInvoice)) return false;
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
            if (!isItalian(incomingInvoice)) return false;
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
            AdvancePolicy.AUTOMATIC){
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
    ){
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
        @Override public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            if (isItalian(incomingInvoice)) return false;
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
            if (isItalian(incomingInvoice)) return false;
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
            if (isItalian(incomingInvoice)) return false;
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
            if (isItalian(incomingInvoice)) return false;
            return incomingInvoice.getPaymentMethod() == PaymentMethod.CREDIT_CARD || incomingInvoice.getPaymentMethod() == PaymentMethod.REFUND_BY_SUPPLIER;
        }
    },
    PAID_IN_CODA(
            Lists.newArrayList(
                    IncomingInvoiceApprovalState.PAYABLE,
                    IncomingInvoiceApprovalState.AUTO_PAYABLE
            ),
            IncomingInvoiceApprovalState.PAID,
            NextTransitionSearchStrategy.none(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.AUTOMATIC
    ){
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
    ADVISE(
            IncomingInvoiceApprovalState.COMPLETED,
            IncomingInvoiceApprovalState.PENDING_ADVISE,
            NextTransitionSearchStrategy.firstMatchingExcluding(REJECT),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL
    ){
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
    ){
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
    ){
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            return isItalian(incomingInvoice);
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

    static boolean isPaidInCoda(final IncomingInvoice incomingInvoice){
        return false; // TODO: change when coda dochead / line is updated with pay status and/or date
    }

    static boolean isItalian(final IncomingInvoice incomingInvoice) {
        return incomingInvoice.getAtPath() !=null && incomingInvoice.getAtPath().startsWith("/ITA");
    }

    static boolean hasNetAmountAboveThreshold(final IncomingInvoice incomingInvoice) {
        return incomingInvoice.getNetAmount()!=null && incomingInvoice.getNetAmount().compareTo(threshold) > 0;
    }

    static BigDecimal threshold = new BigDecimal("100000.00");

    static boolean hasPropertyInvoiceManager(final Property property) {
        return ! Lists.newArrayList(property.getRoles()).stream()
                .filter(x->x.getType()==FixedAssetRoleTypeEnum.PROPERTY_INV_MANAGER)
                .collect(Collectors.toList()).isEmpty();
    }

    static boolean hasCenterManager(final Property property) {
        return ! Lists.newArrayList(property.getRoles()).stream()
                .filter(x->x.getType()==FixedAssetRoleTypeEnum.CENTER_MANAGER)
                .collect(Collectors.toList()).isEmpty();
    }

    public static boolean hasPreferredManagerAndDirector(Party buyer){
        for (PartyRole role : buyer.getRoles()){
            if (role.getRoleType().getKey().equals(IncomingInvoiceRoleTypeEnum.ECP_MGT_COMPANY.getKey())) return true;
        }
        return false;
    }

}

