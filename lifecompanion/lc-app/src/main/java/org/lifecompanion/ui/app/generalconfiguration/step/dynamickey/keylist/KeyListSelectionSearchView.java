package org.lifecompanion.ui.app.generalconfiguration.step.dynamickey.keylist;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class KeyListSelectionSearchView extends HBox implements LCViewInitHelper {
    private final KeyListContentConfigView keyListContentConfigView;

    private final ObjectProperty<List<KeyListNodeI>> searchResult;
    private final IntegerProperty foundIndex;
    private String lastSearch;
    private TextField textFieldSearchNode;
    private Button buttonSearch, buttonPreviousFound, buttonNextFound, buttonClearSearch;
    private Label labelFoundNodeInfo;

    public KeyListSelectionSearchView(KeyListContentConfigView keyListContentConfigView) {
        this.keyListContentConfigView = keyListContentConfigView;
        searchResult = new SimpleObjectProperty<>();
        foundIndex = new SimpleIntegerProperty();
        initAll();
    }

    @Override
    public void initUI() {
        textFieldSearchNode = new TextField();
        textFieldSearchNode.setPromptText(Translation.getText("generaL.configuration.view.key.list.search.prompt"));
        textFieldSearchNode.setPrefColumnCount(30);
        buttonSearch = createSearchBarButton(FontAwesome.Glyph.SEARCH, LCGraphicStyle.MAIN_DARK, null, 16);
        buttonNextFound = createSearchBarButton(FontAwesome.Glyph.CHEVRON_RIGHT, LCGraphicStyle.MAIN_DARK, null, 18);
        buttonPreviousFound = createSearchBarButton(FontAwesome.Glyph.CHEVRON_LEFT, LCGraphicStyle.MAIN_DARK, null, 18);
        buttonClearSearch = createSearchBarButton(FontAwesome.Glyph.TIMES, LCGraphicStyle.SECOND_DARK, null, 18);
        labelFoundNodeInfo = new Label();
        labelFoundNodeInfo.setPrefWidth(40.0);
        labelFoundNodeInfo.getStyleClass().add("text-weight-bold");

        setSpacing(10.0);
        setAlignment(Pos.CENTER);

        getChildren().addAll(textFieldSearchNode, buttonSearch, buttonClearSearch, labelFoundNodeInfo, buttonPreviousFound, buttonNextFound);
    }

    private Button createSearchBarButton(FontAwesome.Glyph icon, Color color, String tooltip, int size) {
        final Button button = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(icon).size(size).color(color), tooltip);
        button.getStyleClass().add("padding-0");
        return button;
    }

    @Override
    public void initListener() {
        buttonSearch.setOnAction(e -> executeSearch(false));
        textFieldSearchNode.setOnAction(buttonSearch.getOnAction());
        textFieldSearchNode.textProperty().addListener((obs, ov, nv) -> clearSearch(false));
        buttonClearSearch.setOnAction(e -> clearSearch(true));
        buttonNextFound.setOnAction(e -> showNextSearchResult());
        buttonPreviousFound.setOnAction(e -> showPreviousSearchResult());
    }

    @Override
    public void initBinding() {
        this.buttonSearch.disableProperty().bind(textFieldSearchNode.textProperty().isEmpty());
        this.buttonClearSearch.disableProperty().bind(searchResult.isNull().and(textFieldSearchNode.textProperty().isEmpty()));
        this.buttonNextFound.visibleProperty().bind(searchResult.isNotNull());
        this.buttonNextFound.managedProperty().bind(buttonNextFound.visibleProperty());
        this.buttonPreviousFound.visibleProperty().bind(searchResult.isNotNull());
        this.buttonPreviousFound.managedProperty().bind(buttonNextFound.visibleProperty());
        this.labelFoundNodeInfo.visibleProperty().bind(searchResult.isNotNull());
        this.labelFoundNodeInfo.managedProperty().bind(labelFoundNodeInfo.visibleProperty());
        labelFoundNodeInfo.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    final List<KeyListNodeI> resultList = searchResult.get();
                    return resultList != null && !resultList.isEmpty() ? (foundIndex.get() + 1) + " / " + resultList.size() : Translation.getText("generaL.configuration.view.key.list.search.empty");
                },
                searchResult, foundIndex)
        );
        this.keyListContentConfigView.rootProperty().addListener((obs, ov, nv) -> {
            clearSearch(true);
        });
    }

    private void showNextSearchResult() {
        if (foundIndex.get() + 1 < searchResult.get().size()) {
            foundIndex.set(foundIndex.get() + 1);
        } else {
            foundIndex.set(0);
        }
        updateDisplayedResult();
    }

    private void showPreviousSearchResult() {
        if (foundIndex.get() - 1 >= 0) {
            foundIndex.set(foundIndex.get() - 1);
        } else {
            foundIndex.set(searchResult.get().size() - 1);
        }
        updateDisplayedResult();
    }

    private void clearSearch(boolean clearTextField) {
        searchResult.set(null);
        lastSearch = null;
        if (clearTextField) {
            textFieldSearchNode.clear();
        }
    }

    private static final Comparator<Pair<KeyListNodeI, Double>> SCORE_MAP_COMPARATOR = (e1, e2) -> Double.compare(e2.getRight(), e1.getRight());

    void executeSearch(boolean forceUpdate) {
        final String searchText = this.textFieldSearchNode.getText();
        if (StringUtils.isNotBlank(searchText)) {
            if (forceUpdate || StringUtils.isDifferent(searchText, lastSearch)) {
                lastSearch = searchText;

                foundIndex.set(0);
                // Create a list with all nodes
                List<KeyListNodeI> allNodes = new ArrayList<>(100);
                keyListContentConfigView.rootProperty().get().traverseTreeToBottom(allNodes::add);

                // Search for similarity
                final List<KeyListNodeI> foundNodes = allNodes
                        .parallelStream()
                        .map(node -> Pair.of(node, getSimilarityScore(node, searchText)))
                        .sorted(SCORE_MAP_COMPARATOR)
                        .filter(e -> e.getRight() > ConfigurationComponentUtils.SIMILARITY_CONTAINS)
                        .map(Pair::getLeft)
                        .collect(Collectors.toList());
                this.searchResult.set(foundNodes);
                updateDisplayedResult();
            } else {
                showNextSearchResult();
            }
            textFieldSearchNode.requestFocus();
        }
    }

    private void updateDisplayedResult() {
        int i = foundIndex.get();
        final List<KeyListNodeI> resultList = searchResult.get();
        if (resultList != null && i >= 0 && i < resultList.size()) {
            final KeyListNodeI toSelect = resultList.get(i);
            keyListContentConfigView.select(toSelect);
        }
    }

    public double getSimilarityScore(KeyListNodeI node, String searchFull) {
        double score = 0.0;
        score += ConfigurationComponentUtils.getSimilarityScoreFor(searchFull, node,
                n -> Pair.of(n.textProperty().get(), 1.0),
                n -> Pair.of(n.textToWriteProperty().get(), node.enableWriteProperty().get() ? 0.8 : 0),
                n -> Pair.of(n.textToSpeakProperty().get(), node.enableSpeakProperty().get() ? 0.8 : 0)
        );
        return score;
    }
}
