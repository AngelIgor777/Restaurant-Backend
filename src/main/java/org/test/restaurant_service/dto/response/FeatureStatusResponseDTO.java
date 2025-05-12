package org.test.restaurant_service.dto.response;

import org.test.restaurant_service.dto.feats.Features;

public class FeatureStatusResponseDTO {
    private Features feature;
    private boolean enabled;

    public FeatureStatusResponseDTO(Features feature, boolean enabled) {
        this.feature = feature;
        this.enabled = enabled;
    }

    public Features getFeature() {
        return feature;
    }

    public void setFeature(Features feature) {
        this.feature = feature;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
