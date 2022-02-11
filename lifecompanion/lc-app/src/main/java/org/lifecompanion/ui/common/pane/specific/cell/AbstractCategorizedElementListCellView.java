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
package org.lifecompanion.ui.common.pane.specific.cell;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementI;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.controller.resource.IconManager;
import org.lifecompanion.ui.app.categorizedelement.AbstractCategorizedListManageView;
import org.lifecompanion.ui.app.categorizedelement.CategorizedIconView;

import java.util.function.BiConsumer;

/**
 * List cell for categorized element (display for action and event in order modifiable list views)
 *
 * @param <T> categorized element type
 */
public class AbstractCategorizedElementListCellView<T extends CategorizedElementI<?>> extends ListCell<T> {
    private static final double ICON_SIZE = 20;
    private Tooltip tooltip;

    private BiConsumer<Node, T> actionSelectedCallback;
    private Label labelActionName, labelActionDescription;
    private CategorizedIconView useActionIcon;
    private BorderPane borderPane;

    public AbstractCategorizedElementListCellView(final BiConsumer<Node, T> actionSelectedCallbackP) {
        super();
        this.actionSelectedCallback = actionSelectedCallbackP;
        this.getStyleClass().add("custom-list-cell");
        //Global
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.setPrefWidth(AbstractCategorizedListManageView.MAX_USEACTION_LIST_WIDTH - 20);
        this.setMaxWidth(AbstractCategorizedListManageView.MAX_USEACTION_LIST_WIDTH - 20);
        this.borderPane = new BorderPane();
        //Labels
        this.labelActionName = new Label();
        this.labelActionName.getStyleClass().add("use-action-cell-name");
        this.labelActionDescription = new Label();
        this.labelActionDescription.getStyleClass().add("use-action-cell-description");
        this.labelActionDescription
                .setMaxWidth(AbstractCategorizedListManageView.MAX_USEACTION_LIST_WIDTH - AbstractCategorizedElementListCellView.ICON_SIZE - 60);
        this.labelActionDescription.setWrapText(true);
        VBox boxLabels = new VBox();
        boxLabels.getChildren().addAll(this.labelActionName, this.labelActionDescription);
        this.borderPane.setCenter(boxLabels);
        //Icons
        this.useActionIcon = new CategorizedIconView();
        this.useActionIcon.setIconSize(AbstractCategorizedElementListCellView.ICON_SIZE);
        this.borderPane.setLeft(this.useActionIcon);
        BorderPane.setMargin(this.useActionIcon, new Insets(0, 10, 0, 2));
        //Tootip
        this.tooltip = UIUtils.createTooltip(null);
        //Double clic, edit
        this.setOnMouseClicked((me) -> {
            if (this.getItem() != null && me.getClickCount() > 1 && this.getItem().isParameterizableElement()) {
                this.actionSelectedCallback.accept(this, this.getItem());
            }
        });
    }

    @Override
    public void updateItem(final T item, final boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            this.labelActionName.textProperty().set(null);
            this.labelActionDescription.textProperty().unbind();
            this.labelActionDescription.textProperty().set(null);
            this.useActionIcon.circleColorProperty().unbind();
            this.useActionIcon.imageProperty().unbind();

            this.setTooltip(null);
            this.tooltip.textProperty().unbind();
            this.tooltip.textProperty().set(null);

            this.setGraphic(null);
        } else {
            this.setTooltip(this.tooltip);
            this.setGraphic(this.borderPane);
            //Bind properties
            this.labelActionName.textProperty().set(item.getName());
            this.labelActionDescription.textProperty().bind(item.variableDescriptionProperty());
            this.useActionIcon.circleColorProperty().set(item.getCategory().getColor());
            this.useActionIcon.imageProperty().set(IconManager.get(item.getConfigIconPath()));
            this.tooltip.textProperty().bind(item.variableDescriptionProperty());
        }
    }

}
