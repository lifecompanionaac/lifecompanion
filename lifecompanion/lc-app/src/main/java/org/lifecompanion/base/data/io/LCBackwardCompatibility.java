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

package org.lifecompanion.base.data.io;

import org.lifecompanion.framework.utils.FluentHashMap;

/**
 * Store type from previous LC versions to ensure backward compatibility
 *
 * @author Mathieu THEBAUD
 */
public class LCBackwardCompatibility {

    private static final FluentHashMap<String, String> PREVIOUS_TYPE_CORRESPONDANCES = FluentHashMap
            // Components
            .map("fr.forusoftware.lifecompanion.data.component.simple.StackComponent", "org.lifecompanion.base.data.component.simple.StackComponent")
            .with("fr.forusoftware.lifecompanion.data.component.simple.ComponentGrid", "org.lifecompanion.base.data.component.simple.ComponentGrid")
            .with("fr.forusoftware.lifecompanion.data.component.simple.GridPartGridComponent", "org.lifecompanion.base.data.component.simple.GridPartGridComponent")
            .with("fr.forusoftware.lifecompanion.data.component.simple.GridPartKeyComponent", "org.lifecompanion.base.data.component.simple.GridPartKeyComponent")
            .with("fr.forusoftware.lifecompanion.data.component.simple.GridPartStackComponent", "org.lifecompanion.base.data.component.simple.GridPartStackComponent")
            .with("fr.forusoftware.lifecompanion.data.component.simple.GridPartTextEditorComponent", "org.lifecompanion.base.data.component.simple.GridPartTextEditorComponent")
            .with("fr.forusoftware.lifecompanion.data.component.simple.WriterEntry", "org.lifecompanion.base.data.component.simple.WriterEntry")
            .with("fr.forusoftware.lifecompanion.data.component.simple.TextEditorComponent", "org.lifecompanion.base.data.component.simple.TextEditorComponent")
            .with("fr.forusoftware.lifecompanion.data.component.simple.PredictionParameter", "org.lifecompanion.base.data.component.simple.PredictionParameter")
            .with("fr.forusoftware.lifecompanion.data.component.simple.StackComponent", "org.lifecompanion.base.data.component.simple.StackComponent")
            .with("fr.forusoftware.lifecompanion.data.component.simple.ComponentSpan", "org.lifecompanion.base.data.component.simple.ComponentSpan")
            // Config
            .with("fr.forusoftware.lifecompanion.data.component.simple.VirtualMouseParameter", "org.lifecompanion.base.data.component.simple.VirtualMouseParameter")
            // Selection modes
            .with("fr.forusoftware.lifecompanion.data.definition.selection.impl.AutoActivationSelectionMode", "org.lifecompanion.base.data.definition.selection.impl.AutoActivationSelectionMode")
            .with("fr.forusoftware.lifecompanion.data.definition.selection.impl.ColumnRowScanSelectionMode", "org.lifecompanion.base.data.definition.selection.impl.ColumnRowScanSelectionMode")
            .with("fr.forusoftware.lifecompanion.data.definition.selection.impl.DirectActivationSelectionMode", "org.lifecompanion.base.data.definition.selection.impl.DirectActivationSelectionMode")
            .with("fr.forusoftware.lifecompanion.data.definition.selection.impl.HorizontalDirectKeyScanSelectionMode", "org.lifecompanion.base.data.definition.selection.impl.HorizontalDirectKeyScanSelectionMode")
            .with("fr.forusoftware.lifecompanion.data.definition.selection.impl.VerticalDirectKeyScanSelectionMode", "org.lifecompanion.base.data.definition.selection.impl.VerticalDirectKeyScanSelectionMode")
            .with("fr.forusoftware.lifecompanion.data.definition.selection.impl.RowColumnScanSelectionMode", "org.lifecompanion.base.data.definition.selection.impl.RowColumnScanSelectionMode")
            // Key options
            .with("fr.forusoftware.lifecompanion.data.component.keyoption.AutoCharKeyOption", "org.lifecompanion.base.data.component.keyoption.AutoCharKeyOption")
            .with("fr.forusoftware.lifecompanion.data.component.keyoption.BasicKeyOption", "org.lifecompanion.base.data.component.keyoption.BasicKeyOption")
            .with("fr.forusoftware.lifecompanion.data.component.keyoption.CustomCharKeyOption", "org.lifecompanion.base.data.component.keyoption.CustomCharKeyOption")
            .with("fr.forusoftware.lifecompanion.data.component.keyoption.NoteKeyOption", "org.lifecompanion.base.data.component.keyoption.note.NoteKeyOption")
            .with("fr.forusoftware.lifecompanion.data.component.keyoption.QuickComKeyOption", "org.lifecompanion.base.data.component.keyoption.QuickComKeyOption")
            .with("fr.forusoftware.lifecompanion.data.component.keyoption.VariableInformationKeyOption", "org.lifecompanion.base.data.component.keyoption.VariableInformationKeyOption")
            .with("fr.forusoftware.lifecompanion.data.component.keyoption.WordPredictionKeyOption", "org.lifecompanion.base.data.component.keyoption.WordPredictionKeyOption");

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
