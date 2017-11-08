package org.estatio.module.capex.platform.pdfmanipulator;

import java.awt.Color;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.assertj.core.util.Lists;

import lombok.Data;

@Data
public class Stamp {
    final List<String> leftLineTexts;
    final List<String> rightLineTexts;
    final String hyperlink;

    private static final Color TEXT_COLOR = PdfManipulator.TEXT_COLOR;
    private static final Color HYPERLINK_COLOR = PdfManipulator.HYPERLINK_COLOR;

    private static List<Line> asLines(final List<String> origLines) {
        return origLines.stream().map(x -> new Line(x, TEXT_COLOR, null)).collect(Collectors.toCollection(
                (Supplier<List<Line>>) Lists::newArrayList));
    }

    public List<Line> getLeftLines() {
        return asLines(getLeftLineTexts());
    }

    public List<Line> getLeftLinesWithHyperlinkIfAny() {
        final List<Line> leftLines = getLeftLines();
        appendHyperlinkIfAnyTo(leftLines);
        return leftLines;
    }

    public List<Line> getRightLines() {
        return asLines(getRightLineTexts());
    }

    public void appendHyperlinkIfAnyTo(final List<Line> leftLines) {
        if(getHyperlink() != null) {
            leftLines.add(new Line("Open in Estatio", HYPERLINK_COLOR, getHyperlink()));
        }

    }
}
