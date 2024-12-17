package org.lifecompanion.plugin.phonecontrol;

import javafx.beans.property.ObjectProperty;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.plugin.PluginConfigPropertiesI;
import org.lifecompanion.model.api.plugin.PluginI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.usevariable.IntegerUseVariable;
import org.lifecompanion.model.impl.usevariable.StringUseVariable;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;
import org.lifecompanion.plugin.phonecontrol.controller.ConnexionController;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class PhoneControlPlugin implements PluginI {
    public static final String PLUGIN_ID = "lc-phonecontrol-plugin";

    public PhoneControlPlugin() { }

    @Override
    public String[] getLanguageFiles(final String languageCode) {
        return new String[] { "/text/" + languageCode + "_phonecontrol_plugin.xml" };
    }

    @Override
    public String[] getJavaFXStylesheets() {
        return null;
    }

    @Override
    public String[] getDefaultConfigurations(String languageCode) {
        return new String[] { "/configurations/" + languageCode + "_phonecontrol-example.lcc" };
    }

    @Override
    public void start(File dataDirectory) {
        PhoneCommunicationManager.INSTANCE.setProtocolType(PhoneCommunicationManager.ProtocolType.ADB, dataDirectory);
    }

    @Override
    public void stop(File dataDirectory) {
        PhoneCommunicationManager.INSTANCE.stop();
    }

    @Override
    public void modeStart(LCConfigurationI configuration) {
        ConnexionController.INSTANCE.modeStart(configuration);
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        ConnexionController.INSTANCE.modeStop(configuration);
    }

    @Override
    public List<UseVariableDefinitionI> getDefinedVariables() {
        return Arrays.asList(
            new UseVariableDefinition(
                ConnexionController.VAR_SMS_UNREAD,
                "phonecontrol.plugin.use.variable.sms.unread.name",
                "phonecontrol.plugin.use.variable.sms.unread.description",
                "phonecontrol.plugin.use.variable.sms.unread.example"
            ),
            new UseVariableDefinition(
                ConnexionController.VAR_CALL_DURATION,
                "phonecontrol.plugin.use.variable.call.duration.name",
                "phonecontrol.plugin.use.variable.call.duration.description",
                "phonecontrol.plugin.use.variable.call.duration.example"
            ),
            new UseVariableDefinition(
                ConnexionController.VAR_PHONE_NUMBER_OR_CONTACT_NAME,
                "phonecontrol.plugin.use.variable.phone.number.or.contact.name.name",
                "phonecontrol.plugin.use.variable.phone.number.or.contact.name.description",
                "phonecontrol.plugin.use.variable.phone.number.or.contact.name.example"
            )
        );
    }

    @Override
    public Function<UseVariableDefinitionI, UseVariableI<?>> getSupplierForUseVariable(String id) {
        return switch (id) {
            case ConnexionController.VAR_SMS_UNREAD ->
                def -> new IntegerUseVariable(def, ConnexionController.INSTANCE.getSmsUnread());
            case ConnexionController.VAR_CALL_DURATION ->
                def -> new StringUseVariable(def, ConnexionController.INSTANCE.getCallDuration());
            case ConnexionController.VAR_PHONE_NUMBER_OR_CONTACT_NAME ->
                def -> new StringUseVariable(def, ConnexionController.INSTANCE.getPhoneNumberOrContactName());
            default -> null;
        };
    }

    @Override
    public PluginConfigPropertiesI newPluginConfigProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        return new PhoneControlPluginProperties(parentConfiguration);
    }
}
