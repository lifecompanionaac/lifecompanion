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
package org.lifecompanion.util;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.controller.systemvk.SystemVirtualKeyboardController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.ui.editmode.ConfigurationProfileLevelEnum;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.util.model.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Class that keep utils method that are just use in config mode.<br>
 * This class contains method to create base needed dialog/notifications always with the same LC style.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
@SuppressWarnings("restriction")
public class ConfigUIUtils {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigUIUtils.class);

    private ConfigUIUtils() {
    }

    // Class part : "Dialog"
    //========================================================================
    public static TextInputDialog createInputDialog(Node source, final String defaultValue) {
        return createInputDialog(UIUtils.getSourceWindow(source), defaultValue);
    }

    public static TextInputDialog createInputDialog(Window window, final String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        ConfigUIUtils.setDialogIcon(dialog);
        dialog.setTitle(LCConstant.NAME);
        dialog.initOwner(window);
        SystemVirtualKeyboardController.INSTANCE.registerSceneFromDialog(dialog);
        return dialog;
    }

    public static Stage createApplicationModalStage(Window owner, final double width, final double height) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setTitle(LCConstant.NAME);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.getIcons().add(IconHelper.get(LCConstant.LC_ICON_PATH));
        stage.setForceIntegerRenderScale(LCGraphicStyle.FORCE_INTEGER_RENDER_SCALE);
        return stage;
    }

    public static Alert createAlert(Node source, final AlertType type) {
        return createAlert(UIUtils.getSourceWindow(source), type);
    }

    public static Alert createAlert(Window window, final AlertType type) {
        Alert dlg = new Alert(type);
        ConfigUIUtils.setDialogIcon(dlg);
        dlg.setTitle(LCConstant.NAME);
        dlg.initOwner(window);
        dlg.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        SystemVirtualKeyboardController.INSTANCE.registerSceneFromDialog(dlg);
        return dlg;
    }


    private static void setDialogIcon(final Dialog<?> dialog) {
        Stage ownerWindow = (Stage) dialog.getDialogPane().getScene().getWindow();
        ownerWindow.getIcons().add(IconHelper.get(LCConstant.LC_ICON_PATH));
    }

    //========================================================================
    public static ToggleSwitch createToggleSwitch(String toggleTextId, String tooltipTextId) {
        ToggleSwitch toggleSwitch = new ToggleSwitch(Translation.getText(toggleTextId));
        toggleSwitch.setMaxWidth(Double.MAX_VALUE);//Restore ToggleSwitch sizing before ControlsFX 8.40.13
        if (tooltipTextId != null) {
            toggleSwitch.setTooltip(UIUtils.createTooltip(Translation.getText(tooltipTextId)));
        }
        return toggleSwitch;
    }

    //========================================================================

    // Class part : "Profile level"
    //========================================================================
    private static SimpleObjectProperty<ConfigurationProfileLevelEnum> currentLevelProperty;
    private static Map<ConfigurationProfileLevelEnum, BooleanBinding> simpleFromLevelBindings;

    // FIXME : not used anymore : delete
    private static void initializeProfileBinding() {
        currentLevelProperty = new SimpleObjectProperty<>(ConfigurationProfileLevelEnum.EXPERT);
        simpleFromLevelBindings = new HashMap<>();
        //        AppController.INSTANCE.currentProfileProperty().addListener((obs, ov, nv) -> {
        //            if (ov != null) {
        //                currentLevelProperty.unbind();
        //            }
        //            if (nv != null) {
        //                currentLevelProperty.bind(nv.levelProperty());
        //            } else {
        //                currentLevelProperty.set(null);
        //            }
        //        });
        //        if (AppController.INSTANCE.currentProfileProperty().get() != null) {
        //            currentLevelProperty.bind(AppController.INSTANCE.currentProfileProperty().get().levelProperty());
        //        }
    }

    public static void bindShowForLevelFrom(final Node node, final ConfigurationProfileLevelEnum minLevel) {
        //No need of binding for the minimum level
        if (minLevel != ConfigurationProfileLevelEnum.BEGINNER) {
            checkLevelBindingInitialization();
            node.visibleProperty().bind(createOrGetForLevel(minLevel));
            node.managedProperty().bind(node.visibleProperty());
        }
    }

    private static void checkLevelBindingInitialization() {
        //Check if binding is initialized
        if (currentLevelProperty == null) {
            initializeProfileBinding();
        }
    }

    public static void bindShowForLevelFromAnd(final Node node, final ConfigurationProfileLevelEnum minLevel, final BooleanBinding otherBinding) {
        if (minLevel != ConfigurationProfileLevelEnum.BEGINNER) {
            checkLevelBindingInitialization();
            node.visibleProperty().bind(createOrGetForLevel(minLevel).and(otherBinding));
            node.managedProperty().bind(node.visibleProperty());
        }
    }

    private static BooleanBinding createOrGetForLevel(final ConfigurationProfileLevelEnum minLevel) {
        if (!simpleFromLevelBindings.containsKey(minLevel)) {
            simpleFromLevelBindings.put(minLevel, Bindings.createBooleanBinding(() -> {
                ConfigurationProfileLevelEnum currentLevel = currentLevelProperty.get();
                return currentLevel != null && currentLevel.ordinal() >= minLevel.ordinal();
            }, currentLevelProperty));
        }
        return simpleFromLevelBindings.get(minLevel);
    }
    //========================================================================

}
