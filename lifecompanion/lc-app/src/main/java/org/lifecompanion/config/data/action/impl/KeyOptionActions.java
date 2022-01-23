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

package org.lifecompanion.config.data.action.impl;

import javafx.scene.paint.Color;
import org.lifecompanion.base.data.action.definition.BasePropertyChangeAction;
import org.lifecompanion.base.data.component.keyoption.ProgressDisplayKeyOption;
import org.lifecompanion.base.data.component.keyoption.QuickComKeyOption;
import org.lifecompanion.base.data.component.keyoption.VariableInformationKeyOption;
import org.lifecompanion.base.data.component.keyoption.WordPredictionKeyOption;
import org.lifecompanion.base.data.component.keyoption.note.NoteKeyDisplayMode;
import org.lifecompanion.base.data.component.keyoption.note.NoteKeyOption;
import org.lifecompanion.base.data.component.keyoption.simplercomp.KeyListNodeKeyOption;
import org.lifecompanion.base.data.component.keyoption.simplercomp.UserActionSequenceDisplayFilter;
import org.lifecompanion.base.data.component.keyoption.simplercomp.UserActionSequenceItemKeyOption;

/**
 * All actions relative to key options.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class KeyOptionActions {

    public static class ChangeWordPredictionAddSpace extends BasePropertyChangeAction<Boolean> {

        public ChangeWordPredictionAddSpace(final WordPredictionKeyOption option, final Boolean wantedValueP) {
            super(option.addSpaceProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.key.option.change.add.space";
        }

    }

    public static class ChangeWordPredictionCorrectionColor extends BasePropertyChangeAction<Color> {

        public ChangeWordPredictionCorrectionColor(final WordPredictionKeyOption option, final Color wantedValueP) {
            super(option.correctionColorProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.key.option.change.correction.color";
        }

    }

    public static class ChangeUserActionSequenceDisplayFilter extends BasePropertyChangeAction<UserActionSequenceDisplayFilter> {

        public ChangeUserActionSequenceDisplayFilter(final UserActionSequenceItemKeyOption option, final UserActionSequenceDisplayFilter wantedValueP) {
            super(option.displayFilterProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "TODO";
        }//FIXME

    }

    public static class ChangeTextToWriteAction extends BasePropertyChangeAction<String> {

        public ChangeTextToWriteAction(final QuickComKeyOption option, final String oldValueP, final String wantedValueP) {
            super(option.textToWriteProperty(), oldValueP, wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.key.option.quick.com.change.text.to.write";
        }

    }

    public static class ChangeTextToSpeakAction extends BasePropertyChangeAction<String> {

        public ChangeTextToSpeakAction(final QuickComKeyOption option, final String oldValueP, final String wantedValueP) {
            super(option.textToSpeakProperty(), oldValueP, wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.key.option.quick.com.change.text.to.speak";
        }

    }

    public static class ChangeEnableSpeakAction extends BasePropertyChangeAction<Boolean> {

        public ChangeEnableSpeakAction(final QuickComKeyOption option, final Boolean wantedValueP) {
            super(option.enableSpeakProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.key.option.quick.com.change.enable.speak";
        }

    }

    public static class ChangeQuickComAddSpaceAction extends BasePropertyChangeAction<Boolean> {

        public ChangeQuickComAddSpaceAction(final QuickComKeyOption option, final Boolean wantedValueP) {
            super(option.addSpaceProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.key.option.quick.com.change.add.space";
        }

    }

    public static class ChangeInformationToDisplayAction extends BasePropertyChangeAction<String> {

        public ChangeInformationToDisplayAction(final VariableInformationKeyOption option, final String oldValueP, final String wantedValueP) {
            super(option.wantedDisplayedInformationProperty(), oldValueP, wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.key.option.variable.information.change.text";
        }

    }

    public static class ChangeNoteKeyStrokeSizeAction extends BasePropertyChangeAction<Number> {

        public ChangeNoteKeyStrokeSizeAction(final NoteKeyOption option, final Number wantedValueP) {
            super(option.wantedStrokeSizeProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.name.change.notekey.stroke.size";
        }

    }

    public static class ChangeNoteKeyStrokeColorAction extends BasePropertyChangeAction<Color> {

        public ChangeNoteKeyStrokeColorAction(final NoteKeyOption option, final Color wantedValueP) {
            super(option.wantedActivatedColorProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.name.change.notekey.stroke.color";
        }

    }

    public static class ChangeProgressDisplayColorAction extends BasePropertyChangeAction<Color> {

        public ChangeProgressDisplayColorAction(final ProgressDisplayKeyOption option, final Color wantedValueP) {
            super(option.progressColorProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "todo";
        }
    }

    public static class ChangeProgressDisplayModeAction extends BasePropertyChangeAction<ProgressDisplayKeyOption.ProgressDisplayMode> {

        public ChangeProgressDisplayModeAction(final ProgressDisplayKeyOption option, final ProgressDisplayKeyOption.ProgressDisplayMode wantedValueP) {
            super(option.progressDisplayModeProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "todo";
        }
    }

    public static class ChangeProgressDisplayTypeAction extends BasePropertyChangeAction<ProgressDisplayKeyOption.ProgressDisplayType> {

        public ChangeProgressDisplayTypeAction(final ProgressDisplayKeyOption option, final ProgressDisplayKeyOption.ProgressDisplayType wantedValueP) {
            super(option.progressDisplayTypeProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "todo";
        }
    }


    public static class ChangeNoteKeyDisplayModeAction extends BasePropertyChangeAction<NoteKeyDisplayMode> {

        public ChangeNoteKeyDisplayModeAction(final NoteKeyOption option, final NoteKeyDisplayMode wantedValueP) {
            super(option.displayModeProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.name.change.notekey.display.mode";
        }

    }

    public static class ChangeNoteCustomTextAction extends BasePropertyChangeAction<String> {

        public ChangeNoteCustomTextAction(final NoteKeyOption option, final String oldValueP, final String wantedValueP) {
            super(option.keyCustomTextProperty(), oldValueP, wantedValueP);
        }

        @Override
        public String getNameID() {
            return "action.name.change.notekey.custom.text";
        }

    }

    public static class ChangeKeyListOptionSelectedLevelAction extends BasePropertyChangeAction<Number> {

        public ChangeKeyListOptionSelectedLevelAction(final KeyListNodeKeyOption option, final Number wantedValueP) {
            super(option.selectedLevelProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "keylist.action.change.keylist.selected.level.name";
        }

    }

    public static class ChangeKeyListOptionSpecificLevelAction extends BasePropertyChangeAction<Boolean> {

        public ChangeKeyListOptionSpecificLevelAction(final KeyListNodeKeyOption option, final Boolean wantedValueP) {
            super(option.specificLevelProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "keylist.action.change.keylist.toggle.specific.level.name";
        }
    }

    public static class ChangeKeyListOptionDisplayLevelBellowAction extends BasePropertyChangeAction<Boolean> {

        public ChangeKeyListOptionDisplayLevelBellowAction(final KeyListNodeKeyOption option, final Boolean wantedValueP) {
            super(option.displayLevelBellowProperty(), wantedValueP);
        }

        @Override
        public String getNameID() {
            return "keylist.action.change.keylist.toggle.level.bellow.name";
        }
    }
}
