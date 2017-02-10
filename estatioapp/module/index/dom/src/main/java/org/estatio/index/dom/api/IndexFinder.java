package org.estatio.index.dom.api;

import org.estatio.index.dom.Index;

public interface IndexFinder {

    Index findByReference(String reference);
}
