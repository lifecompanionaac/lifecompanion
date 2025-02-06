package org.lifecompanion.model.impl.io;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class PdfConfig {
    private final PDRectangle pageSize;
    private final boolean enableHeaderFooter;

    public PdfConfig(PDRectangle pageSize, boolean enableHeaderFooter) {
        this.pageSize = pageSize;
        this.enableHeaderFooter = enableHeaderFooter;
    }

    public PDRectangle getPageSize() {
        return pageSize;
    }

    public boolean isEnableHeaderFooter() {
        return enableHeaderFooter;
    }
}
