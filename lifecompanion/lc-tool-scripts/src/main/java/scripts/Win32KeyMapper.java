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

package scripts;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Win32KeyMapper {
    public static void main(String[] args) throws Exception {
        Document doc = Jsoup.connect("https://docs.microsoft.com/en-us/windows/win32/inputdev/virtual-key-codes").get();
        Element firstTable = doc.select("main table tbody").get(0);
        for (Element trElement : firstTable.select("tr")) {
            final Elements dtList = trElement.select("td dl dt");
            if (dtList.size() == 2) {
                final String code = dtList.get(0).text();
                final String hexaValue = dtList.get(1).text();
                final String desc = trElement.select("td").get(1).text();
                //System.out.println(code + " = " + hexaValue + " / " + desc);
                final String fxCode = code.startsWith("VK_") ? code.substring(3) : " TODO ";
                System.out.println("WIN32TO_JAVAFX.put(" + hexaValue + ", KeyCode." + fxCode + "); // " + hexaValue + " - " + code + " - " + desc);

                //

            } else {
                System.err.println("ERROR ON " + dtList);
            }
        }


    }
}
