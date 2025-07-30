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

package org.lifecompanion.model.api.categorizedelement.useaction;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementColorProvider;
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
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.TEXT)), //
    DELETE_TEXT("use.action.sub.category.delete.text.name", DefaultUseActionMainCategories.TEXT,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.TEXT)), //
    UPPERCASE("use.action.sub.category.text.uppercase.name", DefaultUseActionMainCategories.TEXT,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.TEXT)), //
    SPECIAL_CHAR("use.action.sub.category.text.specialchar", DefaultUseActionMainCategories.TEXT,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.TEXT)), //
    CARET("use.action.sub.category.caret.text.name", DefaultUseActionMainCategories.TEXT,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.TEXT)), //
    PREDICTION("use.action.sub.category.prediction.name", DefaultUseActionMainCategories.TEXT,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.TEXT)), //

    //Show
    MOVE_TO_SIMPLE("use.action.sub.category.move.to.simple.component.name", DefaultUseActionMainCategories.SHOW,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.SHOW)), //
    MOVE_TO_COMPLEX("use.action.sub.category.move.to.complex.component.name", DefaultUseActionMainCategories.SHOW,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.SHOW)), //
    CHANGE_PAGE("use.action.sub.category.change.page.name", DefaultUseActionMainCategories.SHOW,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.SHOW)), //

    //Key list
    KEY_LIST_CURRENT("use.action.sub.category.key.list.current.name", DefaultUseActionMainCategories.KEY_LIST,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.KEY_LIST)), //
    KEY_LIST_GENERAL("use.action.sub.category.key.list.general.name", DefaultUseActionMainCategories.KEY_LIST,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.KEY_LIST)), //
    KEY_LIST_SELECTED("use.action.sub.category.key.list.selected.name", DefaultUseActionMainCategories.KEY_LIST,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.KEY_LIST)), //

    // Sequences
    UA_SEQUENCE_GENERAL("use.action.sub.category.user.action.sequence.sub.general", DefaultUseActionMainCategories.USER_ACTION_SEQUENCE,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.USER_ACTION_SEQUENCE)),//
    UA_SEQUENCE_CURRENT("use.action.sub.category.user.action.sequence.sub.current", DefaultUseActionMainCategories.USER_ACTION_SEQUENCE,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.USER_ACTION_SEQUENCE)),//
    UA_SEQUENCE_LIST("use.action.sub.category.user.action.sequence.sub.list", DefaultUseActionMainCategories.USER_ACTION_SEQUENCE,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.USER_ACTION_SEQUENCE)),//


    //Selection
    SELECTION_MODE_GENERAL("use.action.sub.category.selection.mode.general.name", DefaultUseActionMainCategories.SELECTION,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.SELECTION)), //
    SCANNING_MODE("use.action.sub.category.selection.scanning.mode.name", DefaultUseActionMainCategories.SELECTION,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.SELECTION)), //
    KEYS("use.action.sub.category.selection.keys.name", DefaultUseActionMainCategories.SELECTION,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.SELECTION)), //

    //Speak
    SPEAK_TEXT("use.action.sub.category.speak.text.name", DefaultUseActionMainCategories.SPEAK,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.SPEAK)), //
    SPELL_TEXT("use.action.sub.category.spell.text.name", DefaultUseActionMainCategories.SPEAK,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.SPEAK)), //
    SPEAK_PARAMETERS("use.action.sub.category.speak.parameters.name", DefaultUseActionMainCategories.SPEAK,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.SPEAK)), //

    //Configuration
    CHANGE("use.action.sub.category.configuration.change.name", DefaultUseActionMainCategories.CONFIGURATION,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.CONFIGURATION)), //
    FRAME("use.action.sub.category.configuration.frame.name", DefaultUseActionMainCategories.CONFIGURATION,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.CONFIGURATION)),//
    DYNAMIC_KEYS("use.action.sub.category.configuration.dynamic.key", DefaultUseActionMainCategories.CONFIGURATION,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.CONFIGURATION)), //
    CONFIG_LIST("use.action.sub.category.configuration.config.list.name", DefaultUseActionMainCategories.CONFIGURATION,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.CONFIGURATION)),//

    //Computer access
    COMPUTER_FEATURES("use.action.sub.category.computer.access.computer.features.name", DefaultUseActionMainCategories.COMPUTER_ACCESS,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.COMPUTER_ACCESS)), //
    KEYBOARD("use.action.sub.category.computer.access.keyboard.name", DefaultUseActionMainCategories.COMPUTER_ACCESS,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.COMPUTER_ACCESS)), //
    MOUSE_ACTION("use.action.sub.category.computer.access.mouse.actions.name", DefaultUseActionMainCategories.COMPUTER_ACCESS,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.COMPUTER_ACCESS)), //
    MOUSE_MOVE("use.action.sub.category.computer.access.mouse.move.name", DefaultUseActionMainCategories.COMPUTER_ACCESS,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.COMPUTER_ACCESS)), //
    MOUSE_ACTION_DIRECT("use.action.sub.category.computer.access.mouse.direct.actions.name", DefaultUseActionMainCategories.COMPUTER_ACCESS,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.COMPUTER_ACCESS)), //
    // TODO : rename
    //    CURSOR_STRIP("use.action.sub.category.computer.access.cursor.strip.actions.name", DefaultUseActionMainCategories.COMPUTER_ACCESS,
    //            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.COMPUTER_ACCESS)), //

    //Media
    SOUND("use.action.sub.category.sound.name", DefaultUseActionMainCategories.MEDIA, CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.MEDIA)), //

    //App
    NOTEPAD("use.action.sub.category.notepad.name", DefaultUseActionMainCategories.APP, CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.APP)), //

    // MISCELLANEOUS
    APPLICATION("use.action.sub.category.application.name", DefaultUseActionMainCategories.MISCELLANEOUS,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.MISCELLANEOUS)),//
    NOTE("use.action.sub.category.note.name", DefaultUseActionMainCategories.MISCELLANEOUS,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.MISCELLANEOUS)),//
    WHITEBOARD("use.action.sub.category.whiteboard.name", DefaultUseActionMainCategories.MISCELLANEOUS,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.MISCELLANEOUS)),//
    GAMING_FRAMEWORK("use.action.sub.category.gaming.framework.name", DefaultUseActionMainCategories.MISCELLANEOUS,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.MISCELLANEOUS)),//
    SCRIPT("use.action.sub.category.script.name", DefaultUseActionMainCategories.MISCELLANEOUS,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.MISCELLANEOUS)),//
    TIMER("use.action.sub.category.timer.name", DefaultUseActionMainCategories.MISCELLANEOUS,
            CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.MISCELLANEOUS)),//

    //Mobile
    MOBILE("use.action.sub.category.mobile.name", DefaultUseActionMainCategories.MOBILE, CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.MOBILE)), //

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
