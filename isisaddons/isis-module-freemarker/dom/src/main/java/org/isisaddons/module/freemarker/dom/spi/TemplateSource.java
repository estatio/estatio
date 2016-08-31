package org.isisaddons.module.freemarker.dom.spi;

public class TemplateSource {
    private final String chars;
    private final long version;

    public TemplateSource(final String chars, final long version) {
        this.chars = chars;
        this.version = version;
    }

    public String getChars() {
        return chars;
    }

    public long getVersion() {
        return version;
    }
}
