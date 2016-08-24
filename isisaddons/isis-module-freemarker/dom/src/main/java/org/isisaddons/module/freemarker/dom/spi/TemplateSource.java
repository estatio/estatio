package org.isisaddons.module.freemarker.dom.spi;

public class TemplateSource {
    private final String text;
    private final long version;

    public TemplateSource(final String text, final long version) {
        this.text = text;
        this.version = version;
    }

    public String getText() {
        return text;
    }

    public long getVersion() {
        return version;
    }
}
