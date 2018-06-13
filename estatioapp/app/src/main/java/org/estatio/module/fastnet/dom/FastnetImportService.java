package org.estatio.module.fastnet.dom;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.message.MessageService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeGroup;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermForFixed;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;

@DomainService(nature = NatureOfService.DOMAIN)
public class FastnetImportService {

    public static final LocalDate EPOCH_DATE_FASTNET_IMPORT = new LocalDate(2018, 01, 01);

    private static final Logger logger = LoggerFactory.getLogger(FastnetImportService.class);

    public FastnetImportManager importFastnetData(final LocalDate exportDate) {

        FastnetImportManager fastnetImportManager = new FastnetImportManager();
        fastnetImportManager.setExportDate(exportDate);
        long start = System.currentTimeMillis();

        List<FastNetRentRollOnLeaseDataLine> potentiallyPartialMatchingDataLines = rentRollOnLeaseDataLineRepo.nonMatchingRentRollLinesForExportDate(exportDate);
        long potentials = System.currentTimeMillis();

        List<FastNetRentRollOnLeaseDataLine> partialMatchingDataLines = getPartiallyMatchingDataLines(potentiallyPartialMatchingDataLines);
        fastnetImportManager.setPartialMatchingDataLines(partialMatchingDataLines);
        long partials = System.currentTimeMillis();

        List<FastNetRentRollOnLeaseDataLine> nonMatchingDataLines = getNonMatchingDataLines(potentiallyPartialMatchingDataLines, partialMatchingDataLines);
        fastnetImportManager.setNonMatchingDataLines(nonMatchingDataLines);
        long nonmatching = System.currentTimeMillis();

        List<FastNetRentRollOnLeaseDataLine> linesWithoutKontraktNumber = rentRollOnLeaseDataLineRepo.findByExternalReferenceAndExportDate(null, exportDate);
        fastnetImportManager.setLinesWithoutKontraktNr(linesWithoutKontraktNumber);
        long nokontraktnr = System.currentTimeMillis();

        List<FastNetRentRollOnLeaseDataLine> matchingRentRollDataLines = rentRollOnLeaseDataLineRepo.matchingRentRollLinesForExportDate(exportDate);
        long matchinglines = System.currentTimeMillis();

        // a bit ugly, but might save some time because we iterate once over matching rent roll data lines
        determineDoubleExternalReferencesAndRentRollLinesWithoutChargingDetails(fastnetImportManager, matchingRentRollDataLines, exportDate);
        long nochargingdetailsanddoubles = System.currentTimeMillis();

        // end of rent roll datalines analysis //////////////////////

        List<FastNetChargingOnLeaseDataLine> chargingDataLines = chargingOnLeaseDataLineRepo.findNonDiscardedAndNonAppliedByExportDate(exportDate);
        List<FastNetChargingOnLeaseDataLine> chargingDataLinesForActiveLeasesNotInImport = chargingOnLeaseDataLineRepo.findByExportDate(exportDate);
        long chargingDatalines = System.currentTimeMillis();

        List<Lease> activeLeasesNotInImport = getLeasesNotInImport(chargingDataLinesForActiveLeasesNotInImport);
        List<LeaseViewModel> toViewmodels = activeLeasesNotInImport.stream().filter(x -> !x.getReference().startsWith("Z-")).map(x -> new LeaseViewModel(x.getReference(), x.getExternalReference())).collect(Collectors.toList());
        fastnetImportManager.getActiveLeasesNotInImport().addAll(toViewmodels);
        long activeleasesnotinimport = System.currentTimeMillis();

        List<FastNetChargingOnLeaseDataLine> chargingDataLinesChargeNotFound = chargingDataLines
                .stream()
                .filter(x -> x.getChargeReference() == null)
                .collect(Collectors.toList());
        chargingDataLines.removeAll(chargingDataLinesChargeNotFound);
        fastnetImportManager.setChargeNotFound(chargingDataLinesChargeNotFound);

        List<FastNetChargingOnLeaseDataLine> chargingDataLinesToDiscard = chargingDataLines
                .stream()
                .filter(x -> x.getChargeGroupReference().equals("SE_DISCARD"))
                .collect(Collectors.toList());
        chargingDataLines.removeAll(chargingDataLinesToDiscard);
        fastnetImportManager.setDiscardedLines(chargingDataLinesToDiscard);

        // cleaning
        // again ugly, but might save some time because we iterate once over charging data lines
        List<String> matchingKeys = matchingRentRollDataLines.stream().map(l -> l.getKeyToLeaseExternalReference()).collect(Collectors.toList());
        List<FastNetChargingOnLeaseDataLine> notInMatchingRentRollDataLines = new ArrayList<>();
        List<FastNetChargingOnLeaseDataLine> noUpdateNeeded = new ArrayList<>();
        Map<String, List<FastNetChargingOnLeaseDataLine>> chargeRefLineMap = new HashMap<>();
        chargingDataLines.forEach(cdl -> {
            // check against matchingRentRollDataLines
            if (!matchingKeys.contains(cdl.getKeyToLeaseExternalReference())) {
                notInMatchingRentRollDataLines.add(cdl);
            }

            if (!chargeRefLineMap.containsKey(getChargeRefMapKey(cdl))) {
                chargeRefLineMap.put(getChargeRefMapKey(cdl), new ArrayList<>(Arrays.asList(cdl)));
            } else {
                chargeRefLineMap.get(getChargeRefMapKey(cdl)).add(cdl);
            }
            // determine no update needed
            if (hasNoEffectAtAll(cdl)) {
                noUpdateNeeded.add(cdl);
            }

        });

        chargeRefLineMap.entrySet().removeIf(pair -> pair.getValue().size() == 1);
        List<FastNetChargingOnLeaseDataLine> duplicateChargeReferences = chargeRefLineMap.values().stream().flatMap(List::stream).collect(Collectors.toList());

        // not all of these duplicates need to be filtered
        // there are two categories: non overlapping (these should be imported in the right order
        // overlapping (like counter bookings): these can be aggregated when at least the end dates (tomdat's) are the same.

        chargingDataLines.removeAll(duplicateChargeReferences);
        chargingDataLines.removeAll(notInMatchingRentRollDataLines);
        chargingDataLines.removeAll(noUpdateNeeded);

        noUpdateNeeded.removeAll(duplicateChargeReferences);

        duplicateChargeReferences.removeAll(notInMatchingRentRollDataLines);

        fastnetImportManager.setDuplicateChargeReferences(duplicateChargeReferences);
        fastnetImportManager.setNoUpdateNeeded(noUpdateNeeded);

        List<FastNetChargingOnLeaseDataLine> chargingDataLinesForItemUpdate = chargingDataLines.stream().filter(cl -> cl.getLeaseTermStartDate() != null).collect(Collectors.toList());
        List<FastNetChargingOnLeaseDataLine> chargingDataLinesForItemCreation = chargingDataLines.stream().filter(cl -> cl.getLeaseTermStartDate() == null).collect(Collectors.toList());
        fastnetImportManager.setLinesForItemUpdate(chargingDataLinesForItemUpdate);
        fastnetImportManager.setLinesForItemCreation(chargingDataLinesForItemCreation);
        long chargenotfoundandnotinmatchingdatalines = System.currentTimeMillis();

        logger.info(String.format("Potentials: %d", (potentials - start) / 1000));
        logger.info(String.format("Partials: %d", (partials - potentials) / 1000));
        logger.info(String.format("Nonmatching lines: %d", (nonmatching - partials) / 1000));
        logger.info(String.format("Nokontraktnr lines: %d", (nokontraktnr - nonmatching) / 1000));
        logger.info(String.format("Matching lines: %d", (matchinglines - nokontraktnr) / 1000));
        logger.info(String.format("No charging details and doubles: %d", (nochargingdetailsanddoubles - matchinglines) / 1000));
        logger.info(String.format("Charging data lines: %d", (chargingDatalines - nochargingdetailsanddoubles) / 1000));
        logger.info(String.format("Charge not found: %d", (chargenotfoundandnotinmatchingdatalines - chargingDatalines) / 1000));
        logger.info(String.format("Active leases not in import: %d", (activeleasesnotinimport - chargenotfoundandnotinmatchingdatalines) / 1000));

        return fastnetImportManager;
    }

    List<Lease> getLeasesNotInImport(final List<FastNetChargingOnLeaseDataLine> chargingDataLines) {
        final List<String> externalRefsInChargingDataLines = chargingDataLines.stream().map(FastNetChargingOnLeaseDataLine::getKeyToLeaseExternalReference).collect(Collectors.toList());
        List<Lease> activeLeasesNotInImport = activeSwedishLeases();
        activeLeasesNotInImport.removeIf(lease -> externalRefsInChargingDataLines.contains(lease.getExternalReference()));
        return activeLeasesNotInImport;
    }

    private String getChargeRefMapKey(final FastNetChargingOnLeaseDataLine cdl) {
        return cdl.getKeyToLeaseExternalReference() + "_" + cdl.getKeyToChargeReference();
    }

    boolean hasNoEffectAtAll(final FastNetChargingOnLeaseDataLine cdl) {

        if (cdl.getLeaseTermStartDate() != null) {

            if (sameDates(cdl) && sameValues(cdl) && sameInvoicingFrequency(cdl)) {
                return true;
            }

        }
        return false;
    }

    boolean sameDates(final FastNetChargingOnLeaseDataLine cdl) {
        if (cdl.getLeaseTermEndDate() != null && cdl.getTomDat() != null) {
            return cdl.getLeaseTermStartDate().equals(stringToDate(cdl.getFromDat())) && cdl.getLeaseTermEndDate().equals(stringToDate(cdl.getTomDat()));
        }
        if (cdl.getLeaseTermEndDate() == null && cdl.getTomDat() == null) {
            return cdl.getLeaseTermStartDate().equals(stringToDate(cdl.getFromDat()));
        }
        // special case for turnover rent fixed that does not allow open enddates
        Charge ch = chargeRepository.findByReference(cdl.getKeyToChargeReference());
        if (ch != null && ch.getGroup().getReference().equals("SE_TURNOVER_RENT_FIXED")) {
            if (cdl.getLeaseTermEndDate() != null && cdl.getTomDat() == null) {
                return true;
            }
        }
        return false;
    }

    boolean sameValues(final FastNetChargingOnLeaseDataLine cdl) {
        final BigDecimal scaledArsBel = cdl.getArsBel().setScale(2, RoundingMode.HALF_UP);
        return scaledArsBel.equals(cdl.getValue()) || scaledArsBel.equals(cdl.getSettledValue()) || scaledArsBel.equals(cdl.getBudgetedValue());
    }

    boolean sameInvoicingFrequency(final FastNetChargingOnLeaseDataLine cdl) {
        final InvoicingFrequency frequency = mapToFrequency(cdl.getDebPer());
        if (frequency != null) {
            return frequency.name().equals(cdl.getInvoicingFrequency());
        }
        return false;
    }

    void determineDoubleExternalReferencesAndRentRollLinesWithoutChargingDetails(final FastnetImportManager fastnetImportManager, final List<FastNetRentRollOnLeaseDataLine> matchingRentRollDataLines, final LocalDate exportDate) {
        List<FastNetRentRollOnLeaseDataLine> dataLinesWithoutChargingDetails = new ArrayList<>();
        List<FastNetRentRollOnLeaseDataLine> doubleExternalReferences;
        Map<String, List<FastNetRentRollOnLeaseDataLine>> externalRefLinePairs = new HashMap<>();
        matchingRentRollDataLines.forEach(line -> {
            ChargingLine cline = chargingLineRepository.findFirstByKeyToLeaseExternalReferenceAndExportDate(line.getKeyToLeaseExternalReference(), exportDate);
            if (cline == null) {
                dataLinesWithoutChargingDetails.add(line);
            }

            if (!externalRefLinePairs.containsKey(line.getKeyToLeaseExternalReference())) {
                externalRefLinePairs.put(line.getKeyToLeaseExternalReference(), new ArrayList<>(Arrays.asList(line)));
            } else {
                externalRefLinePairs.get(line.getKeyToLeaseExternalReference()).add(line);
            }
        });
        matchingRentRollDataLines.removeAll(dataLinesWithoutChargingDetails);
        externalRefLinePairs.entrySet().removeIf(pair -> pair.getValue().size() == 1);
        doubleExternalReferences = externalRefLinePairs.values().stream().flatMap(List::stream).collect(Collectors.toList());
        matchingRentRollDataLines.removeAll(doubleExternalReferences);
        ////////
        fastnetImportManager.setNoChargingDetails(dataLinesWithoutChargingDetails);
        fastnetImportManager.setDoubleExternalReferences(doubleExternalReferences);
        ///////
    }

    List<FastNetRentRollOnLeaseDataLine> getNonMatchingDataLines(final List<FastNetRentRollOnLeaseDataLine> potentiallyPartialMatchingDataLines, final List<FastNetRentRollOnLeaseDataLine> partialMatchingDataLines) {
        List<FastNetRentRollOnLeaseDataLine> nonMatchingDataLines = new ArrayList<>();
        List<String> partialKeys = partialMatchingDataLines.stream().map(FastNetRentRollOnLeaseDataLine::getKeyToLeaseExternalReference).collect(Collectors.toList());
        potentiallyPartialMatchingDataLines.forEach(nml -> {
            if (!partialKeys.contains(nml.getKeyToLeaseExternalReference())) {
                nonMatchingDataLines.add(nml);
            }
        });
        return nonMatchingDataLines;
    }

    List<FastNetRentRollOnLeaseDataLine> getPartiallyMatchingDataLines(List<FastNetRentRollOnLeaseDataLine> potentiallyPartialMatchingDataLines) {
        List<FastNetRentRollOnLeaseDataLine> partialMatchingDataLines = new ArrayList<>();
        potentiallyPartialMatchingDataLines.forEach(line -> {
            final List<Lease> partialMatchedLeases = leaseRepository.matchLeaseByExternalReference(mapPartialExternalReference(line.getKeyToLeaseExternalReference()));
            if (!partialMatchedLeases.isEmpty()) {
                partialMatchedLeases.forEach(lease -> {
                    FastNetRentRollOnLeaseDataLine lineForPartial = new FastNetRentRollOnLeaseDataLine(
                            line.getImportStatus(),
                            line.getApplied(),
                            line.getKeyToLeaseExternalReference(),
                            line.getExportDate(),
                            line.getLeaseReference(),
                            line.getExternalReference(),
                            line.getLeaseStartDate(),
                            line.getLeaseEndDate(),
                            line.getKontraktNr(),
                            line.getKundNr(),
                            line.getArshyra(),
                            line.getHyresgast(),
                            line.getKontraktFrom(),
                            line.getKontraktTom(),
                            line.isFutureRentRollLine()
                    );
                    lineForPartial.setLeaseReference(lease.getReference());
                    lineForPartial.setExternalReference(lease.getExternalReference());
                    lineForPartial.setLeaseStartDate(lease.getStartDate());
                    lineForPartial.setLeaseEndDate(lease.getEndDate());
                    partialMatchingDataLines.add(lineForPartial);
                });
            }
        });
        return partialMatchingDataLines;
    }

    List<Lease> findLeaseByExternalReferenceReturnActiveFirst(final String kontraktNrToUse) {
        final List<Lease> candidates = leaseRepository.matchLeaseByExternalReference(kontraktNrToUse);
        // TODO: use max epoch date - startDate
        final List<Lease> activeCandidates = candidates.stream()
                .filter(x -> x.getTenancyEndDate() == null
                        || x.getTenancyEndDate().isAfter(EPOCH_DATE_FASTNET_IMPORT))
                .collect(Collectors.toList());
        return activeCandidates.isEmpty() ? candidates : activeCandidates;
    }

    private InvoicingFrequency mapToFrequency(final String debper) {

        switch (debper) {

            case "Månad":
                return InvoicingFrequency.MONTHLY_IN_ADVANCE;

            case "Kvartal":
                return InvoicingFrequency.QUARTERLY_IN_ADVANCE;

            case "Halvår":
                return InvoicingFrequency.SEMI_YEARLY_IN_ADVANCE;

            case "Helår":
                return InvoicingFrequency.YEARLY_IN_ADVANCE;

        }
        return null;
    }

    LocalDate stringToDate(final String dateString) {
        return dateString != null ? LocalDate.parse(dateString) : null;
    }

    void updateOrCreateItem(final FastNetChargingOnLeaseDataLine cdl) {
        final ChargingLine cLine = chargingLineRepository.findUnique(cdl.getKeyToLeaseExternalReference(), cdl.getKeyToChargeReference(), cdl.getFromDat(), cdl.getTomDat(), cdl.getArsBel(), cdl.getExportDate(), null);
        if (cLine!=null) cLine.apply();
    }

    public ImportStatus updateOrCreateItemAndTerm(final ChargingLine cLine) {

        if (cLine.getFromDat() == null) {
            final String message = String.format("Charging line for lease %s with charge %s has no start date (fromdat).", cLine.getKeyToLeaseExternalReference(), cLine.getKeyToChargeReference());
//            messageService.warnUser(message);
            cLine.appendImportLog(message);
            logger.warn(message);
            return null;
        }

        final Lease lease = findLeaseOrReturnNull(cLine);
        if (lease == null)
            return null;

        final Charge charge = findChargeOrReturnNull(cLine);
        if (charge == null)
            return null;

        final ChargeGroup chargeGroup = charge.getGroup();
        if (chargeGroup.getReference().equals("SE_DISCARD"))
            return null;

        final List<ChargingLine> linesWithSameChargeNotYetAggregated = chargingLineRepository.findByKeyToLeaseExternalReferenceAndKeyToChargeReferenceAndExportDate(cLine.getKeyToLeaseExternalReference(), cLine.getKeyToChargeReference(), cLine.getExportDate())
                .stream()
                .filter(cl -> cl.getImportStatus() != ImportStatus.AGGREGATED && !cl.discardedOrAggregatedOrApplied())
                .collect(Collectors.toList());

        if (linesWithSameChargeNotYetAggregated.size() > 1) {
            return handleChargingLinesWithSameCharge(linesWithSameChargeNotYetAggregated, lease, charge);
        }

        LeaseItem itemToUpdate = lease.findFirstItemOfTypeAndCharge(mapToLeaseItemType(charge), charge);

        return itemToUpdate == null ? createItemAndTerm(cLine, lease, charge) : updateItemAndTerm(cLine, charge, itemToUpdate);

    }

    ImportStatus handleChargingLinesWithSameCharge(final List<ChargingLine> linesWithSameCharge, final Lease lease, final Charge charge) {

        // analysis
        // check on from date
        for (ChargingLine cLine : linesWithSameCharge) {
            if (cLine.getFromDat() == null) {
                final String message = String.format("Charging line for lease %s with charge %s has no start date (fromdat) while also multiple lines with this charge found. Please handle manually.", cLine.getKeyToLeaseExternalReference(), cLine.getKeyToChargeReference());
//                messageService.warnUser(message);
                cLine.appendImportLog(message);
                logger.warn(message);
                return null;
            }
        }
        linesWithSameCharge.sort((a, b) -> stringToDate(a.getFromDat()).compareTo(stringToDate(b.getFromDat())));

        final LocalDateInterval previousCenturies = new LocalDateInterval(null, new LocalDate(1999, 12, 31));
        LocalDateInterval previous = previousCenturies;
        boolean overlapFound = false;
        for (ChargingLine cLine : linesWithSameCharge) {
            LocalDateInterval next = new LocalDateInterval(stringToDate(cLine.getFromDat()), stringToDate(cLine.getTomDat()));
            if (previous.overlaps(next)) {
                overlapFound = true;
            } else {
                previous = next;
            }
        }

        if (overlapFound) {
            // try to aggregate
            boolean endDatesAreAllEmpty = true;
            for (ChargingLine line : linesWithSameCharge) {
                if (line.getTomDat() != null) {
                    endDatesAreAllEmpty = false;
                }
            }
            if (endDatesAreAllEmpty) {
                ChargingLine result = aggregateAndUpdateOrCreate(linesWithSameCharge, lease, charge);
                return result.getImportStatus();
            } else {
                List<String> tomDats = linesWithSameCharge.stream().map(ChargingLine::getTomDat).distinct().collect(Collectors.toList());
                if (tomDats.size() == 1) {
                    ChargingLine result = aggregateAndUpdateOrCreate(linesWithSameCharge, lease, charge);
                    return result.getImportStatus();
                }
            }
            final String message = String.format("Multiple lines for lease %s with charge %s found that could not be aggregated. Please handle manually.", linesWithSameCharge.get(0).getKeyToLeaseExternalReference(), linesWithSameCharge.get(0).getKeyToChargeReference());
            // messageService.warnUser(message);
            for (ChargingLine cLine : linesWithSameCharge) {
                cLine.appendImportLog(message);
            }
            logger.warn(message);
            return null;

        } else {
            LeaseItem itemToUpdate = lease.findFirstItemOfTypeAndCharge(mapToLeaseItemType(charge), charge);
            if (itemToUpdate == null) {
                linesWithSameCharge.forEach(line -> {
                    createItemAndTerm(line, lease, charge);
                });
                return ImportStatus.LEASE_ITEM_CREATED;
            } else {
                linesWithSameCharge.forEach(line -> {
                    updateItemAndTerm(line, charge, itemToUpdate);
                });
                return ImportStatus.LEASE_ITEM_UPDATED;
            }
        }

    }

    ChargingLine aggregateAndUpdateOrCreate(final List<ChargingLine> linesWithSameCharge, final Lease lease, final Charge charge) {

        ChargingLine aggregatedLine = new ChargingLine();
        aggregatedLine.setFromDat(getMinFromDatAsString(linesWithSameCharge));
        aggregatedLine.setTomDat(getMaxTomDatAsString(linesWithSameCharge));
        ChargingLine firstLine = linesWithSameCharge.get(0);
        aggregatedLine.setKeyToLeaseExternalReference(firstLine.getKeyToLeaseExternalReference());
        aggregatedLine.setKeyToChargeReference(firstLine.getKeyToChargeReference());
        aggregatedLine.setExportDate(firstLine.getExportDate());
        aggregatedLine.setArsBel(summedArsBel(linesWithSameCharge));
        aggregatedLine.setKlientKod(firstLine.getKlientKod());
        aggregatedLine.setKlientNamn(firstLine.getKlientNamn());
        aggregatedLine.setFastighetsNr(firstLine.getFastighetsNr());
        aggregatedLine.setFastighetsBeteckning(firstLine.getFastighetsBeteckning());
        aggregatedLine.setObjektNr(firstLine.getObjektNr());
        aggregatedLine.setKontraktNr(firstLine.getKontraktNr());
        aggregatedLine.setKundNr(firstLine.getKundNr());
        aggregatedLine.setKod(firstLine.getKod());
        aggregatedLine.setKod2(firstLine.getKod2());
        aggregatedLine.setKontText(firstLine.getKontText());
        aggregatedLine.setKontText2(firstLine.getKontText2());
        aggregatedLine.setPerBel(firstLine.getPerBel());
        aggregatedLine.setDebPer(firstLine.getDebPer());
        aggregatedLine.setImportDate(firstLine.getImportDate());
        aggregatedLine.setEvdInSd(firstLine.getEvdInSd());

        aggregatedLine.setImportStatus(ImportStatus.COMPOUNDED);
        aggregatedLine = chargingLineRepository.persist(aggregatedLine);
        linesWithSameCharge.forEach(line -> line.setImportStatus(ImportStatus.AGGREGATED));

        final ImportStatus compoundedStatus = aggregatedLine.apply();
        if (compoundedStatus != null) {
            final LocalDate appliedDate = aggregatedLine.getApplied();
            linesWithSameCharge.forEach(line -> line.setApplied(appliedDate));
        }

        return aggregatedLine;
    }

    BigDecimal summedArsBel(final List<ChargingLine> linesWithSameCharge) {
        return linesWithSameCharge.stream().map(ChargingLine::getArsBel).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    String getMinFromDatAsString(final List<ChargingLine> chargingLines) {
        final List<LocalDate> localdates = chargingLines.stream().filter(cl -> cl.getFromDat() != null).map(cl -> stringToDate(cl.getFromDat())).collect(Collectors.toList());
        return localdates.isEmpty() ? null : Collections.min(localdates).toString("yyyy-MM-dd");
    }

    String getMaxTomDatAsString(final List<ChargingLine> chargingLines) {
        final List<LocalDate> localdates = chargingLines.stream().filter(cl -> cl.getTomDat() != null).map(cl -> stringToDate(cl.getTomDat())).collect(Collectors.toList());
        return localdates.isEmpty() ? null : Collections.max(localdates).toString("yyyy-MM-dd");
    }

    ImportStatus updateItemAndTerm(final ChargingLine cLine, final Charge charge, LeaseItem itemToUpdate) {

        if (itemToUpdate == null) {
            final String message = String.format("Item with charge %s not found for lease %s.", charge.getReference(), cLine.getKeyToLeaseExternalReference());
//            messageService.warnUser(message);
            cLine.appendImportLog(message);
            logger.warn(message);
            return null;
        }

        final InvoicingFrequency frequency = mapToFrequency(cLine.getDebPer());
        if (frequency != null) {
            itemToUpdate.setInvoicingFrequency(frequency);
        } else {
            final String message = String.format("Value debPer %s could not be mapped to invoicing frequency for charge %s on lease %s.", cLine.getDebPer(), charge.getReference(), cLine.getKeyToLeaseExternalReference());
//            messageService.warnUser(message);
            cLine.appendImportLog(message);
            logger.warn(message);
            return null;
        }

        LeaseTerm termToUpdate = findOrCreateTermToUpdate(itemToUpdate, cLine);
        if (termToUpdate==null){
            return null;
        }

        itemToUpdate.setEndDate(itemToUpdate.getTerms().last().getEndDate());

        if (cLine.getArsBel() == null) {
            cLine.setArsBel(BigDecimal.ZERO);
        }
        updateLeaseTermValue(itemToUpdate.getType(), cLine.getArsBel(), termToUpdate);

        return ImportStatus.LEASE_ITEM_UPDATED;
    }

    LeaseTerm findOrCreateTermToUpdate(final LeaseItem itemToUpdate, final ChargingLine cLine){

        if (itemToUpdate.getTerms().isEmpty()){
            return itemToUpdate.newTerm(stringToDate(cLine.getFromDat()), stringToDate(cLine.getTomDat()));
        }

        return deriveTermToUpdateFromLastTerm(itemToUpdate.getTerms().last(), cLine);
    }

    LeaseTerm deriveTermToUpdateFromLastTerm(LeaseTerm lastTerm, final ChargingLine cLine) {

        LeaseTerm termToUpdate = null;

        final LocalDate cLineStartDate = stringToDate(cLine.getFromDat());
        final LocalDate cLineEndDate = stringToDate(cLine.getTomDat());
        final LocalDateInterval cLineInterval = LocalDateInterval.including(cLineStartDate, cLineEndDate);

        final LocalDateInterval lastTermInterval = lastTerm.getInterval();

        if (!cLineInterval.overlaps(lastTermInterval)) {

            if (cLineStartDate.isBefore(lastTerm.getStartDate())) {
                final String message = String.format("Item with charge %s for lease %s cannot be updated. FromDat %s is before last term start date %s", cLine.getKeyToChargeReference(), cLine.getKeyToLeaseExternalReference(), cLine.getFromDat(), lastTerm.getStartDate().toString("yyyy-MM-dd"));
//                messageService.warnUser(message);
                cLine.appendImportLog(message);
                logger.warn(message);
                return null;
            } else {
                LocalDate ltEnd = lastTerm.getEndDate();
                termToUpdate = lastTerm.getLeaseItem().newTerm(cLineStartDate, cLineEndDate);
                lastTerm.setEndDate(ltEnd); // this line may be superfluous ??
            }

        } else {

            // case: term has no enddate
            if (lastTermInterval.isOpenEnded()){

                if (cLineStartDate.isAfter(lastTerm.getStartDate())){
                    termToUpdate = lastTerm.getLeaseItem().newTerm(cLineStartDate, cLineEndDate);
                    lastTerm.setEndDate(cLineStartDate.minusDays(1)); // this line may be superfluous ??
                } else {
                    termToUpdate = lastTerm;
                    lastTerm.setEndDate(cLineEndDate);
                }

            } else {

                // case: term has enddate while cLine has not
                if (cLineInterval.isOpenEnded()){

                    if (cLineStartDate.isAfter(lastTerm.getStartDate())){
                        termToUpdate = lastTerm.getLeaseItem().newTerm(cLineStartDate, cLineEndDate);
                        lastTerm.setEndDate(cLineStartDate.minusDays(1)); // this line may be superfluous ??
                    } else {
                        termToUpdate = lastTerm;
                        termToUpdate.setEndDate(cLineEndDate);
                    }

                } else {
                    // case: both term and cLine have enddate

                    if (cLineStartDate.isAfter(lastTerm.getStartDate())){
                        LocalDate endDateToUse = cLineEndDate.isBefore(lastTerm.getEndDate()) ? lastTerm.getEndDate() : cLineEndDate;
                        termToUpdate = lastTerm.getLeaseItem().newTerm(cLineStartDate, endDateToUse);
                        lastTerm.setEndDate(cLineStartDate.minusDays(1)); // this line may be superfluous ??
                    } else {
                        termToUpdate = lastTerm;
                        LocalDate endDateToUse = cLineEndDate.isBefore(lastTerm.getEndDate()) ? lastTerm.getEndDate() : cLineEndDate;
                        termToUpdate.setEndDate(endDateToUse);
                    }

                }

            }

        }

        return termToUpdate;
    }


    public void discard(final FastNetChargingOnLeaseDataLine cdl) {
        final ChargingLine cLine = chargingLineRepository.findUnique(cdl.getKeyToLeaseExternalReference(), cdl.getKeyToChargeReference(), cdl.getFromDat(), cdl.getTomDat(), cdl.getArsBel(), cdl.getExportDate(), null);
        if (cLine!=null) cLine.discard();
    }

    public void noUpdate(final FastNetChargingOnLeaseDataLine cdl) {
        final ChargingLine cLine = chargingLineRepository.findUnique(cdl.getKeyToLeaseExternalReference(), cdl.getKeyToChargeReference(), cdl.getFromDat(), cdl.getTomDat(), cdl.getArsBel(), cdl.getExportDate(), null);
        if (cLine!=null) cLine.noUpdate();
    }

    ImportStatus createItemAndTerm(final ChargingLine cLine, final Lease lease, final Charge charge) {

        LeaseItemType leaseItemType = mapToLeaseItemType(charge);
        closeAllItemsOfTypeActiveOnEpochDate(lease, leaseItemType);

        final LeaseItem leaseItem = findOrCreateLeaseItemForTypeAndCharge(lease, leaseItemType, charge, mapToFrequency(cLine.getDebPer()), stringToDate(cLine.getFromDat()));
        leaseItem.setEndDate(stringToDate(cLine.getTomDat()));
        LeaseTerm newTerm = createNewTermAndCloseExistingIfOverlappingAndOpenEnded(leaseItem, cLine.getArsBel(), stringToDate(cLine.getFromDat()), stringToDate(cLine.getTomDat()));

        return newTerm != null ? ImportStatus.LEASE_ITEM_CREATED : null;
    }

    Charge findChargeOrReturnNull(final ChargingLine cLine) {
        final Charge charge = chargeRepository.findByReference(cLine.getKeyToChargeReference());
        if (charge == null) {
            final String message = String.format("Charge with reference %s not found for lease %s.", cLine.getKeyToChargeReference(), cLine.getKeyToLeaseExternalReference());
//            messageService.warnUser(message);
            cLine.appendImportLog(message);
            logger.warn(message);
            return null;
        }
        return charge;
    }

    Lease findLeaseOrReturnNull(final ChargingLine cLine) {
        final List<Lease> canditateLeaseForUpdate = leaseRepository.matchLeaseByExternalReference(cLine.getKeyToLeaseExternalReference());
        if (canditateLeaseForUpdate.isEmpty()) {
            final String message = String.format("Lease with external reference %s not found.", cLine.getKeyToLeaseExternalReference());
//            messageService.warnUser(message);
            cLine.appendImportLog(message);
            logger.warn(message);
            return null;
        }
        if (canditateLeaseForUpdate.size() > 1) {
            final String message = String.format("Multiple leases with external reference %s found.", cLine.getKeyToLeaseExternalReference());
//            messageService.warnUser(message);
            cLine.appendImportLog(message);
            logger.warn(message);
            return null;
        }
        return canditateLeaseForUpdate.get(0);
    }

    Lease closeAllItemsOfTypeActiveOnEpochDate(final Lease lease, final LeaseItemType leaseItemType) {
        throwExceptionIfLeaseItemTypeIsNotYetImplemented(leaseItemType);
        lease.findItemsOfType(leaseItemType).stream().filter(x -> x.getInterval().contains(EPOCH_DATE_FASTNET_IMPORT.minusDays(1))).forEach(x -> x.setEndDate(EPOCH_DATE_FASTNET_IMPORT.minusDays(1)));
        // this is needed now since we map rent items to rent fixed.
        if (leaseItemType.equals(LeaseItemType.RENT_FIXED)){
            lease.findItemsOfType(LeaseItemType.RENT).stream().filter(x -> x.getInterval().contains(EPOCH_DATE_FASTNET_IMPORT.minusDays(1))).forEach(x -> x.setEndDate(EPOCH_DATE_FASTNET_IMPORT.minusDays(1)));
        }
        return lease;
    }

    @Programmatic
    public LeaseItemType mapToLeaseItemType(final Charge charge) {
        if (!charge.getGroup().getReference().equals("SE_DISCARD")){

            if (charge.getGroup().getReference().equals("SE_RENT")) return LeaseItemType.RENT_FIXED; // this charge group is used for base rent
            if (charge.getGroup().getReference().equals("SE_RENT_INDEX")) return LeaseItemType.RENT_FIXED; // this charge group is used for the amount of the indexation

            return LeaseItemType.valueOf(charge.getGroup().getReference().replace("SE_", "")); // by convention
        }
        return null;
    }

    LeaseItem findOrCreateLeaseItemForTypeAndCharge(final Lease lease, final LeaseItemType leaseItemType, final Charge charge, final InvoicingFrequency invoicingFrequency, final LocalDate startDate) {

        throwExceptionIfLeaseItemTypeIsNotYetImplemented(leaseItemType);

        LocalDate startDateToUse = startDate == null || EPOCH_DATE_FASTNET_IMPORT.isAfter(startDate) ? EPOCH_DATE_FASTNET_IMPORT : startDate;

        List<LeaseItem> candidates = lease.findItemsOfType(leaseItemType).stream().filter(x -> x.getCharge().equals(charge)).collect(Collectors.toList());
        if (candidates.size() > 1) {
            throw new RuntimeException("Multiple lease items of type " + leaseItemType + " and charge " + charge.getReference() + " found for lease " + lease.getReference());
        }
        LeaseItem leaseItem;
        if (candidates.size() == 1) {
            leaseItem = candidates.get(0);
        } else {
            leaseItem = lease.newItem(leaseItemType, LeaseAgreementRoleTypeEnum.LANDLORD, charge, invoicingFrequency, PaymentMethod.BANK_TRANSFER, startDateToUse);
        }
        return leaseItem;
    }

    private void throwExceptionIfLeaseItemTypeIsNotYetImplemented(final LeaseItemType leaseItemType) {
        if (!Arrays.asList(LeaseItemType.RENT_FIXED, LeaseItemType.TURNOVER_RENT_FIXED, LeaseItemType.SERVICE_CHARGE, LeaseItemType.PROPERTY_TAX, LeaseItemType.MARKETING, LeaseItemType.RENT_DISCOUNT_FIXED).contains(leaseItemType)) {
            throw new RuntimeException("Type  " + leaseItemType + " not yet supported");
        }
    }

    LeaseTerm createNewTermAndCloseExistingIfOverlappingAndOpenEnded(final LeaseItem leaseItem, final BigDecimal amount, final LocalDate startDate, final LocalDate endDate) {


        closeOverlappingOpenEndedExistingTerms(leaseItem, startDate, endDate);

        LeaseTerm leaseTerm = leaseItem.newTerm(startDate, endDate);
        return updateLeaseTermValue(leaseItem.getType(), amount, leaseTerm);
    }

    LeaseTerm updateLeaseTermValue(final LeaseItemType leaseItemType, final BigDecimal amount, final LeaseTerm leaseTerm) {
        final BigDecimal value = amount.setScale(2, RoundingMode.HALF_UP);
        switch (leaseItemType) {

            case RENT_FIXED:
            LeaseTermForFixed termForRentFixed;
            termForRentFixed = (LeaseTermForFixed) leaseTerm;
            termForRentFixed.setValue(value);

            return termForRentFixed;

            case TURNOVER_RENT_FIXED:
            LeaseTermForFixed termForTurnoverRent;
            termForTurnoverRent = (LeaseTermForFixed) leaseTerm;
            termForTurnoverRent.setValue(value);

            return termForTurnoverRent;

            case SERVICE_CHARGE:
            LeaseTermForServiceCharge termForServiceCharge;
            termForServiceCharge = (LeaseTermForServiceCharge) leaseTerm;
            termForServiceCharge.setBudgetedValue(value);
            return termForServiceCharge;

            case PROPERTY_TAX:
            LeaseTermForServiceCharge termForPropertyTax;
            termForPropertyTax = (LeaseTermForServiceCharge) leaseTerm;
            termForPropertyTax.setBudgetedValue(value);
            return termForPropertyTax;

            case MARKETING:
            LeaseTermForServiceCharge termForMarketing;
            termForMarketing = (LeaseTermForServiceCharge) leaseTerm;
            termForMarketing.setBudgetedValue(value);
            return termForMarketing;

            case RENT_DISCOUNT_FIXED:
            LeaseTermForFixed termForRentDiscount;
            termForRentDiscount = (LeaseTermForFixed) leaseTerm;
            termForRentDiscount.setValue(value);

            return termForRentDiscount;

            default:
                // TODO: add support for other types when the time is right
        }
        return null;
    }

    void closeOverlappingOpenEndedExistingTerms(final LeaseItem leaseItem, final LocalDate startDate, final LocalDate endDate) {
        for (LeaseTerm existingTerm : leaseItem.getTerms()) {
            if (existingTerm.getInterval().overlaps(new LocalDateInterval(startDate, endDate)) && existingTerm.getInterval().isOpenEnded()) {
                if (existingTerm.getStartDate().isBefore(startDate)) { // Extra guard for non realistic case - should only happen in integ test ;-)
                    existingTerm.setEndDate(startDate.minusDays(1));
                }
            }
        }
    }

    String mapKontraktNrToExternalReference(String kontraktnr) {
        return kontraktnr != null ? kontraktnr.trim().substring(2) : null;
    }

    String mapPartialExternalReference(String reference) {
        // 1234-4567-02 <=> 1234-4567 of ook 1234-4567-01
        return reference != null ? reference.trim().substring(0, 9) : null;
    }

    Charge mapToCharge(final String kod, final String kod2) {
        if (kod == null || kod2 == null) {
            return null;
        }
        return chargeRepository.findByReference("SE".concat(kod).concat("-").concat(kod2));
    }

    List<Lease> activeSwedishLeases() {
        List<Lease> result = new ArrayList<>();
        Country sweden = countryRepository.findCountry("SWE");
        List<Property> propertiesForSweden = propertyRepository.allProperties().stream()
                .filter(
                        x -> x.getCountry().equals(sweden)
                                &&
                                (x.getDisposalDate() == null
                                        ||
                                        x.getDisposalDate().isAfter(LocalDate.now()))
                ).collect(Collectors.toList());
        propertiesForSweden.forEach(x -> result.addAll(leaseRepository.findByAssetAndActiveOnDate(x, LocalDate.now())));
        return result;
    }

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    ChargeRepository chargeRepository;

    @Inject PropertyRepository propertyRepository;

    @Inject CountryRepository countryRepository;

    @Inject ChargingLineRepository chargingLineRepository;

    @Inject FastNetRentRollOnLeaseDataLineRepo rentRollOnLeaseDataLineRepo;

    @Inject FastNetChargingOnLeaseDataLineRepo chargingOnLeaseDataLineRepo;

    @Inject MessageService messageService;

}
