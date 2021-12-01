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
package org.lifecompanion.base.data.ui;


import org.lifecompanion.api.component.definition.*;
import org.lifecompanion.api.ui.AddTypeEnum;
import org.lifecompanion.api.ui.PossibleAddCategoryEnum;
import org.lifecompanion.api.ui.PossibleAddComponentCategoryI;
import org.lifecompanion.api.ui.PossibleAddComponentI;
import org.lifecompanion.api.ui.config.ConfigurationProfileLevelEnum;
import org.lifecompanion.base.data.component.keyoption.*;
import org.lifecompanion.base.data.component.keyoption.note.NoteKeyOption;
import org.lifecompanion.base.data.component.simple.*;

/**
 * Class that implements all the {@link PossibleAddComponentI} and that keep them into a observable list.
 * TODO : description on each possible add
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class PossibleAddComponents {

    // Class part : "Component adder"
    // ========================================================================

    /**
     * Grid component to add
     */
    public static enum AddTextEditor implements PossibleAddComponentI<WriterDisplayerI> {
        INSTANCE;

        @Override
        public String getIconPath() {
            return "component/icon_text_editor.png";
        }

        @Override
        public String getNameID() {
            return "component.texteditor.name";
        }

        @Override
        public WriterDisplayerI getNewComponent(final AddTypeEnum addType, final Object... optionalParams) {
            if (addType == AddTypeEnum.GRID_PART) {
                GridPartTextEditorComponent gridPartTextEditorComponent = new GridPartTextEditorComponent();
                return gridPartTextEditorComponent;
            } else {
                TextEditorComponent textEditor = new TextEditorComponent();
                textEditor.widthProperty().set(450);
                textEditor.heightProperty().set(120);
                return textEditor;
            }
        }

        @Override
        public AddTypeEnum[] getAllowedAddType() {
            return new AddTypeEnum[]{AddTypeEnum.ROOT, AddTypeEnum.GRID_PART};
        }

        @Override
        public String getDescriptionID() {
            return "component.texteditor.add.description";
        }

        @Override
        public ConfigurationProfileLevelEnum getMinimumLevel() {
            return ConfigurationProfileLevelEnum.BEGINNER;
        }

        @Override
        public PossibleAddComponentCategoryI getCategory() {
            return PossibleAddCategoryEnum.BASE_COMPONENT;
        }
    }

    /**
     * Grid stack component to add
     */
    public static enum AddGridInKey implements PossibleAddComponentI<GridComponentI> {
        INSTANCE;

        @Override
        public String getIconPath() {
            return "component/icon_add_grid.png";
        }

        @Override
        public String getNameID() {
            return "component.grid.in.key";
        }

        @Override
        public GridComponentI getNewComponent(final AddTypeEnum addType, final Object... optionalParams) {
            // First grid
            GridPartGridComponent keypad = new GridPartGridComponent();
            keypad.getGrid().setRow(4);
            keypad.getGrid().setColumn(5);
            return keypad;
        }

        @Override
        public AddTypeEnum[] getAllowedAddType() {
            return new AddTypeEnum[]{AddTypeEnum.GRID_PART};
        }

        @Override
        public String getDescriptionID() {
            return "component.grid.in.key.description";
        }

        @Override
        public ConfigurationProfileLevelEnum getMinimumLevel() {
            return ConfigurationProfileLevelEnum.BEGINNER;
        }

        @Override
        public PossibleAddComponentCategoryI getCategory() {
            return PossibleAddCategoryEnum.GRIDS;
        }
    }

    /**
     * Add a grid in a stack : this instance is just used to flag the possible add, a specific behavior is implemented in DragController.
     */
    public static enum AddGridInStack implements PossibleAddComponentI<GridComponentI> {
        INSTANCE;

        @Override
        public String getIconPath() {
            return "component/icon_add_grid_in_stack.png";
        }

        @Override
        public String getNameID() {
            return "component.grid.in.stack";
        }

        @Override
        public GridComponentI getNewComponent(final AddTypeEnum addType, final Object... optionalParams) {
            return null;
        }

        @Override
        public AddTypeEnum[] getAllowedAddType() {
            return new AddTypeEnum[]{AddTypeEnum.GRID_PART};
        }

        @Override
        public String getDescriptionID() {
            return "component.grid.in.stack.description";
        }

        @Override
        public ConfigurationProfileLevelEnum getMinimumLevel() {
            return ConfigurationProfileLevelEnum.BEGINNER;
        }

        @Override
        public PossibleAddComponentCategoryI getCategory() {
            return PossibleAddCategoryEnum.GRIDS;
        }
    }

    public static enum AddStack implements PossibleAddComponentI<StackComponentI> {
        INSTANCE;

        @Override
        public String getIconPath() {
            return "component/icon_add_stack.png";
        }

        @Override
        public String getNameID() {
            return "component.stack.name";
        }

        @Override
        public StackComponentI getNewComponent(final AddTypeEnum addType, final Object... optionalParams) {
            StackComponentI stack = null;
            if (addType == AddTypeEnum.ROOT) {
                stack = new StackComponent();
                RootGraphicComponentI rootGraphic = (RootGraphicComponentI) stack;
                rootGraphic.widthProperty().set(450);
                rootGraphic.heightProperty().set(250);
            } else if (addType == AddTypeEnum.GRID_PART) {
                stack = new GridPartStackComponent();
            }
            // First and second grid
            stack.getComponentList().add(AddGridInKey.INSTANCE.getNewComponent(AddTypeEnum.STACK));
            return stack;
        }

        @Override
        public AddTypeEnum[] getAllowedAddType() {
            return new AddTypeEnum[]{AddTypeEnum.ROOT, AddTypeEnum.GRID_PART};
        }

        @Override
        public String getDescriptionID() {
            return "component.stack.add.description";
        }

        @Override
        public ConfigurationProfileLevelEnum getMinimumLevel() {
            return ConfigurationProfileLevelEnum.BEGINNER;
        }

        @Override
        public PossibleAddComponentCategoryI getCategory() {
            return PossibleAddCategoryEnum.BASE_COMPONENT;
        }
    }

    /**
     * Word prediction key to add
     */
    public static class AddWordPredictionKey extends AbstractAddKey {
        public static final AddWordPredictionKey INSTANCE = new AddWordPredictionKey();

        @Override
        public String getIconPath() {
            return "component/icon_add_word_key.png";
        }

        @Override
        public String getNameID() {
            return "component.prediction.key.name";
        }

        @Override
        public AddTypeEnum[] getAllowedAddType() {
            return new AddTypeEnum[]{AddTypeEnum.GRID_PART};
        }

        @Override
        public String getDescriptionID() {
            return "tooltip.add.key.word.auto";
        }

        @Override
        public ConfigurationProfileLevelEnum getMinimumLevel() {
            return ConfigurationProfileLevelEnum.BEGINNER;
        }

        @Override
        protected void configureKey(GridPartKeyComponentI key, GridComponentI parentGrid, GridPartKeyComponentI targetKey) {
            key.changeKeyOption(new WordPredictionKeyOption(), true);
        }
    }

    //    public static class AddKeyListKeyDisplayerKeyOption extends AbstractAddKey {
    //        public static final AddKeyListKeyDisplayerKeyOption INSTANCE = new AddKeyListKeyDisplayerKeyOption();
    //
    //        @Override
    //        public String getIconPath() {
    //            return "component/icon_add_key_keylist_key.png";
    //        }
    //
    //        @Override
    //        public String getNameID() {
    //            return "component.keylist.key.name";
    //        }
    //
    //        @Override
    //        public AddTypeEnum[] getAllowedAddType() {
    //            return new AddTypeEnum[]{AddTypeEnum.GRID_PART};
    //        }
    //
    //        @Override
    //        public String getDescriptionID() {
    //            return "component.keylist.key.description";
    //        }
    //
    //        @Override
    //        public ConfigurationProfileLevelEnum getMinimumLevel() {
    //            return ConfigurationProfileLevelEnum.BEGINNER;
    //        }
    //
    //        @Override
    //        protected void configureKey(GridPartKeyComponentI key, GridComponentI parentGrid, GridPartKeyComponentI targetKey) {
    //            key.changeKeyOption(new KeyListKeyDisplayKeyOption(), true);
    //        }
    //    }
    //
    //    public static class AddKeyListCategoryDisplayKeyOption extends AbstractAddKey {
    //        public static final AddKeyListKeyDisplayerKeyOption INSTANCE = new AddKeyListKeyDisplayerKeyOption();
    //
    //        @Override
    //        public String getIconPath() {
    //            return "component/icon_add_key_keylist_category.png";
    //        }
    //
    //        @Override
    //        public String getNameID() {
    //            return "component.keylist.category.name";
    //        }
    //
    //        @Override
    //        public AddTypeEnum[] getAllowedAddType() {
    //            return new AddTypeEnum[]{AddTypeEnum.GRID_PART};
    //        }
    //
    //        @Override
    //        public String getDescriptionID() {
    //            return "component.keylist.category.description";
    //        }
    //
    //        @Override
    //        public ConfigurationProfileLevelEnum getMinimumLevel() {
    //            return ConfigurationProfileLevelEnum.BEGINNER;
    //        }
    //
    //        @Override
    //        protected void configureKey(GridPartKeyComponentI key, GridComponentI parentGrid, GridPartKeyComponentI targetKey) {
    //            key.changeKeyOption(new KeyListCategoryDisplayKeyOption(), true);
    //        }
    //    }

    /**
     * Char prediction key
     */
    public static class AddCharPredictionKey extends AbstractAddKey {
        public static final AddCharPredictionKey INSTANCE = new AddCharPredictionKey();

        @Override
        public String getIconPath() {
            return "component/icon_add_auto_char_key.png";
        }

        @Override
        public String getNameID() {
            return "component.char.prediction.key.name";
        }

        @Override
        public AddTypeEnum[] getAllowedAddType() {
            return new AddTypeEnum[]{AddTypeEnum.GRID_PART};
        }

        @Override
        public String getDescriptionID() {
            return "tooltip.add.key.auto.char";
        }

        @Override
        public ConfigurationProfileLevelEnum getMinimumLevel() {
            return ConfigurationProfileLevelEnum.BEGINNER;
        }

        @Override
        protected void configureKey(GridPartKeyComponentI key, GridComponentI parentGrid, GridPartKeyComponentI targetKey) {
            key.changeKeyOption(new AutoCharKeyOption(), true);
        }
    }

    /**
     * Char key to add
     */
    public static class AddCharKey extends AbstractAddKey {
        public static final AddCharKey INSTANCE = new AddCharKey();

        @Override
        public String getIconPath() {
            return "component/icon_add_char_key.png";
        }

        @Override
        public String getNameID() {
            return "component.char.key.name";
        }

        @Override
        protected void configureKey(GridPartKeyComponentI key, GridComponentI parentGrid, GridPartKeyComponentI targetKey) {
            key.changeKeyOption(new CustomCharKeyOption(), true);
            // Try to find the missing char
            if (parentGrid != null) {
                String nextChar = CharKeyOptionHelper.getNextCharFor(parentGrid);
                if (nextChar != null) {
                    key.textContentProperty().set("" + nextChar);
                }
            }
        }

        @Override
        public AddTypeEnum[] getAllowedAddType() {
            return new AddTypeEnum[]{AddTypeEnum.GRID_PART};
        }

        @Override
        public String getDescriptionID() {
            return "tooltip.add.key.char";
        }

        @Override
        public ConfigurationProfileLevelEnum getMinimumLevel() {
            return ConfigurationProfileLevelEnum.EXPERT;
        }

    }

    /**
     * Classic key to add
     */
    public static class AddBasicKey extends AbstractAddKey {
        public static final AddBasicKey INSTANCE = new AddBasicKey();

        @Override
        public String getIconPath() {
            return "component/icon_add_basic_key.png";
        }

        @Override
        public String getNameID() {
            return "component.basic.key.name";
        }

        @Override
        protected void configureKey(GridPartKeyComponentI key, GridComponentI parentGrid, GridPartKeyComponentI targetKey) {
            key.changeKeyOption(new BasicKeyOption(), true);
        }

        @Override
        public AddTypeEnum[] getAllowedAddType() {
            return new AddTypeEnum[]{AddTypeEnum.GRID_PART};
        }

        @Override
        public String getDescriptionID() {
            return "tooltip.add.key.basic";
        }

        @Override
        public ConfigurationProfileLevelEnum getMinimumLevel() {
            return ConfigurationProfileLevelEnum.NORMAL;
        }

    }

    /**
     * Quick communication key
     */
    public static class AddQuickCommunicationKey extends AbstractAddKey {
        public final static AddQuickCommunicationKey INSTANCE = new AddQuickCommunicationKey();

        @Override
        public String getIconPath() {
            return "component/icon_add_com_key.png";
        }

        @Override
        public String getNameID() {
            return "component.quick.communication.key.name";
        }

        @Override
        public AddTypeEnum[] getAllowedAddType() {
            return new AddTypeEnum[]{AddTypeEnum.GRID_PART};
        }

        @Override
        public String getDescriptionID() {
            return "tooltip.quick.communication.key.name";
        }

        @Override
        public ConfigurationProfileLevelEnum getMinimumLevel() {
            return ConfigurationProfileLevelEnum.BEGINNER;
        }

        @Override
        protected void configureKey(GridPartKeyComponentI key, GridComponentI parentGrid, GridPartKeyComponentI targetKey) {
            key.changeKeyOption(new QuickComKeyOption(), true);
        }
    }

    /**
     * Variable information
     */
    public static class AddVariableInformationKey extends AbstractAddKey {
        public static final AddVariableInformationKey INSTANCE = new AddVariableInformationKey();

        @Override
        public String getIconPath() {
            return "component/icon_info_variables_key.png";
        }

        @Override
        public String getNameID() {
            return "component.variable.information.key.name";
        }

        @Override
        protected void configureKey(GridPartKeyComponentI key, GridComponentI parentGrid, GridPartKeyComponentI targetKey) {
            key.changeKeyOption(new VariableInformationKeyOption(), true);
        }

        @Override
        public AddTypeEnum[] getAllowedAddType() {
            return new AddTypeEnum[]{AddTypeEnum.GRID_PART};
        }

        @Override
        public String getDescriptionID() {
            return "tooltip.add.key.variable.information";
        }

        @Override
        public ConfigurationProfileLevelEnum getMinimumLevel() {
            return ConfigurationProfileLevelEnum.EXPERT;
        }

    }

    /**
     * Note key
     */
    public static class AddNoteKey extends AbstractAddKey {
        public static final AddNoteKey INSTANCE = new AddNoteKey();

        @Override
        public String getIconPath() {
            return "component/icon_add_note_key.png";
        }

        @Override
        public String getNameID() {
            return "component.variable.note.key.name";
        }

        @Override
        protected void configureKey(GridPartKeyComponentI key, GridComponentI parentGrid, GridPartKeyComponentI targetKey) {
            key.changeKeyOption(new NoteKeyOption(), true);
        }

        @Override
        public AddTypeEnum[] getAllowedAddType() {
            return new AddTypeEnum[]{AddTypeEnum.GRID_PART};
        }

        @Override
        public String getDescriptionID() {
            return "tooltip.add.key.notekey";
        }

        @Override
        public ConfigurationProfileLevelEnum getMinimumLevel() {
            return ConfigurationProfileLevelEnum.EXPERT;
        }

    }
    // ========================================================================
}
