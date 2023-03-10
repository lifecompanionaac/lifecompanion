package org.lifecompanion.plugin.flirc.model;

import java.util.List;

public record IRLearningDialogResult(boolean cancelled, List<List<String>> codes) {
}
