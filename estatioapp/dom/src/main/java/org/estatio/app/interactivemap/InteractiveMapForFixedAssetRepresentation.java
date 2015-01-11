package org.estatio.app.interactivemap;

public enum InteractiveMapForFixedAssetRepresentation {
    DEFAULT {
        @Override
        public ColorService getColorService() {
            return new StatusColorService();
        }
    };

    public abstract ColorService getColorService();
}
