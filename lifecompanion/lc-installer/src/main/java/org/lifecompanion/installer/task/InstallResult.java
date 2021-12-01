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

package org.lifecompanion.installer.task;

public enum InstallResult {
    INSTALLATION_SUCCESS("lc.installer.installation.result.success", true),
    INSTALLATION_FAILED_DOWNLOAD("lc.installer.installation.result.failed.download", false),
    INSTALLATION_FAILED_ADMIN_RIGHTS("lc.installer.installation.result.failed.admin", false),
    INSTALLATION_FAILED_SYSTEM_SPECIFIC("lc.installer.installation.result.failed.system", false);

    private final String translationId;
    private final boolean success;

    InstallResult(String translationId, boolean success) {
        this.translationId = translationId;
        this.success = success;
    }

    public String getTranslationId() {
        return translationId;
    }

    public boolean isSuccess() {
        return success;
    }
}
