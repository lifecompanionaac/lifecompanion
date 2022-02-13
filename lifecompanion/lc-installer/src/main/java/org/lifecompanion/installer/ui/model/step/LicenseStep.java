/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2021 CMRRF KERPAPE (Lorient, France)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.lifecompanion.installer.ui.model.step;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.installer.ui.model.InstallerStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LicenseStep extends VBox implements InstallerStep {
    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseStep.class);

    private final BooleanProperty nextButtonEnabled;

    private CheckBox checkBoxAcceptLicense;
    private ScrollPane scrollPaneLicense;

    public LicenseStep() {
        super(10.0);
        this.nextButtonEnabled = new SimpleBooleanProperty(false);
    }

    @Override
    public Node getContent() {
        return this;
    }

    // UI
    //========================================================================
    @Override
    public void initUI() {
        this.setAlignment(Pos.CENTER);
        Label labelExplainTitle = new Label(Translation.getText("lc.installer.step.license.label.explain.title"));
        labelExplainTitle.getStyleClass().add("label-explain-title");
        Label labelExplainText = new Label(Translation.getText("lc.installer.step.license.label.explain.text"));
        labelExplainText.setWrapText(true);
        labelExplainText.setTextAlignment(TextAlignment.JUSTIFY);

        Label labelLicense = new Label();
        labelLicense.getStyleClass().add("label-license");
        labelLicense.setMaxWidth(Double.MAX_VALUE);

        scrollPaneLicense = new ScrollPane(labelLicense);
        scrollPaneLicense.setMaxHeight(300);
        scrollPaneLicense.setFitToWidth(true);
        StringBuilder content = new StringBuilder();
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(LicenseStep.class.getResourceAsStream("/LICENSE"), StandardCharsets.UTF_8))) {
            String line;
            while ((line = bf.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load license", e);
        }
        labelLicense.setText(content.toString());

        checkBoxAcceptLicense = new CheckBox(Translation.getText("lc.installer.step.license.accept"));
        checkBoxAcceptLicense.setDisable(true);

        this.getChildren().addAll(labelExplainTitle, labelExplainText, scrollPaneLicense, checkBoxAcceptLicense);
    }

    @Override
    public void initListener() {
        scrollPaneLicense.vvalueProperty().addListener((obs, ov, nv) -> {
            if (nv != null && nv.doubleValue() > 0.95) {
                checkBoxAcceptLicense.setDisable(false);
            }
        });
        nextButtonEnabled.bind(checkBoxAcceptLicense.selectedProperty());
    }
    //========================================================================

    // MODEL
    //========================================================================
    @Override
    public void stepDisplayed() {
    }

    @Override
    public void stepHidden() {
    }

    @Override
    public ReadOnlyBooleanProperty previousButtonAvailable() {
        return new SimpleBooleanProperty(true);
    }

    @Override
    public ReadOnlyBooleanProperty nextButtonAvailable() {
        return this.nextButtonEnabled;
    }
    //========================================================================
}
