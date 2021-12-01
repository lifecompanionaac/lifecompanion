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

package org.lifecompanion.framework.commons.utils.app;

public class VersionUtils {

	public static int compare(String version1, String version2) {
		VersionInfo v1Info = VersionInfo.parse(version1);
		VersionInfo v2Info = VersionInfo.parse(version2);
		final int compareMajor = Integer.compare(v1Info.major, v2Info.major);
		if (compareMajor == 0) {
			final int compareMinor = Integer.compare(v1Info.minor, v2Info.minor);
			if (compareMinor == 0) {
				return Integer.compare(v1Info.patch, v2Info.patch);
			}
			return compareMinor;
		}
		return compareMajor;
	}

	public static class VersionInfo {
		private final int major, minor, patch;
		private final String details;

		private VersionInfo(int major, int minor, int patch, String details) {
			super();
			this.major = major;
			this.minor = minor;
			this.patch = patch;
			this.details = details;
		}

		public int getMajor() {
			return major;
		}

		public int getMinor() {
			return minor;
		}

		public int getPatch() {
			return patch;
		}

		public String getDetails() {
			return details;
		}

		public static VersionInfo parse(final String version) {
			if (version != null) {
				int firstHyphenIndex = version.indexOf('-');
				final String details = firstHyphenIndex >= 0 && firstHyphenIndex + 1 < version.length()
						? version.substring(firstHyphenIndex + 1, version.length())
						: null;
				final String versionNumberPart = firstHyphenIndex >= 0 ? version.substring(0, firstHyphenIndex) : version;
				String[] parts = versionNumberPart.split("\\.");
				if (parts.length > 0 && parts.length <= 3) {
					try {
						return new VersionInfo(Integer.parseInt(parts[0]), parts.length >= 2 ? Integer.parseInt(parts[1]) : 0,
								parts.length == 3 ? Integer.parseInt(parts[2]) : 0, details);
					} catch (NumberFormatException e) {}
				}
			}
			throw new IllegalArgumentException("The given version : \"" + version + "\" is not in a correct format");
		}
	}
}
