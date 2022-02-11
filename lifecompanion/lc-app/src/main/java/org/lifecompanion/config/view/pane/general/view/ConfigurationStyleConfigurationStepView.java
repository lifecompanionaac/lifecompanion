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

package org.lifecompanion.config.view.pane.general.view;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.base.view.reusable.GeneralConfigurationStep;
import org.lifecompanion.base.view.reusable.GeneralConfigurationStepViewI;
import org.lifecompanion.config.view.pane.tabs.style2.view.AbstractStyleEditView;
import org.lifecompanion.config.view.pane.tabs.style2.view.key.KeyStyleEditView;
import org.lifecompanion.config.view.pane.tabs.style2.view.shape.ShapeStyleEditView;
import org.lifecompanion.config.view.pane.tabs.style2.view.text.TextStyleEditView;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ConfigurationStyleConfigurationStepView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {
    private final List<Pair<AbstractStyleEditView, Function<LCConfigurationI, Object>>> styleEditViews;

    public ConfigurationStyleConfigurationStepView() {
        this.styleEditViews = new ArrayList<>();
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "general.configuration.view.step.styles.title";
    }

    @Override
    public String getStep() {
        return GeneralConfigurationStep.STYLES.name();
    }


    @Override
    public String getPreviousStep() {
        return null;
    }

    @Override
    public Node getViewNode() {
        return this;
    }


    // UI
    //========================================================================
    @Override
    public void initUI() {
        Label labelGeneralExplain = new Label(Translation.getText("general.configuration.styles.general.explain"));
        labelGeneralExplain.getStyleClass().add("explain-text");

        VBox boxChildren = new VBox(GeneralConfigurationStepViewI.GRID_V_GAP, labelGeneralExplain);
        boxChildren.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));

        // Keys
        addStyleEditView(boxChildren, "style.ribbon.part.key.style.plural", new KeyStyleEditView(true), LCConfigurationI::getKeyStyle);
        addStyleEditView(boxChildren, "style.ribbon.part.key.text.style.plural", new TextStyleEditView(true), LCConfigurationI::getKeyTextStyle);

        // Grid
        addStyleEditView(boxChildren, "style.ribbon.part.shape.style.plural", new ShapeStyleEditView(), LCConfigurationI::getGridShapeStyle);

        // Text displayer
        addStyleEditView(boxChildren, "style.ribbon.part.textdisplayer.text.style.plural", new TextStyleEditView(true), LCConfigurationI::getTextDisplayerTextStyle);
        addStyleEditView(boxChildren, "style.ribbon.part.textdisplayer.shape.style.plural", new ShapeStyleEditView(), LCConfigurationI::getTextDisplayerShapeStyle);


        ScrollPane scrollPane = new ScrollPane(boxChildren);
        scrollPane.setFitToWidth(true);
        this.setCenter(scrollPane);
    }

    private void addStyleEditView(VBox box, String titleId, AbstractStyleEditView styleEditView, Function<LCConfigurationI, Object> styleGetter) {
        Label labelTitle = UIUtils.createTitleLabel(Translation.getText(titleId));
        box.getChildren().addAll(labelTitle, styleEditView);
        this.styleEditViews.add(Pair.of(styleEditView, styleGetter));
    }
    //========================================================================


    @Override
    public void saveChanges() {
    }

    @Override
    public void bind(LCConfigurationI model) {
        styleEditViews.forEach(p -> p.getLeft().modelProperty().set(p.getRight().apply(model)));
    }

    @Override
    public void unbind(LCConfigurationI model) {
        styleEditViews.forEach(p -> p.getLeft().modelProperty().set(null));
    }
}
