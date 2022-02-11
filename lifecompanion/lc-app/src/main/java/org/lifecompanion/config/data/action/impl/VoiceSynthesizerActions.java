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

import org.lifecompanion.model.impl.editaction.BasePropertyChangeAction;
import org.lifecompanion.model.api.editaction.UndoRedoActionI;
import org.lifecompanion.model.api.configurationcomponent.VoiceSynthesizerUserI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.voicesynthesizer.PronunciationExceptionI;
import org.lifecompanion.model.api.voicesynthesizer.VoiceInfoI;
import org.lifecompanion.model.api.voicesynthesizer.VoiceSynthesizerI;
import org.lifecompanion.model.api.voicesynthesizer.VoiceSynthesizerParameterI;

/**
 * All actions relative to voice synthesizer.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */

// TODO : REMOVE THESE ACTIONS !
public class VoiceSynthesizerActions {

	public static class ChangeVoiceSynthesizerAction extends BasePropertyChangeAction<VoiceSynthesizerI> {

		public ChangeVoiceSynthesizerAction(final VoiceSynthesizerParameterI parameter, final VoiceSynthesizerI wantedValue) {
			super(parameter.selectedVoiceSynthesizerProperty(), wantedValue);
		}

		@Override
		public String getNameID() {
			return "action.voice.synthesizer.set.synthesizer";
		}

	}

	public static class ChangeSelectedVoiceAction extends BasePropertyChangeAction<VoiceInfoI> {

		public ChangeSelectedVoiceAction(final VoiceSynthesizerParameterI parameter, final VoiceInfoI wantedValue) {
			super(parameter.getVoiceParameter().selectedVoiceInfoProperty(), wantedValue);
		}

		@Override
		public String getNameID() {
			return "action.voice.synthesizer.set.synthesizer.voice";
		}

	}

	public static class ChangeVolumeAction extends BasePropertyChangeAction<Number> {

		public ChangeVolumeAction(final VoiceSynthesizerParameterI parameter, final Number wantedValue) {
			super(parameter.volumeProperty(), wantedValue);
		}

		@Override
		public String getNameID() {
			return "action.voice.synthesizer.set.volume";
		}
	}

	public static class ChangeRateAction extends BasePropertyChangeAction<Number> {

		public ChangeRateAction(final VoiceSynthesizerParameterI parameter, final Number wantedValue) {
			super(parameter.rateProperty(), wantedValue);
		}

		@Override
		public String getNameID() {
			return "action.voice.synthesizer.set.rate";
		}

	}

	public static class ChangePitchAction extends BasePropertyChangeAction<Number> {

		public ChangePitchAction(final VoiceSynthesizerParameterI parameter, final Number wantedValue) {
			super(parameter.pitchProperty(), wantedValue);
		}

		@Override
		public String getNameID() {
			return "action.voice.synthesizer.set.pitch";
		}

	}

	public static class AddPronunciationExceptionAction implements UndoRedoActionI {
		private PronunciationExceptionI added;
		private VoiceSynthesizerParameterI parameter;

		public AddPronunciationExceptionAction(final VoiceSynthesizerUserI voiceSynthesizerUser, final PronunciationExceptionI added) {
			this.parameter = voiceSynthesizerUser.getVoiceSynthesizerParameter();
			this.added = added;
		}

		@Override
		public void doAction() throws LCException {
			this.parameter.getPronunciationExceptions().add(this.added);
		}

		@Override
		public String getNameID() {
			return "action.voice.synthesizer.add.pronunciation.exception";
		}

		@Override
		public void undoAction() throws LCException {
			this.parameter.getPronunciationExceptions().remove(this.added);
		}

		@Override
		public void redoAction() throws LCException {
			this.doAction();
		}

	}

	public static class RemovePronunciationExceptionAction implements UndoRedoActionI {
		private PronunciationExceptionI removed;
		private VoiceSynthesizerParameterI parameter;

		public RemovePronunciationExceptionAction(final VoiceSynthesizerUserI voiceSynthesizerUser, final PronunciationExceptionI removed) {
			this.parameter = voiceSynthesizerUser.getVoiceSynthesizerParameter();
			this.removed = removed;
		}

		@Override
		public void doAction() throws LCException {
			this.parameter.getPronunciationExceptions().remove(this.removed);
		}

		@Override
		public String getNameID() {
			return "action.voice.synthesizer.remove.pronunciation.exception";
		}

		@Override
		public void undoAction() throws LCException {
			this.parameter.getPronunciationExceptions().add(this.removed);
		}

		@Override
		public void redoAction() throws LCException {
			this.doAction();
		}

	}

	public static class ChangeOriginalPronunciationAction extends BasePropertyChangeAction<String> {

		public ChangeOriginalPronunciationAction(final PronunciationExceptionI exception, final String wantedValueP) {
			super(exception.originalTextProperty(), wantedValueP);
		}

		@Override
		public String getNameID() {
			return "action.voice.synthesizer.modify.pronunciation.exception.original";
		}

	}

	public static class ChangeReplacePronunciationAction extends BasePropertyChangeAction<String> {

		public ChangeReplacePronunciationAction(final PronunciationExceptionI exception, final String wantedValueP) {
			super(exception.replaceTextProperty(), wantedValueP);
		}

		@Override
		public String getNameID() {
			return "action.voice.synthesizer.modify.pronunciation.exception.replace";
		}

	}

}
