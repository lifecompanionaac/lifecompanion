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
package org.lifecompanion.ui.app.userconfiguration;

import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.lifecompanion.util.DesktopUtils;
import org.lifecompanion.util.javafx.DialogUtils;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.javafx.FXUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.function.Function;

/**
 * Tab to display info on the projet to user.
 *
 * @author Mathieu THEBAUD
 */
public class AboutSubmenu extends VBox implements LCViewInitHelper, UserConfigSubmenuI {
    private final static Logger LOGGER = LoggerFactory.getLogger(AboutSubmenu.class);
    private ImageView imageViewLogo, imageViewCopyright;
    private Label labelGeneralInfo, labelVersionInfo, labelUpdateWebsite, labelLastUpdateDate;
    private Hyperlink buttonThirdPartyLicenses, buttonLicense;
    private Hyperlink linkWebsite;
    private Button buttonInstallationId, buttonDeviceId;

    private ProgressBar progressBarUpdateTask;
    private Label labelUpdateTaskMessage;
    private Button buttonCheckUpdate;

    private InstallationController.InstallationRegistrationInformation registrationInformation;

    public AboutSubmenu() {
        this.initAll();
    }

    @Override
    public Region getView() {
        return this;
    }

    @Override
    public void initUI() {
        // Top : logo, general info, website
        imageViewLogo = new ImageView(IconHelper.get(LCConstant.LC_BIG_ICON_PATH));
        imageViewLogo.setFitHeight(70);
        imageViewLogo.setSmooth(true);
        imageViewLogo.setPreserveRatio(true);

        imageViewCopyright = new ImageView(IconHelper.get(LCConstant.LC_COPYRIGHT_ICON_PATH));
        imageViewCopyright.setFitHeight(70);
        imageViewCopyright.setSmooth(true);
        imageViewCopyright.setPreserveRatio(true);

        HBox boxImages = new HBox(20.0, imageViewCopyright, imageViewLogo);
        boxImages.setAlignment(Pos.CENTER);

        labelGeneralInfo = new Label(Translation.getText("about.tab.general.lc.info"));
        labelGeneralInfo.getStyleClass().add("general-app-info-label");
        labelGeneralInfo.setTextAlignment(TextAlignment.JUSTIFY);
        labelGeneralInfo.setMinHeight(80);

        labelVersionInfo = new Label();
        labelVersionInfo.getStyleClass().add("general-app-info-label");
        labelVersionInfo.setTextAlignment(TextAlignment.CENTER);
        labelVersionInfo.setMinHeight(80);

        labelUpdateWebsite = new Label(Translation.getText("about.tab.update.website.url", InstallationController.INSTANCE.getBuildProperties().getUpdateServerUrl()));
        labelUpdateWebsite.getStyleClass().add("general-app-info-label");
        labelUpdateWebsite.setTextAlignment(TextAlignment.CENTER);

        linkWebsite = new Hyperlink(InstallationController.INSTANCE.getBuildProperties().getAppServerUrl());

        buttonInstallationId = FXControlUtils.createSimpleTextButton(Translation.getText("about.tab.button.installation.id", Translation.getText("about.tab.id.null")), null);
        buttonDeviceId = FXControlUtils.createSimpleTextButton(Translation.getText("about.tab.button.device.id", Translation.getText("about.tab.id.null")), null);
        buttonDeviceId.setManaged(false);
        buttonDeviceId.setVisible(false);

        buttonLicense = new Hyperlink(Translation.getText("about.tab.license.app"));
        buttonThirdPartyLicenses = new Hyperlink(Translation.getText("about.tab.license.third.party"));


        // Update part
        this.buttonCheckUpdate = FXControlUtils.createLeftTextButton(Translation.getText("about.tab.check.update.now"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.REFRESH).size(14).color(LCGraphicStyle.MAIN_PRIMARY), null);
        this.labelLastUpdateDate = new Label();
        this.progressBarUpdateTask = new ProgressBar(-1);
        this.labelUpdateTaskMessage = new Label();
        labelUpdateTaskMessage.getStyleClass().add("text-font-italic");
        this.progressBarUpdateTask.setPrefHeight(15.0);
        this.progressBarUpdateTask.setPrefWidth(150.0);

        this.setAlignment(Pos.CENTER);
        this.setSpacing(5.0);
        this.getChildren().addAll(boxImages, new Separator(Orientation.HORIZONTAL), labelGeneralInfo, linkWebsite, new Separator(Orientation.HORIZONTAL), labelVersionInfo, labelUpdateWebsite, buttonInstallationId, buttonDeviceId, new Separator(Orientation.HORIZONTAL), this.labelLastUpdateDate, buttonCheckUpdate, progressBarUpdateTask, labelUpdateTaskMessage, new Separator(Orientation.HORIZONTAL), buttonLicense, buttonThirdPartyLicenses);
    }

    @Override
    public void initListener() {
        this.linkWebsite.setOnAction(a -> {
            boolean openOk = DesktopUtils.openUrlInDefaultBrowser(InstallationController.INSTANCE.getBuildProperties().getAppServerUrl());
            if (!openOk) {
                DialogUtils.alertWithSourceAndType(this.linkWebsite, AlertType.ERROR)
                        .withContentText(Translation.getText("action.cant.open.browser.message", InstallationController.INSTANCE.getBuildProperties().getAppServerUrl()))
                        .withHeaderText(Translation.getText("action.cant.open.browser.header"))
                        .show();
            }
        });
        this.buttonLicense.setOnAction(e -> new LicenseShowStage(FXUtils.getSourceWindow(buttonLicense), "LICENSE").show());
        this.buttonThirdPartyLicenses.setOnAction(e -> new LicenseShowStage(FXUtils.getSourceWindow(buttonThirdPartyLicenses), "custom-THIRD-PARTY-NOTICES.txt", "THIRD-PARTY-NOTICES.txt").show());
        buttonDeviceId.setOnAction(e -> setContentInfoWith(InstallationController.InstallationRegistrationInformation::getDeviceId));
        buttonInstallationId.setOnAction(e -> setContentInfoWith(InstallationController.InstallationRegistrationInformation::getInstallationId));
        buttonCheckUpdate.setOnAction(e -> InstallationController.INSTANCE.launchUpdateCheckProcess(true));
    }


    @Override
    public void initBinding() {
        buttonCheckUpdate.disableProperty().bind(InstallationController.INSTANCE.updateTaskRunningProperty());
        progressBarUpdateTask.progressProperty().bind(InstallationController.INSTANCE.updateTaskProgressProperty());
        labelUpdateTaskMessage.textProperty().bind(InstallationController.INSTANCE.updateTaskMessageProperty());

        progressBarUpdateTask.visibleProperty().bind(InstallationController.INSTANCE.updateTaskRunningProperty());
        labelUpdateTaskMessage.visibleProperty().bind(InstallationController.INSTANCE.updateTaskRunningProperty());

        labelLastUpdateDate.textProperty().bind(Bindings.createStringBinding(() -> {
            Date lastUpdateCheckDate = InstallationController.INSTANCE.lastUpdateCheckDateProperty().get();
            return Translation.getText("update.status.last.check.date", lastUpdateCheckDate != null ? StringUtils.dateToStringDateWithHour(lastUpdateCheckDate) : Translation.getText("about.tab.last.update.check.never"));
        }, InstallationController.INSTANCE.lastUpdateCheckDateProperty()));
    }

    private void setContentInfoWith(Function<InstallationController.InstallationRegistrationInformation, String> getter) {
        if (this.registrationInformation != null) {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(getter.apply(this.registrationInformation));
            clipboard.setContent(content);
            LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo("about.notification.id.copied"));
        }
    }

    @Override
    public String getTabTitleId() {
        return "user.config.tab.about";
    }

    @Override
    public void updateFields() {
        // Version info
        labelVersionInfo.setText(Translation.getText("about.tab.update.info",
                InstallationController.INSTANCE.getBuildProperties().getVersionLabel(),
                StringUtils.dateToStringWithoutHour(InstallationController.INSTANCE.getBuildProperties().getBuildDate())));

        // Registration info
        registrationInformation = InstallationController.INSTANCE.getInstallationRegistrationInformation();
        buttonInstallationId.setText(Translation.getText("about.tab.button.installation.id", registrationInformation != null ? registrationInformation.getInstallationId() : Translation.getText("about.tab.id.null")));
        buttonDeviceId.setText(Translation.getText("about.tab.button.device.id", registrationInformation != null ? registrationInformation.getDeviceId() : Translation.getText("about.tab.id.null")));
    }

    @Override
    public void updateModel() {
    }
}
