package org.lifecompanion.util.pdf;

import javafx.scene.paint.Color;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.File;

public class DocumentConfiguration {
    private final Color backgroundColor;
    private final PDRectangle pageSize;
    private final String profileName, configurationName, documentNameTranslationId;

    public DocumentConfiguration(Color backgroundColor, PDRectangle pageSize, String profileName, String configurationName, String documentNameTranslationId) {
        this.backgroundColor = backgroundColor;
        this.pageSize = pageSize;
        this.profileName = profileName;
        this.configurationName = configurationName;
        this.documentNameTranslationId = documentNameTranslationId;
    }

    public PDRectangle getPageSize() {
        return pageSize;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public String getProfileName() {
        return profileName;
    }

    public String getConfigurationName() {
        return configurationName;
    }

    public String getDocumentNameTranslationId() {
        return documentNameTranslationId;
    }
}
