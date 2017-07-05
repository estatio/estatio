package org.estatio.capex.dom.payment;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.util.Matrix;
import org.assertj.core.util.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

@DomainService(nature = NatureOfService.DOMAIN)
public class PdfStamper {


    static class Line {
        private final String text;
        private final Color color;
        private final String hyperlink;
        Line(final String text, final Color color, final String hyperlink) {
            this.text = text;
            this.color = color;
            this.hyperlink = hyperlink;
        }
    }

    private static final float X_MARGIN_LEFT = 50F;
    private static final float X_MARGIN_RIGHT = 290F;
    private static final float Y_BOX_BASE = 100F;
    private static final float BOX_WIDTH = 240F;
    private static final int BOX_LINE_WIDTH = 1;
    private static final int BOX_Y_PADDING = 4;
    private static final Color BOX_FILL = new Color(240, 240, 240); // lightGrey

    private static final float TEXT_LINE_HEIGHT = 14F;
    private static final int TEXT_X_PADDING = 4;
    private static final Color TEXT_COLOR = Color.DARK_GRAY;
    private static final PDFont TEXT_FONT = PDType1Font.COURIER;
    private static final int TEXT_FONT_SIZE = 10;

    private static final Color HYPERLINK_COLOR = Color.BLUE;

    @Programmatic
    public byte[] firstPageOf(
            byte[] docBytes,
            final List<String> leftLineTexts,
            final List<String> rightLineTexts,
            final String hyperlink) throws IOException {

        List<Line> leftLines = asLines(leftLineTexts);
        List<Line> rightLines = asLines(rightLineTexts);

        final PDDocument pdDoc = PDDocument.load(docBytes);
        try {

            final Splitter splitter = new Splitter();
            final List<PDDocument> splitDocs = splitter.split(pdDoc);

            if (!splitDocs.isEmpty()) {

                leftLines.add(new Line(String.format("# orig pgs : %d", splitDocs.size()), TEXT_COLOR, null));
                leftLines.add(new Line("Open in Estatio", HYPERLINK_COLOR, hyperlink));

                PDDocument docOfFirstPage = splitDocs.get(0);
                docBytes = stamp(docOfFirstPage, leftLines, rightLines);

                for (PDDocument splitDoc : splitDocs) {
                    splitDoc.close();
                }
            }

        } finally {
            pdDoc.close();
        }

        return docBytes;
    }
    private byte[] stamp(
            final PDDocument docOfFirstPage,
            final List<Line> leftLines,
            final List<Line> rightLines) throws IOException {

        final byte[] docBytes;
        PDPage pdPage = docOfFirstPage.getPage(0);

        final float pageHeight = pdPage.getMediaBox().getHeight();

        final PDPageContentStream prependStream =
                new PDPageContentStream(docOfFirstPage, pdPage, PDPageContentStream.AppendMode.PREPEND, false);
        try {
            prependStream.transform(Matrix.getScaleInstance(0.9f, 0.9f));
            prependStream.transform(Matrix.getTranslateInstance(0f, pageHeight * 0.1f));
        } finally {
            prependStream.close();
        }

        final PDPageContentStream appendStream =
                new PDPageContentStream(docOfFirstPage, pdPage, PDPageContentStream.AppendMode.APPEND, true, true);

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

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        docOfFirstPage.save(baos);
        docBytes = baos.toByteArray();
        return docBytes;
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

    private List<Line> asLines(final List<String> origLines) {
        return origLines.stream().map(x -> new Line(x, Color.DARK_GRAY, null)).collect(Collectors.toCollection(
                (Supplier<List<Line>>) Lists::newArrayList));
    }

    private PDColor asPdColor(final Color color) {
        float[] components = new float[] {
                color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f };
        return new PDColor(components, PDDeviceRGB.INSTANCE);
    }

}
