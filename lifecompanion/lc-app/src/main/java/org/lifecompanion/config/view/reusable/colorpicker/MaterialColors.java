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

package org.lifecompanion.config.view.reusable.colorpicker;


import javafx.scene.paint.Color;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * From https://material.io/design/color/the-color-system.html#tools-for-picking-colors
 */
public enum MaterialColors {
    INSTANCE;

    private List<MaterialColor> colors;

    public List<MaterialColor> getColors() {
        if (colors == null) {
            initializeColors();
        }
        return colors;
    }

    public List<List<MaterialColor>> getColorsByTitle() {
        final List<MaterialColor> colors = getColors();
        final Map<String, List<MaterialColor>> colorsByTitleMap = colors.stream().collect(Collectors.groupingBy(MaterialColor::getTitle));
        List<List<MaterialColor>> colorsByTitle = new ArrayList<>();
        for (MaterialColor color : colors) {
            final List<MaterialColor> remove = colorsByTitleMap.remove(color.getTitle());
            if (remove != null) colorsByTitle.add(remove);
        }
        return colorsByTitle;
    }

    public static Color darker(Color color) {
        return color.darker();
    }

    public String getColorName(Color color) {
        if (color == null) {
            return Translation.getText("lc.colorpicker.null.value");
        } else if (color.getOpacity() < 0.001) {
            return Translation.getText("lc.colorpicker.transparent.value");
        } else {
            // Find base color
            return getColors().stream().filter(c -> LCUtils.colorEquals(c.getColor(), color)).findAny().map(mc -> mc.getTitle() + " " + mc.getSubTitle())
                    // Find dark color
                    .orElse(getColors().stream().filter(c -> LCUtils.colorEquals(darker(c.getColor()), color)).findAny().map(mc -> mc.getTitle() + " " + mc.getSubTitle() + "-D")
                            // No name : use web notation
                            .orElse(LCUtils.toWebColorWithoutAlpha(color)));
        }
    }

    private void initializeColors() {
        colors = new ArrayList<>();
        final Set<String> withoutA = Set.of("Brown 50", "Gray 50", "Blue Gray 50");
        try {
            final String[] lines = MATERIAL_COLOR_TXT.split("\n");
            for (int i = 0; i < lines.length; i++) {
                String title = lines[i];
                for (int j = 0; j < 10; j++) {
                    String subTitle = j != 0 ? lines[i + j * 2] : "";
                    final String val = lines[i + j * 2 + 1];
                    colors.add(new MaterialColor(title.replace(" 50", ""), subTitle, Color.web(val, 1)));
                }
                i += withoutA.contains(title) ? 19 : 27;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class MaterialColor {
        private final String title;
        private final String subTitle;
        private final Color color;

        public MaterialColor(String title, String subTitle, Color color) {
            this.title = title;
            this.subTitle = subTitle;
            this.color = color;
        }

        public String getTitle() {
            return title;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public Color getColor() {
            return color;
        }
    }

    // RAW COPY FROM WEBSITE
    private static final String MATERIAL_COLOR_TXT = "Red 50\n" +
            "#FFEBEE\n" +
            "100\n" +
            "#FFCDD2\n" +
            "200\n" +
            "#EF9A9A\n" +
            "300\n" +
            "#E57373\n" +
            "400\n" +
            "#EF5350\n" +
            "500\n" +
            "#F44336\n" +
            "600\n" +
            "#E53935\n" +
            "700\n" +
            "#D32F2F\n" +
            "800\n" +
            "#C62828\n" +
            "900\n" +
            "#B71C1C\n" +
            "A100\n" +
            "#FF8A80\n" +
            "A200\n" +
            "#FF5252\n" +
            "A400\n" +
            "#FF1744\n" +
            "A700\n" +
            "#D50000\n" +
            "Pink 50\n" +
            "#FCE4EC\n" +
            "100\n" +
            "#F8BBD0\n" +
            "200\n" +
            "#F48FB1\n" +
            "300\n" +
            "#F06292\n" +
            "400\n" +
            "#EC407A\n" +
            "500\n" +
            "#E91E63\n" +
            "600\n" +
            "#D81B60\n" +
            "700\n" +
            "#C2185B\n" +
            "800\n" +
            "#AD1457\n" +
            "900\n" +
            "#880E4F\n" +
            "A100\n" +
            "#FF80AB\n" +
            "A200\n" +
            "#FF4081\n" +
            "A400\n" +
            "#F50057\n" +
            "A700\n" +
            "#C51162\n" +
            "Purple 50\n" +
            "#F3E5F5\n" +
            "100\n" +
            "#E1BEE7\n" +
            "200\n" +
            "#CE93D8\n" +
            "300\n" +
            "#BA68C8\n" +
            "400\n" +
            "#AB47BC\n" +
            "500\n" +
            "#9C27B0\n" +
            "600\n" +
            "#8E24AA\n" +
            "700\n" +
            "#7B1FA2\n" +
            "800\n" +
            "#6A1B9A\n" +
            "900\n" +
            "#4A148C\n" +
            "A100\n" +
            "#EA80FC\n" +
            "A200\n" +
            "#E040FB\n" +
            "A400\n" +
            "#D500F9\n" +
            "A700\n" +
            "#AA00FF\n" +
            "Deep Purple 50\n" +
            "#EDE7F6\n" +
            "100\n" +
            "#D1C4E9\n" +
            "200\n" +
            "#B39DDB\n" +
            "300\n" +
            "#9575CD\n" +
            "400\n" +
            "#7E57C2\n" +
            "500\n" +
            "#673AB7\n" +
            "600\n" +
            "#5E35B1\n" +
            "700\n" +
            "#512DA8\n" +
            "800\n" +
            "#4527A0\n" +
            "900\n" +
            "#311B92\n" +
            "A100\n" +
            "#B388FF\n" +
            "A200\n" +
            "#7C4DFF\n" +
            "A400\n" +
            "#651FFF\n" +
            "A700\n" +
            "#6200EA\n" +
            "Indigo 50\n" +
            "#E8EAF6\n" +
            "100\n" +
            "#C5CAE9\n" +
            "200\n" +
            "#9FA8DA\n" +
            "300\n" +
            "#7986CB\n" +
            "400\n" +
            "#5C6BC0\n" +
            "500\n" +
            "#3F51B5\n" +
            "600\n" +
            "#3949AB\n" +
            "700\n" +
            "#303F9F\n" +
            "800\n" +
            "#283593\n" +
            "900\n" +
            "#1A237E\n" +
            "A100\n" +
            "#8C9EFF\n" +
            "A200\n" +
            "#536DFE\n" +
            "A400\n" +
            "#3D5AFE\n" +
            "A700\n" +
            "#304FFE\n" +
            "Blue 50\n" +
            "#E3F2FD\n" +
            "100\n" +
            "#BBDEFB\n" +
            "200\n" +
            "#90CAF9\n" +
            "300\n" +
            "#64B5F6\n" +
            "400\n" +
            "#42A5F5\n" +
            "500\n" +
            "#2196F3\n" +
            "600\n" +
            "#1E88E5\n" +
            "700\n" +
            "#1976D2\n" +
            "800\n" +
            "#1565C0\n" +
            "900\n" +
            "#0D47A1\n" +
            "A100\n" +
            "#82B1FF\n" +
            "A200\n" +
            "#448AFF\n" +
            "A400\n" +
            "#2979FF\n" +
            "A700\n" +
            "#2962FF\n" +
            "Light Blue 50\n" +
            "#E1F5FE\n" +
            "100\n" +
            "#B3E5FC\n" +
            "200\n" +
            "#81D4FA\n" +
            "300\n" +
            "#4FC3F7\n" +
            "400\n" +
            "#29B6F6\n" +
            "500\n" +
            "#03A9F4\n" +
            "600\n" +
            "#039BE5\n" +
            "700\n" +
            "#0288D1\n" +
            "800\n" +
            "#0277BD\n" +
            "900\n" +
            "#01579B\n" +
            "A100\n" +
            "#80D8FF\n" +
            "A200\n" +
            "#40C4FF\n" +
            "A400\n" +
            "#00B0FF\n" +
            "A700\n" +
            "#0091EA\n" +
            "Cyan 50\n" +
            "#E0F7FA\n" +
            "100\n" +
            "#B2EBF2\n" +
            "200\n" +
            "#80DEEA\n" +
            "300\n" +
            "#4DD0E1\n" +
            "400\n" +
            "#26C6DA\n" +
            "500\n" +
            "#00BCD4\n" +
            "600\n" +
            "#00ACC1\n" +
            "700\n" +
            "#0097A7\n" +
            "800\n" +
            "#00838F\n" +
            "900\n" +
            "#006064\n" +
            "A100\n" +
            "#84FFFF\n" +
            "A200\n" +
            "#18FFFF\n" +
            "A400\n" +
            "#00E5FF\n" +
            "A700\n" +
            "#00B8D4\n" +
            "Teal 50\n" +
            "#E0F2F1\n" +
            "100\n" +
            "#B2DFDB\n" +
            "200\n" +
            "#80CBC4\n" +
            "300\n" +
            "#4DB6AC\n" +
            "400\n" +
            "#26A69A\n" +
            "500\n" +
            "#009688\n" +
            "600\n" +
            "#00897B\n" +
            "700\n" +
            "#00796B\n" +
            "800\n" +
            "#00695C\n" +
            "900\n" +
            "#004D40\n" +
            "A100\n" +
            "#A7FFEB\n" +
            "A200\n" +
            "#64FFDA\n" +
            "A400\n" +
            "#1DE9B6\n" +
            "A700\n" +
            "#00BFA5\n" +
            "Green 50\n" +
            "#E8F5E9\n" +
            "100\n" +
            "#C8E6C9\n" +
            "200\n" +
            "#A5D6A7\n" +
            "300\n" +
            "#81C784\n" +
            "400\n" +
            "#66BB6A\n" +
            "500\n" +
            "#4CAF50\n" +
            "600\n" +
            "#43A047\n" +
            "700\n" +
            "#388E3C\n" +
            "800\n" +
            "#2E7D32\n" +
            "900\n" +
            "#1B5E20\n" +
            "A100\n" +
            "#B9F6CA\n" +
            "A200\n" +
            "#69F0AE\n" +
            "A400\n" +
            "#00E676\n" +
            "A700\n" +
            "#00C853\n" +
            "Light Green 50\n" +
            "#F1F8E9\n" +
            "100\n" +
            "#DCEDC8\n" +
            "200\n" +
            "#C5E1A5\n" +
            "300\n" +
            "#AED581\n" +
            "400\n" +
            "#9CCC65\n" +
            "500\n" +
            "#8BC34A\n" +
            "600\n" +
            "#7CB342\n" +
            "700\n" +
            "#689F38\n" +
            "800\n" +
            "#558B2F\n" +
            "900\n" +
            "#33691E\n" +
            "A100\n" +
            "#CCFF90\n" +
            "A200\n" +
            "#B2FF59\n" +
            "A400\n" +
            "#76FF03\n" +
            "A700\n" +
            "#64DD17\n" +
            "Lime 50\n" +
            "#F9FBE7\n" +
            "100\n" +
            "#F0F4C3\n" +
            "200\n" +
            "#E6EE9C\n" +
            "300\n" +
            "#DCE775\n" +
            "400\n" +
            "#D4E157\n" +
            "500\n" +
            "#CDDC39\n" +
            "600\n" +
            "#C0CA33\n" +
            "700\n" +
            "#AFB42B\n" +
            "800\n" +
            "#9E9D24\n" +
            "900\n" +
            "#827717\n" +
            "A100\n" +
            "#F4FF81\n" +
            "A200\n" +
            "#EEFF41\n" +
            "A400\n" +
            "#C6FF00\n" +
            "A700\n" +
            "#AEEA00\n" +
            "Yellow 50\n" +
            "#FFFDE7\n" +
            "100\n" +
            "#FFF9C4\n" +
            "200\n" +
            "#FFF59D\n" +
            "300\n" +
            "#FFF176\n" +
            "400\n" +
            "#FFEE58\n" +
            "500\n" +
            "#FFEB3B\n" +
            "600\n" +
            "#FDD835\n" +
            "700\n" +
            "#FBC02D\n" +
            "800\n" +
            "#F9A825\n" +
            "900\n" +
            "#F57F17\n" +
            "A100\n" +
            "#FFFF8D\n" +
            "A200\n" +
            "#FFFF00\n" +
            "A400\n" +
            "#FFEA00\n" +
            "A700\n" +
            "#FFD600\n" +
            "Amber 50\n" +
            "#FFF8E1\n" +
            "100\n" +
            "#FFECB3\n" +
            "200\n" +
            "#FFE082\n" +
            "300\n" +
            "#FFD54F\n" +
            "400\n" +
            "#FFCA28\n" +
            "500\n" +
            "#FFC107\n" +
            "600\n" +
            "#FFB300\n" +
            "700\n" +
            "#FFA000\n" +
            "800\n" +
            "#FF8F00\n" +
            "900\n" +
            "#FF6F00\n" +
            "A100\n" +
            "#FFE57F\n" +
            "A200\n" +
            "#FFD740\n" +
            "A400\n" +
            "#FFC400\n" +
            "A700\n" +
            "#FFAB00\n" +
            "Orange 50\n" +
            "#FFF3E0\n" +
            "100\n" +
            "#FFE0B2\n" +
            "200\n" +
            "#FFCC80\n" +
            "300\n" +
            "#FFB74D\n" +
            "400\n" +
            "#FFA726\n" +
            "500\n" +
            "#FF9800\n" +
            "600\n" +
            "#FB8C00\n" +
            "700\n" +
            "#F57C00\n" +
            "800\n" +
            "#EF6C00\n" +
            "900\n" +
            "#E65100\n" +
            "A100\n" +
            "#FFD180\n" +
            "A200\n" +
            "#FFAB40\n" +
            "A400\n" +
            "#FF9100\n" +
            "A700\n" +
            "#FF6D00\n" +
            "Deep Orange 50\n" +
            "#FBE9E7\n" +
            "100\n" +
            "#FFCCBC\n" +
            "200\n" +
            "#FFAB91\n" +
            "300\n" +
            "#FF8A65\n" +
            "400\n" +
            "#FF7043\n" +
            "500\n" +
            "#FF5722\n" +
            "600\n" +
            "#F4511E\n" +
            "700\n" +
            "#E64A19\n" +
            "800\n" +
            "#D84315\n" +
            "900\n" +
            "#BF360C\n" +
            "A100\n" +
            "#FF9E80\n" +
            "A200\n" +
            "#FF6E40\n" +
            "A400\n" +
            "#FF3D00\n" +
            "A700\n" +
            "#DD2C00\n" +
            "Brown 50\n" +
            "#EFEBE9\n" +
            "100\n" +
            "#D7CCC8\n" +
            "200\n" +
            "#BCAAA4\n" +
            "300\n" +
            "#A1887F\n" +
            "400\n" +
            "#8D6E63\n" +
            "500\n" +
            "#795548\n" +
            "600\n" +
            "#6D4C41\n" +
            "700\n" +
            "#5D4037\n" +
            "800\n" +
            "#4E342E\n" +
            "900\n" +
            "#3E2723\n" +
            "Gray 50\n" +
            "#FAFAFA\n" +
            "100\n" +
            "#F5F5F5\n" +
            "200\n" +
            "#EEEEEE\n" +
            "300\n" +
            "#E0E0E0\n" +
            "400\n" +
            "#BDBDBD\n" +
            "500\n" +
            "#9E9E9E\n" +
            "600\n" +
            "#757575\n" +
            "700\n" +
            "#616161\n" +
            "800\n" +
            "#424242\n" +
            "900\n" +
            "#212121\n" +
            "Blue Gray 50\n" +
            "#ECEFF1\n" +
            "100\n" +
            "#CFD8DC\n" +
            "200\n" +
            "#B0BEC5\n" +
            "300\n" +
            "#90A4AE\n" +
            "400\n" +
            "#78909C\n" +
            "500\n" +
            "#607D8B\n" +
            "600\n" +
            "#546E7A\n" +
            "700\n" +
            "#455A64\n" +
            "800\n" +
            "#37474F\n" +
            "900\n" +
            "#263238";
}
