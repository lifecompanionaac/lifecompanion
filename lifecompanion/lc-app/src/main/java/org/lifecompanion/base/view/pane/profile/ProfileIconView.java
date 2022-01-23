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
package org.lifecompanion.base.view.pane.profile;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.lifecompanion.api.component.definition.LCProfileI;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

/**
 * View to simply display the profile icon.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ProfileIconView extends StackPane implements LCViewInitHelper {
    private static final int CIRCLE_BASE_SIZE = 23;
    private static final int FONT_BASE_SIZE = 30;
    private ObjectProperty<LCProfileI> profile;
    private Label labelLetter;
    private Circle circleColor;

    private ChangeListener<String> nameChangeListener;

    public ProfileIconView() {
        this.profile = new SimpleObjectProperty<>();
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        this.circleColor = new Circle(CIRCLE_BASE_SIZE, LCGraphicStyle.MAIN_DARK);
        this.labelLetter = new Label();
        StackPane.setAlignment(this.circleColor, Pos.CENTER);
        StackPane.setAlignment(this.labelLetter, Pos.CENTER);
        this.getChildren().addAll(this.circleColor, this.labelLetter);
        //Label font
        this.labelLetter.setFont(createFont(FONT_BASE_SIZE));
        this.labelLetter.setTextFill(Color.WHITE);
    }

    private Font createFont(double size) {
        return Font.font("Arial", FontWeight.BOLD, size);
    }

    @Override
    public void initBinding() {
        //Init binding on name
        this.nameChangeListener = (obs, ov, nv) -> {
            this.nameChanged(nv);
        };
        //Bind on profile
        this.profile.addListener((obs, ov, nv) -> {
            //Unbind
            if (ov != null) {
                ov.nameProperty().removeListener(this.nameChangeListener);
                this.circleColor.fillProperty().unbind();
            }
            //Bind
            if (nv != null) {
                nv.nameProperty().addListener(this.nameChangeListener);
                this.circleColor.fillProperty().bind(nv.colorProperty());
                this.nameChanged(nv.nameProperty().get());
            }
        });
    }

    private void nameChanged(final String nv) {
        if (nv != null && !nv.trim().isEmpty()) {
            char firstChar = nv.trim().charAt(0);
            this.labelLetter.setText(("" + firstChar).toUpperCase());
        } else {
            this.labelLetter.setText("");
        }
    }

    public void setIconSizeFactor(double factor) {
        circleColor.setRadius(factor * CIRCLE_BASE_SIZE);
        this.labelLetter.setFont(createFont(factor * FONT_BASE_SIZE));
    }
    //========================================================================

    // Class part : "Public API"
    //========================================================================
    public ObjectProperty<LCProfileI> profileProperty() {
        return this.profile;
    }
    //========================================================================

}
