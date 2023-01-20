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

package org.lifecompanion.plugin.calendar.action.event;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.lifecompanion.controller.resource.ResourceHelper;
import org.lifecompanion.controller.voicesynthesizer.VoiceSynthesizerController;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.impl.categorizedelement.useaction.BaseUseActionImpl;
import org.lifecompanion.plugin.calendar.action.category.CalendarActionSubCategories;
import org.lifecompanion.plugin.calendar.controller.CalendarController;
import org.lifecompanion.plugin.calendar.keyoption.CalendarEventListKeyOption;
import org.lifecompanion.plugin.calendar.model.CalendarEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

public class CatchUpCalendarEventAction extends BaseUseActionImpl<GridPartKeyComponentI> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CatchUpCalendarEventAction.class);

    private AtomicInteger selectionCount = new AtomicInteger();
    private long firstSelectionTime;

    public CatchUpCalendarEventAction() {
        super(GridPartKeyComponentI.class);
        this.order = 30;
        this.category = CalendarActionSubCategories.EVENT;
        this.nameID = "calendar.plugin.action.catch.up.calendar.event.name";
        this.staticDescriptionID = "calendar.plugin.action.catch.up.calendar.event.description";
        this.configIconPath = "icon_catchup.png";
        this.parameterizableAction = false;
        this.selectionCount = new AtomicInteger();
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    private static final int SELECTION_DELAY = 800;
    private static final int SELECTION_COUNT_THRESHOLD = 3;


    @Override
    public void eventStarts(UseActionEvent eventType) {
        GridPartKeyComponentI parentKey = this.parentComponentProperty().get();
        if (parentKey != null) {
            if (parentKey.keyOptionProperty().get() instanceof CalendarEventListKeyOption) {
                CalendarEventListKeyOption calendarEventListKeyOption = (CalendarEventListKeyOption) parentKey.keyOptionProperty().get();
                CalendarEvent calendarEvent = calendarEventListKeyOption.currentSimplerKeyContentContainerProperty().get();
                if (calendarEvent != null) {
                    if (System.currentTimeMillis() - firstSelectionTime > SELECTION_DELAY) {
                        firstSelectionTime = System.currentTimeMillis();
                        this.selectionCount.set(1);
                    } else {
                        if (this.selectionCount.incrementAndGet() >= SELECTION_COUNT_THRESHOLD) {
                            VoiceSynthesizerController.INSTANCE.stopCurrentSpeakAndClearQueue();
                            initConfirmationSoundPlayer();
                            confirmationSoundPlayer.stop();
                            confirmationSoundPlayer.play();
                            CalendarController.INSTANCE.catchUpCalendarEvent(calendarEvent);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void eventEnds(UseActionEvent eventType) {
    }

    private static MediaPlayer confirmationSoundPlayer;

    private static void initConfirmationSoundPlayer() {
        if (confirmationSoundPlayer == null) {
            File destSoundFile = new File(org.lifecompanion.util.IOUtils.getTempDir("calendar-sound") + File.separator + "confirm_catchup.wav");
            IOUtils.createParentDirectoryIfNeeded(destSoundFile);
            try (FileOutputStream fos = new FileOutputStream(destSoundFile)) {
                try (InputStream is = ResourceHelper.getInputStreamForPath("/confirm_catchup.wav")) {
                    IOUtils.copyStream(is, fos);
                    Media media = new Media(destSoundFile.toURI().toString());
                    confirmationSoundPlayer = new MediaPlayer(media);
                }
            } catch (Exception e) {
                LOGGER.warn("Couldn't copy catch up sound", e);
            }
        }
    }

}