package org.lifecompanion.ui.training;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.lifecompanion.controller.editmode.LCStateController;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;

public class TrainingInformationStage extends Stage {

    public TrainingInformationStage(final Window owner) {
        this.setTitle(LCConstant.NAME);
        this.initModality(Modality.APPLICATION_MODAL);
        this.initStyle(StageStyle.UTILITY);
        this.initOwner(owner);
        this.setWidth(450);
        this.setHeight(400);
        this.setResizable(LCGraphicStyle.TOOL_STAGE_RESIZABLE);
        TrainingInformationPane trainingInformationPane = new TrainingInformationPane();
        Scene scene = new Scene(trainingInformationPane);
        scene.getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
        this.setScene(scene);
        this.setForceIntegerRenderScale(LCGraphicStyle.FORCE_INTEGER_RENDER_SCALE);
        this.getIcons().add(IconHelper.get(LCConstant.LC_ICON_PATH));
        this.setOnHidden(e -> {
            LCStateController.INSTANCE.setLastTrainingDialogShow(System.currentTimeMillis());
            LCStateController.INSTANCE.hideTrainingDialogProperty().set(trainingInformationPane.getCheckboxNeverShowAgain().isSelected());
        });
    }
}
