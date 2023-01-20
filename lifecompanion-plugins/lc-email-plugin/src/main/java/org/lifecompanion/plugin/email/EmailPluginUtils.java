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

package org.lifecompanion.plugin.email;

import org.jetbrains.annotations.NotNull;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class EmailPluginUtils {
    public static String trimToEmpty(final String str) {
        return str != null ? str.trim() : "";
    }

    public static String getPersonalAddressFormatted(Address[] addresses) {
        if (addresses != null) {
            return Arrays.stream(addresses).map(a -> ((InternetAddress) a)).map(EmailPluginUtils::getPersonalOrAddress).collect(Collectors.joining(", "));
        } else return "";
    }

    public static String getPersonalOrAddress(InternetAddress a) {
        String personal = trimToEmpty(a.getPersonal());
        if (StringUtils.isNotBlank(personal) && !StringUtils.isEqualsIgnoreCase("null", personal)) {
            return personal;
        } else {
            return trimToEmpty(a.getAddress());
        }
    }

}
