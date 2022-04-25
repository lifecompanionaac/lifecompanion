package org.lifecompanion.model.impl.ui.editmode;

import gnu.trove.impl.sync.TSynchronizedShortObjectMap;
import org.lifecompanion.controller.editaction.GridActions;
import org.lifecompanion.controller.editaction.GridStackActions;
import org.lifecompanion.controller.editaction.OptionActions;
import org.lifecompanion.controller.editmode.ComponentActionController;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.StackComponentI;
import org.lifecompanion.model.api.editaction.UndoRedoActionI;
import org.lifecompanion.model.api.profile.UserCompDescriptionI;
import org.lifecompanion.model.api.ui.editmode.AddComponentCategoryEnum;
import org.lifecompanion.model.api.ui.editmode.AddComponentI;
import org.lifecompanion.model.impl.configurationcomponent.*;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.ui.app.main.usercomponent.UserCompSelectorDialog;

import java.util.Optional;

public class AddComponents {
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
            return selectionFilter;//FIXME : use it
        }
    }

    private static abstract class AbstractAddUserComponent extends AbstractAddComponent {

        public AbstractAddUserComponent(String nameID, AddComponentCategoryEnum category, Class<? extends DisplayableComponentI> selectionFilter) {
            super("component/add_user_model.png", nameID, "component.user.model.description", category, selectionFilter);
        }

        @Override
        public UndoRedoActionI createAddAction() {
            //FIXME : create an action that open the correct selector and call add action on selection finished
            return new UndoRedoActionI() {
                @Override
                public void undoAction() throws LCException {

                }

                @Override
                public void redoAction() throws LCException {

                }

                @Override
                public void doAction() throws LCException {
                    Optional<UserCompDescriptionI> userCompDescriptionI = UserCompSelectorDialog.getInstance().showAndWait();
                }

                @Override
                public String getNameID() {
                    return null;
                }
            };
        }
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
            super("add.user.model.name", AddComponentCategoryEnum.ROOT, null);
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
            super("add.user.model.name", AddComponentCategoryEnum.GRID, null);
        }
    }

    public static class ChangeKeyToGrid extends AbstractAddComponent {
        ChangeKeyToGrid() {
            super("component/grid.png", "change.key.to.grid", "component.user.model.description", AddComponentCategoryEnum.KEY, null);
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
            super("component/text_editor.png", "change.key.to.text.editor", "component.user.model.description", AddComponentCategoryEnum.KEY, null);
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
            super("component/stack.png", "change.key.to.stack", "component.user.model.description", AddComponentCategoryEnum.KEY, null);
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
            super("add.user.model.name", AddComponentCategoryEnum.KEY, null);
        }
    }
}
