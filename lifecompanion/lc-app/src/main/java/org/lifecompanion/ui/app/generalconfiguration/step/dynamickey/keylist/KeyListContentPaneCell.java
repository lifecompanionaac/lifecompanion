package org.lifecompanion.ui.app.generalconfiguration.step.dynamickey.keylist;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.configurationcomponent.editmode.componentoption.ButtonComponentOption;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.binding.BindingUtils;

public class KeyListContentPaneCell extends StackPane implements LCViewInitHelper {
    private static final double CELL_WIDTH = 75, CELL_HEIGHT = 75;
    private static final double LABEL_HEIGHT = 15.0, SPACE = 3.0;
    private static final double STROKE_WIDTH = 3.0;
    private final BooleanProperty selected;
    private final ObjectProperty<KeyListNodeI> item;


    private ImageView imageView;
    private Node listGlyph, keyGlyph, linkGlyph;
    private HBox glyphPane;
    private Label labelText;
    private Rectangle rectangleColors;

    private final String nodeIdForImageLoading;

    private Button buttonOpenOrFollowLink;
    private final KeyListContentConfigView keyListContentConfigView;
    private final KeyListContentPane keyListContentPane;

    public KeyListContentPaneCell(KeyListContentPane keyListContentPane, final KeyListContentConfigView keyListContentConfigView) {
        this.keyListContentConfigView = keyListContentConfigView;
        this.keyListContentPane = keyListContentPane;
        selected = new SimpleBooleanProperty();
        this.item = new SimpleObjectProperty<>();
        nodeIdForImageLoading = "KeyListFlowPaneCell" + this.hashCode();
        initAll();
    }


    public BooleanProperty selectedProperty() {
        return selected;
    }

    public ObjectProperty<KeyListNodeI> itemProperty() {
        return item;
    }

    @Override
    public void initUI() {
        this.getStyleClass().addAll("opacity-80-hover", "opacity-60-pressed");
        listGlyph = GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.FOLDER).size(14).color(LCGraphicStyle.LC_GRAY);
        final HBox iconTypeLeaf = new HBox(new ImageView(IconHelper.get("keylist/icon_type_leaf.png")));
        iconTypeLeaf.getStyleClass().add("padding-3_5");
        this.keyGlyph = iconTypeLeaf;
        linkGlyph = GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.LINK).size(14).color(LCGraphicStyle.LC_GRAY);
        glyphPane = new HBox();

        this.buttonOpenOrFollowLink = new Button();
        ButtonComponentOption.applyButtonBaseStyle(this.buttonOpenOrFollowLink, LCGraphicStyle.SECOND_DARK, FontAwesome.Glyph.SHARE, 10, 8, 18);
        this.buttonOpenOrFollowLink.getStyleClass().addAll("padding-0");
        this.buttonOpenOrFollowLink.setMaxSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);

        labelText = new Label();
        labelText.setPrefHeight(LABEL_HEIGHT);
        labelText.setMaxWidth(CELL_WIDTH);
        labelText.setTextOverrun(OverrunStyle.ELLIPSIS);
        labelText.setAlignment(Pos.CENTER);
        labelText.setTextAlignment(TextAlignment.CENTER);

        imageView = new ImageView();
        imageView.setFitHeight(CELL_HEIGHT - LABEL_HEIGHT - SPACE - STROKE_WIDTH);
        imageView.setFitWidth(CELL_WIDTH - STROKE_WIDTH * 2.0);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        rectangleColors = new Rectangle(CELL_WIDTH - STROKE_WIDTH, CELL_HEIGHT - STROKE_WIDTH);
        rectangleColors.setFill(Color.TRANSPARENT);
        rectangleColors.setStrokeWidth(STROKE_WIDTH);
        rectangleColors.setStroke(Color.TRANSPARENT);

        StackPane stackPaneContent = new StackPane(rectangleColors, imageView, glyphPane, labelText, buttonOpenOrFollowLink);
        stackPaneContent.setAlignment(Pos.CENTER);
        StackPane.setAlignment(imageView, Pos.TOP_CENTER);
        StackPane.setAlignment(buttonOpenOrFollowLink, Pos.TOP_RIGHT);
        StackPane.setMargin(imageView, new Insets(STROKE_WIDTH, STROKE_WIDTH, LABEL_HEIGHT + SPACE, STROKE_WIDTH));
        StackPane.setAlignment(glyphPane, Pos.TOP_LEFT);
        StackPane.setAlignment(labelText, Pos.BOTTOM_CENTER);
        this.getChildren().add(stackPaneContent);
        this.setPrefSize(CELL_WIDTH, CELL_HEIGHT);
    }

    @Override
    public void initListener() {
        this.setOnMouseClicked(e -> {
            if (e.getClickCount() >= 2) {
                KeyListNodeI item = this.item.get();
                if (!item.isLeafNode()) {
                    keyListContentConfigView.openList(item);
                } else if (item.isLinkNode()) {
                    keyListContentConfigView.openById(item.linkedNodeIdProperty().get());
                }
            } else {
                try {
                    this.keyListContentPane.setTempDisableScrollTo(true);
                    if (keyListContentConfigView.selectedProperty().get() == item.get()) {
                        keyListContentConfigView.clearSelection();
                    } else {
                        keyListContentConfigView.select(item.get());
                    }
                } finally {
                    this.keyListContentPane.setTempDisableScrollTo(false);
                }
            }
        });
        this.buttonOpenOrFollowLink.setOnAction(e -> {
            KeyListNodeI item = this.item.get();
            if (item != null) {
                if (item.isLinkNode()) {
                    keyListContentConfigView.openById(item.linkedNodeIdProperty().get());
                } else if (!item.isLeafNode()) {
                    keyListContentConfigView.openList(item);
                }
            }
        });
        KeyListContentConfigView.installDragNDropOn(keyListContentConfigView, this, n -> n.item.get());
    }

    @Override
    public void initBinding() {
        item.addListener((obs, ov, nv) -> {
            if (ov != null) {
                BindingUtils.unbindAndSetNull(imageView.imageProperty());
                BindingUtils.unbindAndSet(imageView.visibleProperty(), false);
                BindingUtils.unbindAndSetNull(labelText.textProperty());
                BindingUtils.unbindAndSetNull(labelText.textFillProperty());
                BindingUtils.unbindAndSetNull(rectangleColors.strokeProperty());
                BindingUtils.unbindAndSetNull(rectangleColors.fillProperty());
                BindingUtils.unbindAndSet(rectangleColors.visibleProperty(), false);
                BindingUtils.unbindAndSet(buttonOpenOrFollowLink.visibleProperty(), false);
                glyphPane.getChildren().clear();
                ov.removeExternalLoadingRequest(nodeIdForImageLoading);
            }
            if (nv != null) {
                glyphPane.getChildren().clear();
                imageView.imageProperty().bind(nv.loadedImageProperty());
                imageView.visibleProperty().bind(nv.loadedImageProperty().isNotNull());
                glyphPane.getChildren().add(nv.isLinkNode() ? linkGlyph : nv.isLeafNode() ? keyGlyph : listGlyph);
                rectangleColors.strokeProperty().bind(Bindings.createObjectBinding(() -> selected.get() ? LCGraphicStyle.SECOND_DARK : nv.strokeColorProperty().get(),
                        selected, nv.strokeColorProperty()));
                rectangleColors.fillProperty().bind(nv.backgroundColorProperty());
                rectangleColors.visibleProperty().bind(selected.or(nv.strokeColorProperty().isNotNull().or(nv.backgroundColorProperty().isNotNull())));
                KeyListNodeI itemV = item.get();
                buttonOpenOrFollowLink.visibleProperty()
                        .bind(new SimpleBooleanProperty(itemV.isLeafNode()).not().or(itemV.linkedNodeIdProperty().isNotEmpty().and(new SimpleBooleanProperty(itemV.isLinkNode()))));
                labelText.textProperty()
                        .bind(Bindings.createStringBinding(nv::getHumanReadableText,
                                nv.textProperty(),
                                nv.enableWriteProperty(),
                                nv.textToWriteProperty(),
                                nv.enableSpeakProperty(),
                                nv.textToSpeakProperty()));
                labelText.textFillProperty().bind(Bindings.createObjectBinding(() -> nv.textColorProperty().get() != null ? nv.textColorProperty().get() : Color.BLACK, nv.textColorProperty()));
                nv.addExternalLoadingRequest(nodeIdForImageLoading);
            }
        });
    }
}
