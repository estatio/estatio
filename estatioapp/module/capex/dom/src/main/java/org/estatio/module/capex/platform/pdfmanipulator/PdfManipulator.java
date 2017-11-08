package org.estatio.module.capex.platform.pdfmanipulator;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.util.Matrix;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.pdfbox.dom.service.PdfBoxService;

@DomainService(nature = NatureOfService.DOMAIN)
public class PdfManipulator {

    private static final float X_MARGIN_LEFT = 50F;
    private static final float X_MARGIN_RIGHT = 290F;
    private static final float Y_BOX_BASE = 100F;
    private static final float BOX_WIDTH = 240F;
    private static final int BOX_LINE_WIDTH = 1;
    private static final int BOX_Y_PADDING = 4;
    private static final Color BOX_FILL = new Color(240, 240, 240); // lightGrey

    private static final float TEXT_LINE_HEIGHT = 14F;
    private static final int TEXT_X_PADDING = 4;
    static final Color TEXT_COLOR = Color.BLUE;
    private static final PDFont TEXT_FONT = PDType1Font.COURIER;
    private static final int TEXT_FONT_SIZE = 10;

    static final Color HYPERLINK_COLOR = Color.MAGENTA;

    private static final float SCALE = 0.85f;
    private static final float SCALE_X = SCALE;
    private static final float SCALE_Y = SCALE;

    @Programmatic
    public byte[] stamp(
            final byte[] docBytes,
            final Stamp stamp) throws IOException {
        return extractAndStamp(docBytes, ExtractSpec.ALL_PAGES, stamp);
    }

    @Programmatic
    public byte[] extract(
            final byte[] docBytes,
            final ExtractSpec extractSpec) throws IOException {
        return extractAndStamp(docBytes, extractSpec, null);
    }

    @Programmatic
    public byte[] extractAndStamp(
            final byte[] docBytes,
            final ExtractSpec extractSpec,
            final Stamp stamp) throws IOException {

        List<byte[]> extractedPageDocBytes = Lists.newArrayList();

        final PDDocument pdDoc = PDDocument.load(docBytes);

        try {

            final Splitter splitter = new Splitter();
            final List<PDDocument> splitDocs = splitter.split(pdDoc);

            final int sizeOfDoc = splitDocs.size();
            final Integer[] pageNums = extractSpec.pageNumbersFor(sizeOfDoc);

            for (Integer pageNum : pageNums) {
                final PDDocument docOfExtractedPage = splitDocs.get(pageNum);

                if(stamp != null) {

                    final List<Line> leftLines = stamp.getLeftLines();
                    final List<Line> rightLines = stamp.getRightLines();

                    leftLines.add(new Line(String.format("Page: %d/%d", (pageNum+1), sizeOfDoc), TEXT_COLOR, null));
                    stamp.appendHyperlinkIfAnyTo(leftLines);

                    extractedPageDocBytes.add(stamp(docOfExtractedPage, leftLines, rightLines));

                } else {
                    extractedPageDocBytes.add(asBytes(docOfExtractedPage));
                }

            }

            for (PDDocument splitDoc : splitDocs) {
                splitDoc.close();
            }

        } finally {
            pdDoc.close();
        }

        final byte[] mergedBytes = pdfBoxService.merge(extractedPageDocBytes.toArray(new byte[][] {}));

        return mergedBytes;
    }

    private static byte[] asBytes(final PDDocument doc) throws IOException {

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        doc.save(baos);

        return baos.toByteArray();
    }

    private byte[] stamp(
            final PDDocument onePageDoc,
            final List<Line> leftLines,
            final List<Line> rightLines) throws IOException {

        PDPage pdPage = onePageDoc.getPage(0);

        final float pageHeight = pdPage.getMediaBox().getHeight();

        final PDPageContentStream prependStream =
                new PDPageContentStream(onePageDoc, pdPage, PDPageContentStream.AppendMode.PREPEND, false);
        try {
            prependStream.transform(Matrix.getScaleInstance(SCALE_X, SCALE_Y));
            prependStream.transform(Matrix.getTranslateInstance(0f, pageHeight * (1 - SCALE_Y)));
        } finally {
            prependStream.close();
        }

        final PDPageContentStream appendStream =
                new PDPageContentStream(onePageDoc, pdPage, PDPageContentStream.AppendMode.APPEND, true, true);

        try {

            final int leftLineSize = leftLines.size();
            final int rightLineSize = rightLines.size();

            final float height = Math.max(leftLineSize, rightLineSize) * TEXT_LINE_HEIGHT + BOX_Y_PADDING;
            final float y = Y_BOX_BASE - height;

            float x = X_MARGIN_LEFT;
            float yLine = addLines(x, y, height, leftLines, appendStream);

            String hyperlink = leftLines.get(leftLineSize - 1).hyperlink;
            addHyperlink(x, yLine + TEXT_LINE_HEIGHT - 6, hyperlink, pdPage);

            x = X_MARGIN_RIGHT;
            addLines(x, y, height, rightLines, appendStream);

        } finally {
            appendStream.close();
        }

        return asBytes(onePageDoc);
    }


    private void addBox(
            final float x, final float y, final float height,
            final PDPageContentStream cs) throws IOException {
        cs.setLineWidth(BOX_LINE_WIDTH);
        cs.setStrokingColor(Color.DARK_GRAY);
        cs.addRect(
                x - (float) BOX_LINE_WIDTH,
                y - (float) BOX_LINE_WIDTH,
                BOX_WIDTH + 2* (float) BOX_LINE_WIDTH,
                height + 2* (float) BOX_LINE_WIDTH);
        cs.stroke();
        cs.setNonStrokingColor(BOX_FILL);
        cs.addRect(x, y, BOX_WIDTH, height);
        cs.fill();
    }

    private float addLines(
            final float x, final float y,
            final float height,
            final List<Line> lines,
            final PDPageContentStream cs) throws IOException {
        cs.setFont(TEXT_FONT, TEXT_FONT_SIZE);
        float yLine = y + height - TEXT_LINE_HEIGHT;
        for (Line line : lines) {

            cs.setNonStrokingColor(line.color);
            cs.beginText();
            cs.newLineAtOffset(x + TEXT_X_PADDING, yLine);
            cs.showText(line.text);
            cs.endText();

            yLine -= TEXT_LINE_HEIGHT;
        }

        return yLine;
    }

    private void addHyperlink(
            final float x, final float y,
            final String hyperlink,
            final PDPage pdPage) throws IOException {

        PDAnnotationLink txtLink = new PDAnnotationLink();

        PDRectangle position = new PDRectangle();
        PDBorderStyleDictionary underline = new PDBorderStyleDictionary();
        underline.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
        txtLink.setBorderStyle(underline);

        position.setLowerLeftX(x);
        position.setLowerLeftY(y);
        position.setUpperRightX(X_MARGIN_LEFT + BOX_WIDTH);
        position.setUpperRightY(y + TEXT_LINE_HEIGHT);
        txtLink.setRectangle(position);

        PDActionURI action = new PDActionURI();
        action.setURI(hyperlink);
        txtLink.setAction(action);
        pdPage.getAnnotations().add(txtLink);
    }

    @Inject
    PdfBoxService pdfBoxService;
}
