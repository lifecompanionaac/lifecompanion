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

package scripts.imagedictionaries;

import java.io.File;

public class DicInfo {
    final ImageDictionary dictionary;
    final File inputdir;
    final String dicId;
    final boolean checkSim;
    final boolean replaceBackground;
    final boolean resize;
    final boolean logDouble;
    boolean deleteImageIntegratedLabel;
    boolean deleteNB;

    DicInfo(ImageDictionary dictionary, File inputdir, String dicId, boolean checkSim, boolean replaceBackground, boolean resize, boolean logDouble, boolean deleteImageIntegratedLabel, boolean deleteNB) {
        this.dictionary = dictionary;
        this.inputdir = inputdir;
        this.dicId = dicId;
        this.checkSim = checkSim;
        this.replaceBackground = replaceBackground;
        this.resize = resize;
        this.logDouble = logDouble;
        this.deleteImageIntegratedLabel = deleteImageIntegratedLabel;
        this.deleteNB = deleteNB;
    }
}
