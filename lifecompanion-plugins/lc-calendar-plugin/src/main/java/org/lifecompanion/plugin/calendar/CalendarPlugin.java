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
package org.lifecompanion.plugin.calendar;

import javafx.beans.property.ObjectProperty;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.plugin.PluginConfigPropertiesI;
import org.lifecompanion.model.api.plugin.PluginI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.plugin.calendar.controller.CalendarController;
import org.lifecompanion.plugin.calendar.controller.SoundAlarmController;
import org.lifecompanion.plugin.calendar.model.CalendarEvent;

import java.io.File;
import java.util.List;
import java.util.Map;

public class CalendarPlugin implements PluginI {
    public static final String PLUGIN_ID = "lc-calendar-plugin";

    public CalendarPlugin() {
    }

    @Override
    public String[] getLanguageFiles(final String languageCode) {
        return new String[]{"/text/" + languageCode + "_calendar_plugin.xml"};
    }

    @Override
    public String[] getJavaFXStylesheets() {
        return null;
    }

    // START/STOP
    //========================================================================
    @Override
    public void modeStart(final LCConfigurationI configuration) {
        SoundAlarmController.INSTANCE.modeStart(configuration);
        CalendarController.INSTANCE.modeStart(configuration);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        CalendarController.INSTANCE.modeStop(configuration);
        SoundAlarmController.INSTANCE.modeStop(configuration);
    }

    //    public static LCCalendar calendar;

    @Override
    public void start(final File dataFolder) {
        //        calendar = new LCCalendar();
        //        calendar.initEmptyWeek();
        //        final CalendarDay today = calendar.getDays().get(2);
        //        // Create event
        //        today.getEvents().add(createEvent("Prendre le petit déjeuner", "Je vais chercher mon plateau petit déjeuner"));
        //        today.getEvents().add(createEvent("Petit déjeuner terminé", "Je ramène mon plateau dans le coin repas", "Bien !"));
        //
        //        final CalendarEvent ouvrirLaFenetre = createEvent("Ouvrir la fenêtre", "J’ouvre ma fenêtre");
        //        ouvrirLaFenetre.enableAutomaticItemProperty().set(true);
        //        ouvrirLaFenetre.automaticItemTimeMsProperty().set(5_000);
        //        today.getEvents().add(ouvrirLaFenetre);
        //
        //        today.getEvents().add(createEvent("Débrancher les tablettes", "Je débranche mes tablettes"));
        //        final CalendarEvent eventWithSeq = createEvent("Se préparer", "Je vais faire ma toilette au lavabo", "Tu as fini ta toilette, bien !");
        //        //eventWithSeq.linkedSequenceIdProperty().set("ee9a7662-0d91-4159-86db-1931200a9e96");
        //        today.getEvents().add(eventWithSeq);
        //        final CalendarEvent prendreUnCafe = createEvent("Prendre un café", "Je vais prendre mon café");
        //        prendreUnCafe.enableAtFixedTimeProperty().set(true);
        //        prendreUnCafe.getFixedTime().hoursProperty().set(10);
        //        prendreUnCafe.getFixedTime().minutesProperty().set(45);
        //        today.getEvents().add(prendreUnCafe);
        //        today.getEvents().add(createEvent("Activités", "Je vais faire mes activités à l'annexe avec ma tablette"));

        //        today.getEvents().add(createEvent("Aller aux WC", "Je vais aux WC avec ma tablette bleue "));
        //        today.getEvents().add(createEvent("Se préparer - fenêtre", "Je ferme ma fenêtre"));
        //        today.getEvents().add(createEvent("Se préparer - habillage", "Je m’habille"));
    }

    private CalendarEvent createEvent(String name, String textOnStart) {
        return createEvent(name, textOnStart, null);
    }

    private CalendarEvent createEvent(String name, String textOnStart, String textOnFinish) {
        CalendarEvent calendarEvent = new CalendarEvent();
        calendarEvent.textProperty().set(name);
        if (StringUtils.isNotBlank(textOnStart)) {
            calendarEvent.textOnStartProperty().set(textOnStart);
            calendarEvent.enableTextOnStartProperty().set(true);
        }
        if (StringUtils.isNotBlank(textOnFinish)) {
            calendarEvent.textOnFinishProperty().set(textOnFinish);
            calendarEvent.enableTextOnFinishProperty().set(true);
        }
        return calendarEvent;
    }

    @Override
    public void stop(File dataFolder) {
    }
    //========================================================================


    // VARIABLES
    //========================================================================

    @Override
    public List<UseVariableDefinitionI> getDefinedVariables() {
        return null;
    }

    @Override
    public Map<String, UseVariableI<?>> generateVariables(final Map<String, UseVariableDefinitionI> variablesToGenerate) {
        return null;
    }

    //========================================================================

    // IO
    //========================================================================


    @Override
    public PluginConfigPropertiesI newPluginConfigProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        return new CalendarPluginProperties(parentConfiguration);
    }
    //========================================================================

}
