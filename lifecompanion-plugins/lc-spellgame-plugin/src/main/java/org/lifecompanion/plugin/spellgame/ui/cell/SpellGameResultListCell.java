package org.lifecompanion.plugin.spellgame.ui.cell;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Pair;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.plugin.spellgame.model.GameStepEnum;
import org.lifecompanion.plugin.spellgame.model.SpellGameResult;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.DesktopUtils;
import org.lifecompanion.util.binding.BindingUtils;
import org.lifecompanion.util.javafx.AbstractAlertBuilder;
import org.lifecompanion.util.javafx.DialogUtils;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.io.File;
import java.net.MalformedURLException;

import static org.lifecompanion.plugin.spellgame.model.SpellGameConstant.RESULT_HTML_FILE_NAME;

public class SpellGameResultListCell extends ListCell<Pair<SpellGameResult, File>> {

    private final GridPane gridPaneGraphics;
    private final Label labelName;
    private final Label labelDate;
    private final Button buttonRemove;
    private final Button buttonView;

    public SpellGameResultListCell(ListView<Pair<SpellGameResult, File>> listView) {
        //Global
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.getStyleClass().add("list-cell-selection-disabled");

        //Labels
        this.labelName = new Label();
        this.labelName.getStyleClass().addAll("text-fill-primary-dark", "text-font-size-120");
        GridPane.setHgrow(labelName, Priority.ALWAYS);
        labelName.setMaxWidth(Double.MAX_VALUE);

        this.labelDate = new Label();
        this.labelDate.getStyleClass().addAll("text-fill-dimgrey", "text-font-size-110");

        buttonRemove = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TRASH).size(22).color(LCGraphicStyle.SECOND_DARK), null);
        buttonView = FXControlUtils.createLeftTextButton(Translation.getText(
                "spellgame.plugin.config.game.result.cell.action.open"), GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_RIGHT).size(22).color(LCGraphicStyle.MAIN_DARK), "TODO");
        GridPane.setMargin(buttonView, new Insets(0, 10, 0, 0));

        gridPaneGraphics = new GridPane();
        gridPaneGraphics.setHgap(5.0);
        gridPaneGraphics.setVgap(5.0);

        gridPaneGraphics.add(labelName, 0, 0);
        gridPaneGraphics.add(labelDate, 0, 1);
        gridPaneGraphics.add(buttonView, 1, 0, 1, 2);
        gridPaneGraphics.add(buttonRemove, 2, 0, 1, 2);

        buttonRemove.setOnAction(e -> {
            Pair<SpellGameResult, File> item = this.getItem();
            if (item != null) {
                if (DialogUtils.alertWithSourceAndType(listView, Alert.AlertType.CONFIRMATION)
                        .withContentText(Translation.getText("spellgame.plugin.config.game.confirm.remove.text"))
                        .showAndWait() == ButtonType.OK) {
                    AsyncExecutorController.INSTANCE.addAndExecute(false, false, () -> {
                        IOUtils.deleteDirectoryAndChildren(item.getValue());
                    }, () -> listView.getItems().remove(item));
                }
            }
        });
        buttonView.setOnAction(e -> {
            Pair<SpellGameResult, File> item = this.getItem();
            if (item != null) {
                final File htmlFile = new File(item.getValue() + File.separator + RESULT_HTML_FILE_NAME);
                if (htmlFile.exists()) {
                    try {
                        DesktopUtils.openUrlInDefaultBrowser(htmlFile.toURI().toURL().toString());
                    } catch (MalformedURLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }

    @Override
    protected void updateItem(Pair<SpellGameResult, File> item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            BindingUtils.unbindAndSetNull(labelName.textProperty());
            BindingUtils.unbindAndSetNull(labelDate.textProperty());
            this.setGraphic(null);
        } else {
            SpellGameResult result = item.getKey();
            this.setGraphic(this.gridPaneGraphics);
            this.labelName.setText(result.getListName() + " - " + result.getScore() + " / " + result.getDoneCount() * GameStepEnum.values().length);
            this.labelDate.setText(StringUtils.dateToStringDateWithHour(result.getCreateAt()));
        }
    }
}
