package org.lifecompanion.controller.configurationcomponent;

import javafx.beans.property.SimpleIntegerProperty;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DynamicListHelper<K extends KeyOptionI, T> implements ModeListenerI {
    private final List<K> keyOptions;
    private List<T> items;
    private final SimpleIntegerProperty pageIndex;
    private final Class<K> keyOptionType;

    public DynamicListHelper(final Class<K> keyOptionType) {
        this.keyOptionType = keyOptionType;
        this.keyOptions = new ArrayList<>();
        this.pageIndex = new SimpleIntegerProperty(-1);
        this.pageIndex.addListener(inv -> refreshConfigurationList());
    }

    private void nextPage(boolean loop) {
        int maxPageCount = getMaxPageCount();
        if (pageIndex.get() + 1 < maxPageCount) {
            pageIndex.set(pageIndex.get() + 1);
        } else if (loop) {
            pageIndex.set(0);
        }
    }

    private int getMaxPageCount() {
        int pageSize = keyOptions.size();
        return (int) Math.ceil((1.0 * items.size()) / (1.0 * pageSize));
    }

    public void nextPageWithoutLoop() {
        nextPage(false);
    }

    public void nextPageWithLoop() {
        nextPage(true);
    }

    public void previousPageWithoutLoop() {
        previousPage(false);
    }

    public void previousPageWithLoop() {
        previousPage(true);
    }

    private void previousPage(boolean loop) {
        if (pageIndex.get() - 1 >= 0) {
            pageIndex.set(pageIndex.get() - 1);
        } else if (loop) {
            pageIndex.set(getMaxPageCount() - 1);
        }
    }

    private void refreshConfigurationList() {
        int pageIndexV = pageIndex.get();
        for (int i = 0; i < keyOptions.size(); i++) {
            int index = pageIndexV * keyOptions.size() + i;
            K keyOption = keyOptions.get(i);
            FXThreadUtils.runOnFXThread(() -> updateKeyOption(keyOption, index >= 0 && index < items.size() ? items.get(index) : null));
        }
    }

    @Override
    public void modeStart(LCConfigurationI configuration) {
        items = getItemsFromConfiguration(configuration);

        if (!CollectionUtils.isEmpty(items)) {
            Map<GridComponentI, List<K>> groupKeysMap = new HashMap<>();
            ConfigurationComponentUtils.findKeyOptionsByGrid(this.keyOptionType, configuration, groupKeysMap, null);
            groupKeysMap.values().stream().flatMap(List::stream).distinct().forEach(keyOptions::add);
            this.pageIndex.set(0);
        }
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        this.items = null;
        this.keyOptions.clear();
        this.pageIndex.set(-1);
    }

    protected abstract List<T> getItemsFromConfiguration(LCConfigurationI configuration);

    protected abstract void updateKeyOption(K keyOption, T item);
}