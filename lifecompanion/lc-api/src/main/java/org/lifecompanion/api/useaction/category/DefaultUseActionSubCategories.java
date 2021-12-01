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

package org.lifecompanion.api.useaction.category;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.api.component.definition.useaction.BaseUseActionI;
import org.lifecompanion.api.component.definition.useaction.UseActionMainCategoryI;
import org.lifecompanion.api.component.definition.useaction.UseActionSubCategoryI;
import org.lifecompanion.framework.commons.translation.Translation;

/**
 * List of all available action sub categories.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum DefaultUseActionSubCategories implements UseActionSubCategoryI {
    // Class part : "Category definition"
    //========================================================================
    //Write
    WRITE_TEXT("use.action.sub.category.write.text.name", DefaultUseActionMainCategories.TEXT,
            CategoryColors.nextColor(DefaultUseActionMainCategories.TEXT)), //
    DELETE_TEXT("use.action.sub.category.delete.text.name", DefaultUseActionMainCategories.TEXT,
            CategoryColors.nextColor(DefaultUseActionMainCategories.TEXT)), //
    UPPERCASE("use.action.sub.category.text.uppercase.name", DefaultUseActionMainCategories.TEXT,
            CategoryColors.nextColor(DefaultUseActionMainCategories.TEXT)), //
    SPECIAL_CHAR("use.action.sub.category.text.specialchar", DefaultUseActionMainCategories.TEXT,
            CategoryColors.nextColor(DefaultUseActionMainCategories.TEXT)), //
    CARET("use.action.sub.category.caret.text.name", DefaultUseActionMainCategories.TEXT,
            CategoryColors.nextColor(DefaultUseActionMainCategories.TEXT)), //
    PREDICTION("use.action.sub.category.prediction.name", DefaultUseActionMainCategories.TEXT,
            CategoryColors.nextColor(DefaultUseActionMainCategories.TEXT)), //

    //Show
    MOVE_TO_SIMPLE("use.action.sub.category.move.to.simple.component.name", DefaultUseActionMainCategories.SHOW,
            CategoryColors.nextColor(DefaultUseActionMainCategories.SHOW)), //
    MOVE_TO_COMPLEX("use.action.sub.category.move.to.complex.component.name", DefaultUseActionMainCategories.SHOW,
            CategoryColors.nextColor(DefaultUseActionMainCategories.SHOW)), //
    CHANGE_PAGE("use.action.sub.category.change.page.name", DefaultUseActionMainCategories.SHOW,
            CategoryColors.nextColor(DefaultUseActionMainCategories.SHOW)), //

    //Key list
    KEY_LIST_CURRENT("use.action.sub.category.key.list.current.name", DefaultUseActionMainCategories.KEY_LIST,
            CategoryColors.nextColor(DefaultUseActionMainCategories.KEY_LIST)), //
    KEY_LIST_GENERAL("use.action.sub.category.key.list.general.name", DefaultUseActionMainCategories.KEY_LIST,
            CategoryColors.nextColor(DefaultUseActionMainCategories.KEY_LIST)), //
    KEY_LIST_SELECTED("use.action.sub.category.key.list.selected.name", DefaultUseActionMainCategories.KEY_LIST,
            CategoryColors.nextColor(DefaultUseActionMainCategories.KEY_LIST)), //

    // Sequences
    UA_SEQUENCE_GENERAL("use.action.sub.category.user.action.sequence.sub.general", DefaultUseActionMainCategories.USER_ACTION_SEQUENCE,
            CategoryColors.nextColor(DefaultUseActionMainCategories.USER_ACTION_SEQUENCE)),//
    UA_SEQUENCE_CURRENT("use.action.sub.category.user.action.sequence.sub.current", DefaultUseActionMainCategories.USER_ACTION_SEQUENCE,
            CategoryColors.nextColor(DefaultUseActionMainCategories.USER_ACTION_SEQUENCE)),//


    //Selection
    SCANNING_MODE("use.action.sub.category.selection.scanning.mode.name", DefaultUseActionMainCategories.SELECTION,
            CategoryColors.nextColor(DefaultUseActionMainCategories.SELECTION)), //
    KEYS("use.action.sub.category.selection.keys.name", DefaultUseActionMainCategories.SELECTION,
            CategoryColors.nextColor(DefaultUseActionMainCategories.SELECTION)), //

    //Speak
    SPEAK_TEXT("use.action.sub.category.speak.text.name", DefaultUseActionMainCategories.SPEAK,
            CategoryColors.nextColor(DefaultUseActionMainCategories.SPEAK)), //
    SPEAK_PARAMETERS("use.action.sub.category.speak.parameters.name", DefaultUseActionMainCategories.SPEAK,
            CategoryColors.nextColor(DefaultUseActionMainCategories.SPEAK)), //

    //Configuration
    CHANGE("use.action.sub.category.configuration.change.name", DefaultUseActionMainCategories.CONFIGURATION,
            CategoryColors.nextColor(DefaultUseActionMainCategories.CONFIGURATION)), //
    FRAME("use.action.sub.category.configuration.frame.name", DefaultUseActionMainCategories.CONFIGURATION,
            CategoryColors.nextColor(DefaultUseActionMainCategories.CONFIGURATION)), //

    //Computer access
    COMPUTER_FEATURES("use.action.sub.category.computer.access.computer.features.name", DefaultUseActionMainCategories.COMPUTER_ACCESS,
            CategoryColors.nextColor(DefaultUseActionMainCategories.COMPUTER_ACCESS)), //
    KEYBOARD("use.action.sub.category.computer.access.keyboard.name", DefaultUseActionMainCategories.COMPUTER_ACCESS,
            CategoryColors.nextColor(DefaultUseActionMainCategories.COMPUTER_ACCESS)), //
    MOUSE_ACTION("use.action.sub.category.computer.access.mouse.actions.name", DefaultUseActionMainCategories.COMPUTER_ACCESS,
            CategoryColors.nextColor(DefaultUseActionMainCategories.COMPUTER_ACCESS)), //
    MOUSE_MOVE("use.action.sub.category.computer.access.mouse.move.name", DefaultUseActionMainCategories.COMPUTER_ACCESS,
            CategoryColors.nextColor(DefaultUseActionMainCategories.COMPUTER_ACCESS)), //

    //Media
    SOUND("use.action.sub.category.sound.name", DefaultUseActionMainCategories.MEDIA, CategoryColors.nextColor(DefaultUseActionMainCategories.MEDIA)), //

    //App
    NOTEPAD("use.action.sub.category.notepad.name", DefaultUseActionMainCategories.APP, CategoryColors.nextColor(DefaultUseActionMainCategories.APP)), //

    // MISCELLANEOUS
    APPLICATION("use.action.sub.category.application.name", DefaultUseActionMainCategories.MISCELLANEOUS,
            CategoryColors.nextColor(DefaultUseActionMainCategories.MISCELLANEOUS)),//
    NOTE("use.action.sub.category.note.name", DefaultUseActionMainCategories.MISCELLANEOUS,
            CategoryColors.nextColor(DefaultUseActionMainCategories.MISCELLANEOUS)),//
    SCRIPT("use.action.sub.category.script.name", DefaultUseActionMainCategories.MISCELLANEOUS,
            CategoryColors.nextColor(DefaultUseActionMainCategories.MISCELLANEOUS))//

    ;
    //========================================================================

    // Class part : "Class part"
    //========================================================================
    private final String nameID;
    private final String id;
    private final UseActionMainCategoryI mainCategory;
    private final Color color;
    private final ObservableList<BaseUseActionI<?>> actions;

    DefaultUseActionSubCategories(final String nameIDP, final UseActionMainCategoryI mainCategoryP, final Color colorP) {
        this.nameID = nameIDP;
        this.id = this.name();
        this.mainCategory = mainCategoryP;
        this.color = colorP;
        this.actions = FXCollections.observableArrayList();
        mainCategoryP.getSubCategories().add(this);
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public String generateID() {
        return this.id;
    }

    @Override
    public String getName() {
        return Translation.getText(this.nameID);
    }

    @Override
    public UseActionMainCategoryI getMainCategory() {
        return this.mainCategory;
    }

    @Override
    public ObservableList<BaseUseActionI<?>> getContent() {
        return this.actions;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int order() {
        return this.ordinal();
    }
    //========================================================================

}
