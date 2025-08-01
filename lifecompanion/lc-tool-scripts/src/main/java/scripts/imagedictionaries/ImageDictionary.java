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

import java.util.List;
import java.util.Map;

public class ImageDictionary {
    String name;
    String description;
    String author;
    String imageExtension;
    String url;
    boolean customDictionary;
    String idCheck;
    List<ImageElement> images;
    Map<String, String> patched;

    public ImageDictionary() {

    }

    public ImageDictionary(String name, String description, String author, String imageExtension, String url, boolean customDictionary) {
        this.name = name;
        this.description = description;
        this.author = author;
        this.imageExtension = imageExtension;
        this.url = url;
        this.customDictionary = customDictionary;
    }
}
