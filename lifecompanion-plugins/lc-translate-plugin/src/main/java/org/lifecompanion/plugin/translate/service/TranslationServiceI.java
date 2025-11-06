/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.plugin.translate.service;

public interface TranslationServiceI {
    String translate(String sourceLanguageCode, String targetLanguageCode, String textToTranslate) throws Exception;

    /**
     * Must initialize the service.<br>
     * The synthesizer should be able to work after this call.
     */
    void initialize() throws Exception;

    /**
     * @return true if and if only the service is initialized.
     */
    boolean isInitialized();

    /**
     * Should dispose all resources that this service use.
     *
     * @throws Exception if dispose can't be done
     */
    void dispose();

}
