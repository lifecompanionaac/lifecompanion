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

package org.lifecompanion.controller.io;

import org.lifecompanion.framework.utils.FluentHashMap;

/**
 * Store type from previous LC versions to ensure backward compatibility
 *
 * @author Mathieu THEBAUD
 */
public class LCBackwardCompatibility {

    private static final FluentHashMap<String, String> PREVIOUS_TYPE_CORRESPONDANCES = FluentHashMap
            // Components
            .map("fr.forusoftware.lifecompanion.data.component.simple.StackComponent", "org.lifecompanion.model.impl.configurationcomponent.StackComponent")
            .with("fr.forusoftware.lifecompanion.data.component.simple.ComponentGrid", "org.lifecompanion.model.impl.configurationcomponent.ComponentGrid")
            .with("fr.forusoftware.lifecompanion.data.component.simple.GridPartGridComponent", "org.lifecompanion.model.impl.configurationcomponent.GridPartGridComponent")
            .with("fr.forusoftware.lifecompanion.data.component.simple.GridPartKeyComponent", "org.lifecompanion.model.impl.configurationcomponent.GridPartKeyComponent")
            .with("fr.forusoftware.lifecompanion.data.component.simple.GridPartStackComponent", "org.lifecompanion.model.impl.configurationcomponent.GridPartStackComponent")
            .with("fr.forusoftware.lifecompanion.data.component.simple.GridPartTextEditorComponent", "org.lifecompanion.model.impl.configurationcomponent.GridPartTextEditorComponent")
            .with("fr.forusoftware.lifecompanion.data.component.simple.WriterEntry", "org.lifecompanion.model.impl.configurationcomponent.WriterEntry")
            .with("fr.forusoftware.lifecompanion.data.component.simple.TextEditorComponent", "org.lifecompanion.model.impl.configurationcomponent.TextEditorComponent")
            .with("fr.forusoftware.lifecompanion.data.component.simple.PredictionParameter", "org.lifecompanion.model.impl.configurationcomponent.PredictionParameter")
            .with("fr.forusoftware.lifecompanion.data.component.simple.StackComponent", "org.lifecompanion.model.impl.configurationcomponent.StackComponent")
            .with("fr.forusoftware.lifecompanion.data.component.simple.ComponentSpan", "org.lifecompanion.model.impl.configurationcomponent.ComponentSpan")
            // Config
            .with("fr.forusoftware.lifecompanion.data.component.simple.VirtualMouseParameter", "org.lifecompanion.model.impl.configurationcomponent.VirtualMouseParameter")
            // Selection modes
            .with("fr.forusoftware.lifecompanion.data.definition.selection.impl.AutoActivationSelectionMode", "org.lifecompanion.model.impl.selectionmode.AutoActivationSelectionMode")
            .with("fr.forusoftware.lifecompanion.data.definition.selection.impl.ColumnRowScanSelectionMode", "org.lifecompanion.model.impl.selectionmode.ColumnRowScanSelectionMode")
            .with("fr.forusoftware.lifecompanion.data.definition.selection.impl.DirectActivationSelectionMode", "org.lifecompanion.model.impl.selectionmode.DirectActivationSelectionMode")
            .with("fr.forusoftware.lifecompanion.data.definition.selection.impl.HorizontalDirectKeyScanSelectionMode", "org.lifecompanion.model.impl.selectionmode.HorizontalDirectKeyScanSelectionMode")
            .with("fr.forusoftware.lifecompanion.data.definition.selection.impl.VerticalDirectKeyScanSelectionMode", "org.lifecompanion.model.impl.selectionmode.VerticalDirectKeyScanSelectionMode")
            .with("fr.forusoftware.lifecompanion.data.definition.selection.impl.RowColumnScanSelectionMode", "org.lifecompanion.model.impl.selectionmode.RowColumnScanSelectionMode")
            // Key options
            .with("fr.forusoftware.lifecompanion.data.component.keyoption.AutoCharKeyOption", "org.lifecompanion.model.impl.configurationcomponent.keyoption.AutoCharKeyOption")
            .with("fr.forusoftware.lifecompanion.data.component.keyoption.BasicKeyOption", "org.lifecompanion.model.impl.configurationcomponent.keyoption.BasicKeyOption")
            .with("fr.forusoftware.lifecompanion.data.component.keyoption.CustomCharKeyOption", "org.lifecompanion.model.impl.configurationcomponent.keyoption.CustomCharKeyOption")
            .with("fr.forusoftware.lifecompanion.data.component.keyoption.NoteKeyOption", "org.lifecompanion.model.impl.configurationcomponent.keyoption.note.NoteKeyOption")
            .with("fr.forusoftware.lifecompanion.data.component.keyoption.QuickComKeyOption", "org.lifecompanion.model.impl.configurationcomponent.keyoption.QuickComKeyOption")
            .with("fr.forusoftware.lifecompanion.data.component.keyoption.VariableInformationKeyOption", "org.lifecompanion.model.impl.configurationcomponent.keyoption.VariableInformationKeyOption")
            .with("fr.forusoftware.lifecompanion.data.component.keyoption.WordPredictionKeyOption", "org.lifecompanion.model.impl.configurationcomponent.keyoption.WordPredictionKeyOption");

    public static String getBackwardCompatibleType(String type) {
        // Actions
        if (type.startsWith("fr.forusoftware.lifecompanion.data.useaction.impl")) {
            return type.replace("fr.forusoftware.lifecompanion.data.useaction.impl", "org.lifecompanion.base.data.useaction.impl");
        }
        // Events
        if (type.startsWith("fr.forusoftware.lifecompanion.data.useevent.impl")) {
            return type.replace("fr.forusoftware.lifecompanion.data.useevent.impl", "org.lifecompanion.base.data.useevent.impl");
        }
        return PREVIOUS_TYPE_CORRESPONDANCES.getOrDefault(type, type);
    }
}
