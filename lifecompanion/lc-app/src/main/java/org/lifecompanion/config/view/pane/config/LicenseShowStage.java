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

package org.lifecompanion.config.view.pane.config;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.lifecompanion.base.data.config.IconManager;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.data.config.ResourceHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LicenseShowStage extends Stage {
    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseShowStage.class);


    public LicenseShowStage(Window owner, String... licenseResourcePaths) {
        this.setTitle(LCConstant.NAME);
        this.initModality(Modality.APPLICATION_MODAL);
        this.initStyle(StageStyle.UTILITY);
        this.initOwner(owner);
        this.setWidth(800);
        this.setHeight(650);
        this.setResizable(false);
        this.setForceIntegerRenderScale(LCGraphicStyle.FORCE_INTEGER_RENDER_SCALE);
        this.getIcons().add(IconManager.get(LCConstant.LC_ICON_PATH));

        // Loading licenses
        StringBuilder content = new StringBuilder();
        for (String licenseResourcePath : licenseResourcePaths) {
            try (BufferedReader bf = new BufferedReader(new InputStreamReader(ResourceHelper.getInputStreamForPath(licenseResourcePath), StandardCharsets.UTF_8))) {
                String line;
                while ((line = bf.readLine()) != null) {
                    content.append(line).append("\n");
                }
                content.append("\n\n==================================================\n\n");
            } catch (Exception e) {
                LOGGER.error("Failed to load license information : ", licenseResourcePath, e);
            }
        }

        // Create UI
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(20.0));
        Scene contentScene = new Scene(borderPane);
        this.setScene(contentScene);

        TextArea textAreaContent = new TextArea();
        textAreaContent.setEditable(false);
        textAreaContent.setWrapText(true);
        textAreaContent.setStyle("-fx-font-family: \"Consolas\"");
        textAreaContent.setText(content.toString());
        borderPane.setCenter(textAreaContent);

        Button closeButton = new Button(Translation.getText("license.stage.close.button"));
        BorderPane.setAlignment(closeButton, Pos.CENTER_RIGHT);
        BorderPane.setMargin(closeButton, new Insets(10.0, 0, 0, 0));
        borderPane.setBottom(closeButton);
        closeButton.setOnAction(e -> close());
    }
}
