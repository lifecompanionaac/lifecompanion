package org.lifecompanion.util.pdf;

import java.io.File;

public class DocumentImagePage {
    private final String title;
    private final File imageFile;
    private final boolean landscape;

    public DocumentImagePage(String title, File imageFile, boolean landscape) {
        this.title = title;
        this.imageFile = imageFile;
        this.landscape = landscape;
    }

    public String getTitle() {
        return title;
    }

    public File getImageFile() {
        return imageFile;
    }

    public boolean isLandscape() {
        return landscape;
    }
}
