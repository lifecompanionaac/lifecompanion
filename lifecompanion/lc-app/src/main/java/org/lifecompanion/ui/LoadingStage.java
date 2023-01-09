package org.lifecompanion.ui;

import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.lifecompanion.controller.editaction.GlobalActions;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.util.javafx.StageUtils;

public class LoadingStage extends Stage {
    public LoadingStage() {
        this.setForceIntegerRenderScale(LCGraphicStyle.FORCE_INTEGER_RENDER_SCALE);
        this.initStyle(StageStyle.DECORATED);
        this.setTitle(StageUtils.getStageDefaultTitle());
        this.setWidth(UserConfigurationController.INSTANCE.mainFrameWidthProperty().get());
        this.setHeight(UserConfigurationController.INSTANCE.mainFrameHeightProperty().get());
        this.setMaximized(UserConfigurationController.INSTANCE.launchMaximizedProperty().get());
        this.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        this.centerOnScreen();
        this.getIcons().add(IconHelper.get(LCConstant.LC_ICON_PATH));
        this.setAlwaysOnTop(true);

        LoadingScene loadingScene = new LoadingScene(new VBox());
        this.setScene(loadingScene);
        this.setOnHidden(e -> loadingScene.stopAndClear());
    }
}