package org.lifecompanion.model.impl.ui.editmode;

import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.configurationcomponent.StackComponentI;
import org.lifecompanion.model.api.editaction.UndoRedoActionI;
import org.lifecompanion.model.api.ui.editmode.AddComponentCategoryEnum;
import org.lifecompanion.model.api.ui.editmode.AddComponentI;

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
            return selectionFilter;
        }
    }

    public static class AddStack extends AbstractAddComponent {
        AddStack() {
            super("component/icon_add_stack.png", "component.stack.name", "component.stack.add.description", AddComponentCategoryEnum.ROOT, null);
        }

        @Override
        public UndoRedoActionI createAddAction() {
            return null;
        }
    }

    public static class AddTextEditor extends AbstractAddComponent {
        AddTextEditor() {
            super("component/icon_text_editor.png", "component.texteditor.name", "component.texteditor.add.description", AddComponentCategoryEnum.ROOT, null);
        }

        @Override
        public UndoRedoActionI createAddAction() {
            return null;
        }
    }

    public static class AddUserModelRoot extends AbstractAddComponent {
        AddUserModelRoot() {
            super("component/add_user_model.png", "add.user.model.name", "component.user.model.description", AddComponentCategoryEnum.ROOT, null);
        }

        @Override
        public UndoRedoActionI createAddAction() {
            return null;
        }
    }

    public static class AddGridInStack extends AbstractAddComponent {
        AddGridInStack() {
            super("component/icon_add_grid_in_stack.png", "add.component.grid.in.stack", "component.stack.add.description", AddComponentCategoryEnum.GRID, StackComponentI.class);
        }

        @Override
        public UndoRedoActionI createAddAction() {
            return null;
        }
    }


}
