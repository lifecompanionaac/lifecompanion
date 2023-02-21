package org.lifecompanion.ui.training;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.DesktopUtils;
import org.lifecompanion.util.javafx.FXControlUtils;

import static org.lifecompanion.model.impl.constant.LCConstant.URL_TRAININGS;

public class TrainingInformationPane extends VBox implements LCViewInitHelper {

    private CheckBox checkboxNeverShowAgain;
    private Button buttonTrain;

    TrainingInformationPane() {
        initAll();
    }

    @Override
    public void initUI() {
        Label title = new Label(Translation.getText("training.information.stage.title"));
        title.getStyleClass().addAll("text-fill-primary-dark", "text-font-size-150", "text-weight-bold", "padding-b5");
        title.setAlignment(Pos.CENTER);

        Label explain = createLabel("training.information.stage.introduction.text");
        Label trainingTypes = createLabel("training.information.stage.diff.types");
        Label ecoModel = createLabel("training.information.stage.eco.model");
        ecoModel.getStyleClass().addAll("text-weight-bold");

        checkboxNeverShowAgain = new CheckBox(Translation.getText("training.information.checkbox.dont.show.again"));
        checkboxNeverShowAgain.getStyleClass().add("text-font-size-90");

        buttonTrain = FXControlUtils.createTextButtonWithBackground(Translation.getText("training.information.button.train"));
        buttonTrain.setPrefWidth(150.0);
        HBox boxTrain = new HBox(buttonTrain);
        boxTrain.setAlignment(Pos.CENTER);
        VBox.setMargin(boxTrain, new Insets(10));

        this.setSpacing(10.0);
        this.setAlignment(Pos.CENTER);
        setPadding(new Insets(15));
        this.getChildren().addAll(title, explain, trainingTypes, ecoModel, boxTrain, checkboxNeverShowAgain);
    }

    private static Label createLabel(String translation) {
        Label explain = new Label(Translation.getText(translation));
        explain.getStyleClass().addAll("text-wrap-enabled");
        explain.setMaxWidth(Double.MAX_VALUE);
        return explain;
    }

    @Override
    public void initListener() {
        buttonTrain.setOnAction(e -> DesktopUtils.openUrlInDefaultBrowser(InstallationController.INSTANCE.getBuildProperties().getAppServerUrl() + URL_TRAININGS));
    }

    public CheckBox getCheckboxNeverShowAgain() {
        return checkboxNeverShowAgain;
    }
}
