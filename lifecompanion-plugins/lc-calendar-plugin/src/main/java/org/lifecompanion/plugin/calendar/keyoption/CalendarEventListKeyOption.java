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

package org.lifecompanion.plugin.calendar.keyoption;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.jdom2.Element;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.commons.fx.io.XMLIgnoreDefaultBooleanValue;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.style.TextCompStyleI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.dynamickey.AbstractSimplerKeyContentContainerKeyOption;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.dynamickey.RectangleOnKeyForKeyViewAdded;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.plugin.calendar.model.CalendarEvent;
import org.lifecompanion.plugin.calendar.model.CalendarEventStatus;

public class CalendarEventListKeyOption extends AbstractSimplerKeyContentContainerKeyOption<CalendarEvent> {
    @XMLIgnoreDefaultBooleanValue(value = false)
    private final BooleanProperty forCurrentEvent;

    @XMLIgnoreDefaultBooleanValue(value = false)
    private final BooleanProperty forRunningEvent;

    private final ObjectProperty<Node> statusNode;
    private final ObjectProperty<Node> hourNode;

    public CalendarEventListKeyOption() {
        this.forCurrentEvent = new SimpleBooleanProperty(false);
        this.forRunningEvent = new SimpleBooleanProperty(false);
        this.optionNameId = "calendar.plugin.key.option.calendar.event.list";
        this.iconName = "icon_keyoption_event.png";

        statusNode = new SimpleObjectProperty<>();
        hourNode = new SimpleObjectProperty<>();

        InvalidationListener statusUpdate = inv -> statusUpdate();
        InvalidationListener hourUpdate = inv -> hourUpdate();
        this.currentSimplerKeyContentContainerProperty().addListener((obs, ov, nv) -> {
            if (ov != null) {
                ov.statusProperty().removeListener(statusUpdate);
                ov.enableAtFixedTimeProperty().removeListener(hourUpdate);
            }
            if (nv != null) {
                nv.statusProperty().addListener(statusUpdate);
                nv.enableAtFixedTimeProperty().addListener(hourUpdate);
            }
            statusUpdate.invalidated(null);
            hourUpdate.invalidated(null);
        });
        keyViewAddedNodeProperty().bind(Bindings.createObjectBinding(() -> {
            if (statusNode.get() != null || hourNode.get() != null) {
                StackPane detailNode = new StackPane();
                if (statusNode.get() != null) {
                    StackPane.setAlignment(statusNode.get(), Pos.CENTER);
                    detailNode.getChildren().add(statusNode.get());
                }
                if (hourNode.get() != null) {
                    StackPane.setAlignment(hourNode.get(), Pos.TOP_LEFT);
                    detailNode.getChildren().add(hourNode.get());
                }
                return detailNode;
            }
            return null;
        }, hourNode, statusNode));
    }

    @Override
    protected String getDefaultTextContentProperty() {
        return AppModeController.INSTANCE.isEditMode() ? Translation.getText("calendar.plugin.key.option.calendar.event.list.default.text") : "";
    }


    public BooleanProperty forCurrentEventProperty() {
        return forCurrentEvent;
    }

    public BooleanProperty forRunningEventProperty() {
        return forRunningEvent;
    }

    private void hourUpdate() {
        final CalendarEvent calendarEvent = currentSimplerKeyContentContainer.get();
        if (calendarEvent != null && calendarEvent.enableAtFixedTimeProperty().get()) {
            final CalendarEventStatus calendarEventStatus = calendarEvent.statusProperty().get();
            if (!forRunningEvent.get()) {
                if (calendarEventStatus == CalendarEventStatus.CURRENT) {
                    statusNode.set(new RectangleOnKeyForKeyViewAdded().withStrokeColor(RectangleOnKeyForKeyViewAdded.CURRENT_COLOR));
                } else if (calendarEventStatus == CalendarEventStatus.PAST) {
                    statusNode.set(new RectangleOnKeyForKeyViewAdded().withBackgroundReduction());
                } else if (calendarEventStatus == CalendarEventStatus.DONE) {
                    statusNode.set(new RectangleOnKeyForKeyViewAdded().withStrokeColor(RectangleOnKeyForKeyViewAdded.STRIKE_OUT_COLOR).withStrikeout().withBackgroundReduction());
                }
            }
            Text hourText = new Text(calendarEvent.getFixedTime().getHumanReadableString());
            final TextCompStyleI keyTextStyle = attachedKey.get().getKeyTextStyle();
            final Font font = keyTextStyle.fontProperty().get();
            hourText.setFont(font);
            hourText.setFill(keyTextStyle.colorProperty().value().getValue());
            hourText.setScaleX(0.7);
            hourText.setScaleY(0.7);
            hourNode.set(hourText);
        } else {
            hourNode.set(null);
        }
    }

    private void statusUpdate() {
        final CalendarEvent calendarEvent = currentSimplerKeyContentContainer.get();
        if (calendarEvent != null && calendarEvent.statusProperty().get() != null && !forRunningEvent.get()) {
            final CalendarEventStatus calendarEventStatus = calendarEvent.statusProperty().get();
            if (calendarEventStatus == CalendarEventStatus.CURRENT) {
                statusNode.set(new RectangleOnKeyForKeyViewAdded().withStrokeColor(RectangleOnKeyForKeyViewAdded.CURRENT_COLOR));
            } else if (calendarEventStatus == CalendarEventStatus.PAST) {
                statusNode.set(new RectangleOnKeyForKeyViewAdded().withBackgroundReduction());
            } else if (calendarEventStatus == CalendarEventStatus.DONE) {
                statusNode.set(new RectangleOnKeyForKeyViewAdded().withStrokeColor(RectangleOnKeyForKeyViewAdded.STRIKE_OUT_COLOR).withStrikeout().withBackgroundReduction());
            }
        } else {
            statusNode.set(null);
        }
    }


    @Override
    public Element serialize(final IOContextI context) {
        return XMLObjectSerializer.serializeInto(CalendarEventListKeyOption.class, this, super.serialize(context));
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(CalendarEventListKeyOption.class, this, node);
    }
}
