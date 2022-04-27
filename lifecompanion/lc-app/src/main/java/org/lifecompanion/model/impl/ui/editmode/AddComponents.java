package org.lifecompanion.model.impl.ui.editmode;

import javafx.scene.control.Alert;
import org.lifecompanion.controller.editaction.*;
import org.lifecompanion.controller.editmode.ComponentActionController;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.controller.io.task.UserCompLoadingTask;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.configurationcomponent.*;
import org.lifecompanion.model.api.editaction.BaseEditActionI;
import org.lifecompanion.model.api.editaction.UndoRedoActionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.api.profile.UserCompDescriptionI;
import org.lifecompanion.model.api.ui.editmode.AddComponentCategoryEnum;
import org.lifecompanion.model.api.ui.editmode.AddComponentI;
import org.lifecompanion.model.impl.configurationcomponent.*;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.ui.app.main.usercomponent.UserCompSelectorDialog;
import org.lifecompanion.ui.common.pane.specific.cell.UserCompListCell;
import org.lifecompanion.util.javafx.DialogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.function.Consumer;

public class AddComponents {
    private static final Logger LOGGER = LoggerFactory.getLogger(AddComponents.class);

    private static abstract class AbstractAddComponent implements AddComponentI {
        private final String iconPath;
        private final String nameID, descriptionID;
        private final AddComponentCategoryEnum category;
        private final Class<? extends DisplayableComponentI> selectionFilter;

        public AbstractAddComponent(String iconPath, String nameID, String descriptionID, AddComponentCategoryEnum category, Class<? extends DisplayableComponentI> selectionFilter) {
            this.iconPath = iconPath;
            this.nameID = nameID;
            this.descriptionID = descriptionID;
            this.category = category;
            this.selectionFilter = selectionFilter;
        }

        @Override
        public String getIconPath() {
            return iconPath;
        }

        @Override
        public String getNameID() {
            return nameID;
        }

        @Override
        public String getDescriptionID() {
            return descriptionID;
        }

        @Override
        public AddComponentCategoryEnum getCategory() {
            return category;
        }

        @Override
        public Class<? extends DisplayableComponentI> getSelectionFilter() {
            return selectionFilter;
        }
    }

    private static abstract class AbstractAddUserComponent extends AbstractAddComponent {
        private final Class<? extends DisplayableComponentI> userCompFilter;

        public AbstractAddUserComponent(String nameID, AddComponentCategoryEnum category, Class<? extends DisplayableComponentI> selectionFilter, Class<? extends DisplayableComponentI> userCompFilter) {
            super("component/add_user_model.png", nameID, "component.user.model.description", category, selectionFilter);
            this.userCompFilter = userCompFilter;
        }

        @Override
        public BaseEditActionI createAddAction() {
            return new BaseEditActionI() {
                @Override
                public void doAction() {
                    UserCompDescriptionI userCompDescriptionI = UserCompSelectorDialog.getInstance().showWithFilter(userCompFilter);
                    // Try to load it if it wasn't loaded yet
                    if (userCompDescriptionI != null) {
                        Runnable afterLoad = () -> ConfigActionController.INSTANCE.executeAction(createAddActionFor(userCompDescriptionI));
                        if (!userCompDescriptionI.getUserComponent().isLoaded()) {
                            // First, check plugin warning
                            LCProfileI currentProfile = ProfileController.INSTANCE.currentProfileProperty().get();
                            PluginActions.warnOnPluginDependencies(AppModeController.INSTANCE.getEditModeContext().getStage().getScene().getRoot(),
                                    new File(IOHelper.getUserCompPath(currentProfile.getID(), userCompDescriptionI.getSavedComponentId()) + File.separator + LCConstant.USER_COMP_XML_NAME),
                                    () -> {
                                        UserCompLoadingTask loadTask = IOHelper.createUserCompLoadingTask(userCompDescriptionI, currentProfile);
                                        loadTask.setOnSucceeded(event -> afterLoad.run());
                                        loadTask.setOnFailed(event -> {
                                            Throwable error = event.getSource().getException();
                                            LOGGER.warn("Couldn't load user component", error);
                                            //Disable drag
                                            StringBuilder sb = new StringBuilder();
                                            if (error instanceof LCException) {
                                                sb.append(((LCException) error).getUserMessage());
                                            } else {
                                                sb.append(Translation.getText("user.comp.loading.failed.message.start"));
                                                sb.append(error.getClass().getSimpleName()).append(" : ").append(error.getMessage());
                                            }
                                            DialogUtils
                                                    .alertWithSourceAndType(AppModeController.INSTANCE.getEditModeContext().getStage(), Alert.AlertType.ERROR)
                                                    .withHeaderText(Translation.getText("user.comp.loading.failed.header"))
                                                    .withContentText(sb.toString())
                                                    .show();
                                        });
                                        AsyncExecutorController.INSTANCE.addAndExecute(false, false, loadTask);
                                    });
                        } else afterLoad.run();
                    }
                }

                @Override
                public String getNameID() {
                    return "user.comp.add.preaction";
                }
            };
        }

        abstract BaseEditActionI createAddActionFor(UserCompDescriptionI userCompDescription);
    }

    public static class AddStack extends AbstractAddComponent {
        AddStack() {
            super("component/icon_add_stack.png", "component.stack.name", "component.stack.add.description", AddComponentCategoryEnum.ROOT, null);
        }

        @Override
        public UndoRedoActionI createAddAction() {
            StackComponent stack = new StackComponent();
            stack.widthProperty().set(450);
            stack.heightProperty().set(250);
            stack.xProperty().set(15);
            stack.yProperty().set(150);
            GridPartGridComponent grid = new GridPartGridComponent();
            grid.getGrid().setRow(4);
            grid.getGrid().setColumn(5);
            stack.getComponentList().add(grid);
            return new OptionActions.AddRootComponentAction(AppModeController.INSTANCE.getEditModeContext().getConfiguration(), stack);
        }
    }

    public static class AddTextEditor extends AbstractAddComponent {
        AddTextEditor() {
            super("component/text_editor.png", "component.texteditor.name", "component.texteditor.add.description", AddComponentCategoryEnum.ROOT, null);
        }

        @Override
        public UndoRedoActionI createAddAction() {
            TextEditorComponent textEditor = new TextEditorComponent();
            textEditor.widthProperty().set(450);
            textEditor.heightProperty().set(120);
            textEditor.xProperty().set(15);
            textEditor.yProperty().set(15);
            return new OptionActions.AddRootComponentAction(AppModeController.INSTANCE.getEditModeContext().getConfiguration(), textEditor);
        }
    }

    public static class AddUserModelRoot extends AbstractAddUserComponent {
        AddUserModelRoot() {
            super("add.user.model.name", AddComponentCategoryEnum.ROOT, null, RootGraphicComponentI.class);
        }

        @Override
        BaseEditActionI createAddActionFor(UserCompDescriptionI userCompDescription) {
            return new OptionActions.AddRootComponentAction(AppModeController.INSTANCE.getEditModeContext().getConfiguration(), userCompDescription.getUserComponent().createNewComponent());
        }
    }

    public static class AddGridInStack extends AbstractAddComponent {
        AddGridInStack() {
            super("component/add_grid.png", "add.component.grid.in.stack", "component.stack.add.description", AddComponentCategoryEnum.GRID, StackComponentI.class);
        }

        @Override
        public UndoRedoActionI createAddAction() {
            StackComponentI displayableComponent = (StackComponentI) SelectionController.INSTANCE.selectedDisplayableComponentHelperProperty().get();
            return new GridStackActions.AddGridInStackAction(displayableComponent, true, true);
        }
    }

    public static class AddGridInStackCopy extends AbstractAddComponent {
        AddGridInStackCopy() {
            super("component/clone_grid.png", "add.component.grid.in.copy", "component.stack.add.description", AddComponentCategoryEnum.GRID, StackComponentI.class);
        }

        @Override
        public UndoRedoActionI createAddAction() {
            StackComponentI displayableComponent = (StackComponentI) SelectionController.INSTANCE.selectedDisplayableComponentHelperProperty().get();
            GridComponentI displayed = displayableComponent.displayedComponentProperty().get();
            GridComponentI toAdd = ComponentActionController.createComponentCopy(displayed);
            return new GridStackActions.AddGridInStackAction(displayableComponent, toAdd, true, true);
        }
    }

    public static class AddUserModelGridInStack extends AbstractAddUserComponent {
        AddUserModelGridInStack() {
            super("add.user.model.name", AddComponentCategoryEnum.GRID, StackComponentI.class, GridComponentI.class);
        }

        @Override
        BaseEditActionI createAddActionFor(UserCompDescriptionI userCompDescription) {
            StackComponentI displayableComponent = (StackComponentI) SelectionController.INSTANCE.selectedDisplayableComponentHelperProperty().get();
            return new GridStackActions.AddGridInStackAction(displayableComponent, userCompDescription.getUserComponent().createNewComponent(), true, true);
        }
    }

    public static class ChangeKeyToGrid extends AbstractAddComponent {
        ChangeKeyToGrid() {
            super("component/grid.png", "change.key.to.grid", "component.user.model.description", AddComponentCategoryEnum.KEY, GridPartKeyComponentI.class);
        }

        @Override
        public UndoRedoActionI createAddAction() {
            GridPartKeyComponentI targetKey = SelectionController.INSTANCE.selectedKeyHelperProperty().get();
            GridPartGridComponent gridComponent = new GridPartGridComponent();
            gridComponent.getGrid().setRow(2);
            gridComponent.getGrid().setColumn(2);
            return new GridActions.ReplaceComponentAction(targetKey.gridParentProperty().get().getGrid(), targetKey, gridComponent, true);
        }
    }

    public static class ChangeKeyToTextEditor extends AbstractAddComponent {
        ChangeKeyToTextEditor() {
            super("component/text_editor.png", "change.key.to.text.editor", "component.user.model.description", AddComponentCategoryEnum.KEY, GridPartKeyComponentI.class);
        }

        @Override
        public UndoRedoActionI createAddAction() {
            GridPartKeyComponentI targetKey = SelectionController.INSTANCE.selectedKeyHelperProperty().get();
            GridPartTextEditorComponent textEditorComponent = new GridPartTextEditorComponent();
            return new GridActions.ReplaceComponentAction(targetKey.gridParentProperty().get().getGrid(), targetKey, textEditorComponent, true);
        }
    }

    public static class ChangeKeyToStack extends AbstractAddComponent {
        ChangeKeyToStack() {
            super("component/stack.png", "change.key.to.stack", "component.user.model.description", AddComponentCategoryEnum.KEY, GridPartKeyComponentI.class);
        }

        @Override
        public UndoRedoActionI createAddAction() {
            GridPartKeyComponentI targetKey = SelectionController.INSTANCE.selectedKeyHelperProperty().get();
            GridPartStackComponent gridPartStackComponent = new GridPartStackComponent();
            GridPartGridComponent grid = new GridPartGridComponent();
            grid.getGrid().setRow(4);
            grid.getGrid().setColumn(5);
            gridPartStackComponent.getComponentList().add(grid);
            return new GridActions.ReplaceComponentAction(targetKey.gridParentProperty().get().getGrid(), targetKey, gridPartStackComponent, true);
        }
    }

    public static class AddUserModelKey extends AbstractAddUserComponent {
        AddUserModelKey() {
            super("add.user.model.name", AddComponentCategoryEnum.KEY, GridPartKeyComponentI.class, GridPartKeyComponentI.class);
        }

        @Override
        BaseEditActionI createAddActionFor(UserCompDescriptionI userCompDescription) {
            GridPartKeyComponentI targetKey = SelectionController.INSTANCE.selectedKeyHelperProperty().get();
            return new GridActions.ReplaceComponentAction(targetKey.gridParentProperty().get().getGrid(), targetKey, userCompDescription.getUserComponent().createNewComponent(), true);
        }
    }
}
