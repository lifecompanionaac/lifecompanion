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

package org.lifecompanion.config.view.pane.general.view.simplercomp.keylist;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.lifecompanion.api.component.definition.simplercomp.KeyListNodeI;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.config.view.pane.general.view.simplercomp.CommonListViewActionContainer;
import org.lifecompanion.config.view.pane.general.view.simplercomp.DetailledSimplerKeyContentContainerListCell;

public class DetailledKeyListContentListCell extends DetailledSimplerKeyContentContainerListCell<KeyListNodeI> {
    private static final int GLYPH_SIZE = 12;
    private final Glyph listGlyph, keyGlyph, linkGlyph;


    public DetailledKeyListContentListCell(CommonListViewActionContainer<KeyListNodeI> commonListViewActionContainer) {
        super(commonListViewActionContainer);

        listGlyph = LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.FOLDER).size(GLYPH_SIZE).color(LCGraphicStyle.LC_GRAY);
        keyGlyph = LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.PICTURE_ALT).size(GLYPH_SIZE).color(LCGraphicStyle.LC_GRAY);
        linkGlyph = LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.LINK).size(GLYPH_SIZE).color(LCGraphicStyle.LC_GRAY);
    }

    @Override
    protected void updateItem(KeyListNodeI itemP, boolean emptyP) {
        super.updateItem(itemP, emptyP);
        fillerPane.getChildren().clear();
        if (itemP != null && !emptyP) {
            fillerPane.getChildren().add(itemP.isLinkNode() ? linkGlyph : itemP.isLeafNode() ? keyGlyph : listGlyph);
        }

    }
}
