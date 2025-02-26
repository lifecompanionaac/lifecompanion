package org.lifecompanion.util.pdf;

import javafx.scene.paint.Color;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.File;

public class DocumentConfiguration {
    private final Color backgroundColor;
    private final PDRectangle pageSize;
    private boolean enableFooter;
    private boolean enableHeader;
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

    public void setEnableFooter(boolean enableFooter) {
        this.enableFooter = enableFooter;
    }

    public void setEnableHeader(boolean enableHeader) {
        this.enableHeader = enableHeader;
    }

    public boolean isEnableFooter() {
        return enableFooter;
    }

    public boolean isEnableHeader() {
        return enableHeader;
    }
}
