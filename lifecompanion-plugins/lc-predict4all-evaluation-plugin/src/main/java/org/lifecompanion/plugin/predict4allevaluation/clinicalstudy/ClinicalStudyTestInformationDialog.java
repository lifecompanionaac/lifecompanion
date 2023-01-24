/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.plugin.predict4allevaluation.clinicalstudy;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.framework.client.props.ApplicationBuildProperties;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.predict4all.nlp.Predict4AllInfo;

import java.util.Arrays;
import java.util.Date;

public class ClinicalStudyTestInformationDialog extends Dialog<ClinicalStudyTestInformationDto> implements LCViewInitHelper {

    private TextField fieldUserId;
    private ComboBox<ClinicalStudyTestContext> comboboxTestContext;
    private TextArea textAreaContextOtherDescription;

    public ClinicalStudyTestInformationDialog() {
        this.initAll();
    }

    @Override
    public void initUI() {
        // Base
        this.setTitle(Translation.getText("predict4all.config.clinical.study.test.title"));
        this.setHeaderText(Translation.getText("predict4all.config.clinical.study.test.header"));
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        //Content
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10.0);
        gridPane.setVgap(5.0);

        // Fields
        this.fieldUserId = new TextField();
        this.comboboxTestContext = new ComboBox<>(FXCollections.observableList(Arrays.asList(ClinicalStudyTestContext.values())));
        this.comboboxTestContext.setConverter(new ClinicalStudyTestContextStrConverter());
        this.comboboxTestContext.setMaxWidth(Double.MAX_VALUE);
        Label labelOtherDescription = new Label(Translation.getText("predict4all.config.ui.field.other.description"));
        this.textAreaContextOtherDescription = new TextArea();
        this.textAreaContextOtherDescription.setPrefRowCount(2);
        labelOtherDescription.visibleProperty().bind(this.textAreaContextOtherDescription.visibleProperty());

        ApplicationBuildProperties buildProperties = InstallationController.INSTANCE.getBuildProperties();

        Label labelVersionInfo = new Label(Translation.getText("predict4all.config.version.check.name", buildProperties.getVersionLabel(),
                buildProperties.getBuildDate() != null ? StringUtils.dateToStringWithoutHour(buildProperties.getBuildDate()) : "UNKNOWN",
                buildProperties.getVersionLabel(), Predict4AllInfo.VERSION,
                Predict4AllInfo.BUILD_DATE != null ? StringUtils.dateToStringWithoutHour(Predict4AllInfo.BUILD_DATE) : "UNKNOWN"));
        labelVersionInfo.setFont(Font.font(null, FontWeight.BOLD, 12));

        gridPane.add(labelVersionInfo, 0, 0);
        gridPane.add(new Label(Translation.getText("predict4all.config.ui.field.user.id")), 0, 1);
        gridPane.add(fieldUserId, 0, 2);
        gridPane.add(new Label(Translation.getText("predict4all.config.ui.field.context")), 0, 3);
        gridPane.add(comboboxTestContext, 0, 4);
        gridPane.add(labelOtherDescription, 0, 5);
        gridPane.add(textAreaContextOtherDescription, 0, 6);

        this.getDialogPane().setContent(gridPane);
    }

    @Override
    public void initListener() {
        ApplicationBuildProperties buildProperties = InstallationController.INSTANCE.getBuildProperties();
        this.setResultConverter(bt -> {
            if (bt.getButtonData() == ButtonData.OK_DONE) {
                return new ClinicalStudyTestInformationDto(new Date(), this.fieldUserId.getText(), this.comboboxTestContext.getValue(),
                        this.comboboxTestContext.getValue() == ClinicalStudyTestContext.OTHER ? this.textAreaContextOtherDescription.getText() : null,
                        buildProperties.getVersionLabel() + " "
                                + (buildProperties.getBuildDate() != null ? StringUtils.dateToStringWithoutHour(buildProperties.getBuildDate()) : "UNKNOWN"),
                        buildProperties.getVersionLabel(),
                        Predict4AllInfo.VERSION + " "
                                + (Predict4AllInfo.BUILD_DATE != null ? StringUtils.dateToStringWithoutHour(Predict4AllInfo.BUILD_DATE)
                                : "UNKNOWN"));
            } else {
                return null;
            }
        });
    }

    @Override
    public void initBinding() {
        this.textAreaContextOtherDescription.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
            return this.comboboxTestContext.valueProperty().get() == ClinicalStudyTestContext.OTHER;
        }, this.comboboxTestContext.valueProperty()));
    }

    private static class ClinicalStudyTestContextStrConverter extends StringConverter<ClinicalStudyTestContext> {

        @Override
        public String toString(ClinicalStudyTestContext object) {
            return object != null ? Translation.getText(object.getNameId()) : "";
        }

        @Override
        public ClinicalStudyTestContext fromString(String str) {
            for (ClinicalStudyTestContext elem : ClinicalStudyTestContext.values()) {
                if (StringUtils.isEquals(Translation.getText(elem.getNameId()), str)) {
                    return elem;
                }
            }
            return null;
        }
    }

}
