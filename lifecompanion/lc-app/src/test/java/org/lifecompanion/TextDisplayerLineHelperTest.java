/*******************************************************************************
 * Copyright (C) ForUSoftware
 * All Rights Reserved - 2016-2017
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Mathieu THEBAUD <math.thebaud@gmail.com>
 ******************************************************************************/
package org.lifecompanion;

import java.util.ArrayList;
import java.util.List;


import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lifecompanion.model.api.configurationcomponent.WriterEntryI;
import org.lifecompanion.model.api.style.TextCompStyleI;
import org.lifecompanion.model.api.textcomponent.TextBoundsProviderI;
import org.lifecompanion.model.api.textcomponent.TextDisplayerLineI;
import org.lifecompanion.model.impl.configurationcomponent.TextEditorComponent;
import org.lifecompanion.model.impl.configurationcomponent.WriterDisplayerComponentBaseImpl;
import org.lifecompanion.model.impl.configurationcomponent.WriterEntry;
import org.lifecompanion.model.impl.textcomponent.TextDisplayerLineHelper;
import org.predict4all.nlp.Separator;

import static org.junit.jupiter.api.Assertions.*;

public class TextDisplayerLineHelperTest {
//	protected static final double CHAR_WIDTH = 10.0, CHAR_HEIGHT = 20.0;
//	private TextBoundsProviderI boundProvider;
//	private WriterDisplayerComponentBaseImpl textDisplayer;
//
//	@BeforeEach
//	public void setUp() {
//		boundProvider = new TextBoundsProviderI() {
//			@Override
//			public Bounds getBounds(String text, TextCompStyleI textStyle) {
//				return new BoundingBox(0.0, 0.0, text != null ? text.length() * CHAR_WIDTH : 0, CHAR_HEIGHT);
//			}
//		};
//		textDisplayer = new TextEditorComponent();
//	}
//
//	@Test
//	public void testCreateSimpleLines() {
//		List<WriterEntryI> entries = createEntries("test, ceci", " ", "est", " un", " ", "test ", "va",null, "leur", "\nAutre");
//		List<TextDisplayerLineI> lines = TextDisplayerLineHelper.generateLines(entries, null);
//		assertEquals(2, lines.size());
//
//		// Check first line
//		TextDisplayerLineI firstLine = lines.get(0);
//		assertEquals(7, firstLine.getWords().size());
//		// "test,"
//		assertEquals(1, firstLine.getWords().get(0).getParts().size());
//		assertEquals("test", firstLine.getWords().get(0).getParts().get(0).getPart());
//		assertEquals(Separator.COMMA, Separator.getSeparatorFor(firstLine.getWords().get(0).getWordSeparatorChar()));
//		// " "
//		assertTrue(firstLine.getWords().get(1).getParts().isEmpty());
//		assertEquals(Separator.SPACE, Separator.getSeparatorFor(firstLine.getWords().get(1).getWordSeparatorChar()));
//		// "ceci "
//		assertEquals(1, firstLine.getWords().get(2).getParts().size());
//		assertEquals("ceci", firstLine.getWords().get(2).getParts().get(0).getPart());
//		assertEquals(Separator.SPACE, Separator.getSeparatorFor(firstLine.getWords().get(2).getWordSeparatorChar()));
//		// "est "
//		assertEquals(1, firstLine.getWords().get(3).getParts().size());
//		assertEquals("est", firstLine.getWords().get(3).getParts().get(0).getPart());
//		assertEquals(Separator.SPACE, Separator.getSeparatorFor(firstLine.getWords().get(3).getWordSeparatorChar()));
//		// "un "
//		assertEquals(1, firstLine.getWords().get(4).getParts().size());
//		assertEquals("un", firstLine.getWords().get(4).getParts().get(0).getPart());
//		assertEquals(Separator.SPACE, Separator.getSeparatorFor(firstLine.getWords().get(4).getWordSeparatorChar()));
//		// "test "
//		assertEquals(1, firstLine.getWords().get(5).getParts().size());
//		assertEquals("test", firstLine.getWords().get(5).getParts().get(0).getPart());
//		assertEquals(Separator.SPACE, Separator.getSeparatorFor(firstLine.getWords().get(5).getWordSeparatorChar()));
//		// "valeur\n"
//		assertEquals(2, firstLine.getWords().get(6).getParts().size());
//		assertEquals("va", firstLine.getWords().get(6).getParts().get(0).getPart());
//		assertEquals("leur", firstLine.getWords().get(6).getParts().get(1).getPart());
//		assertNotEquals(firstLine.getWords().get(6).getParts().get(0).getEntry(), firstLine.getWords().get(6).getParts().get(1).getEntry());
//		assertEquals(Separator.NEWLINE, Separator.getSeparatorFor(firstLine.getWords().get(6).getWordSeparatorChar()));
//
//		// Check second line
//		TextDisplayerLineI secondLine = lines.get(1);
//		assertEquals(1, secondLine.getWords().size());
//		assertEquals(1, secondLine.getWords().get(0).getParts().size());
//		assertEquals("Autre", secondLine.getWords().get(0).getParts().get(0).getPart());
//		assertNull(secondLine.getWords().get(0).getWordSeparatorChar());
//	}
//
//	@Test
//	public void testWordWrapSimple() {
//		List<WriterEntryI> entries = createEntries("test, ceci");
//		textDisplayer.getWriterEntries().addAll(entries);
//		List<TextDisplayerLineI> lines = TextDisplayerLineHelper.generateLines(textDisplayer, boundProvider, null, 65);
//		assertEquals(2, lines.size());
//
//		// First line
//		TextDisplayerLineI firstLine = lines.get(0);
//		assertEquals(2, firstLine.getWords().size());
//		assertEquals(1, firstLine.getWords().get(0).getParts().size());
//		assertEquals("test", firstLine.getWords().get(0).getParts().get(0).getPart());
//		assertEquals(Separator.COMMA, Separator.getSeparatorFor(firstLine.getWords().get(0).getWordSeparatorChar()));
//		assertEquals(0, firstLine.getWords().get(1).getParts().size());
//		assertEquals(Separator.SPACE, Separator.getSeparatorFor(firstLine.getWords().get(1).getWordSeparatorChar()));
//
//		// Second line
//		TextDisplayerLineI secondLine = lines.get(1);
//		assertEquals(1, secondLine.getWords().size());
//		assertEquals(1, secondLine.getWords().get(0).getParts().size());
//		assertEquals("ceci", secondLine.getWords().get(0).getParts().get(0).getPart());
//		assertNull(secondLine.getWords().get(0).getWordSeparatorChar());
//	}
//
//	@Test
//	public void testWordWrapSimpleShiftLine() {
//		List<WriterEntryI> entries = createEntries("test ce\nautre");
//		textDisplayer.getWriterEntries().addAll(entries);
//		List<TextDisplayerLineI> lines = TextDisplayerLineHelper.generateLines(textDisplayer, boundProvider, null, 55);
//		assertEquals(3, lines.size());
//
//		TextDisplayerLineI firstLine = lines.get(0);
//		assertEquals(1, firstLine.getWords().size());
//		assertEquals(1, firstLine.getWords().get(0).getParts().size());
//		assertEquals("test", firstLine.getWords().get(0).getParts().get(0).getPart());
//		assertEquals(Separator.SPACE, Separator.getSeparatorFor(firstLine.getWords().get(0).getWordSeparatorChar()));
//
//		TextDisplayerLineI secondLine = lines.get(1);
//		assertEquals(1, secondLine.getWords().size());
//		assertEquals(1, secondLine.getWords().get(0).getParts().size());
//		assertEquals("ce", secondLine.getWords().get(0).getParts().get(0).getPart());
//		assertEquals(Separator.NEWLINE, Separator.getSeparatorFor(secondLine.getWords().get(0).getWordSeparatorChar()));
//
//		TextDisplayerLineI thirdLine = lines.get(2);
//		assertEquals(1, thirdLine.getWords().size());
//		assertEquals(1, thirdLine.getWords().get(0).getParts().size());
//		assertEquals("autre", thirdLine.getWords().get(0).getParts().get(0).getPart());
//		assertNull(thirdLine.getWords().get(0).getWordSeparatorChar());
//	}
//
//	@Test
//	public void testWordWrapImpossibleOneLine() {
//		List<WriterEntryI> entries = createEntries("mot autre");
//		textDisplayer.getWriterEntries().addAll(entries);
//		List<TextDisplayerLineI> lines = TextDisplayerLineHelper.generateLines(textDisplayer, boundProvider, null, 45);
//		assertEquals(3, lines.size());
//
//		TextDisplayerLineI firstLine = lines.get(0);
//		assertEquals(1, firstLine.getWords().size());
//		assertEquals(1, firstLine.getWords().get(0).getParts().size());
//		assertEquals("mot", firstLine.getWords().get(0).getParts().get(0).getPart());
//		assertEquals(Separator.SPACE, Separator.getSeparatorFor(firstLine.getWords().get(0).getWordSeparatorChar()));
//
//		TextDisplayerLineI secondLine = lines.get(1);
//		assertEquals(1, secondLine.getWords().size());
//		assertEquals(1, secondLine.getWords().get(0).getParts().size());
//		assertEquals("autr", secondLine.getWords().get(0).getParts().get(0).getPart());
//		assertNull(secondLine.getWords().get(0).getWordSeparatorChar());
//
//		TextDisplayerLineI thirdLine = lines.get(2);
//		assertEquals(1, thirdLine.getWords().size());
//		assertEquals(1, thirdLine.getWords().get(0).getParts().size());
//		assertEquals("e", thirdLine.getWords().get(0).getParts().get(0).getPart());
//		assertNull(thirdLine.getWords().get(0).getWordSeparatorChar());
//	}
//
//	@Test
//	public void testWordWrapImpossibleOneLineShift() {
//		List<WriterEntryI> entries = createEntries("mot autre\naft");
//		textDisplayer.getWriterEntries().addAll(entries);
//		List<TextDisplayerLineI> lines = TextDisplayerLineHelper.generateLines(textDisplayer, boundProvider, null, 45);
//		assertEquals(4, lines.size());
//
//		TextDisplayerLineI firstLine = lines.get(0);
//		assertEquals(1, firstLine.getWords().size());
//		assertEquals(1, firstLine.getWords().get(0).getParts().size());
//		assertEquals("mot", firstLine.getWords().get(0).getParts().get(0).getPart());
//		assertEquals(Separator.SPACE, Separator.getSeparatorFor(firstLine.getWords().get(0).getWordSeparatorChar()));
//
//		TextDisplayerLineI secondLine = lines.get(1);
//		assertEquals(1, secondLine.getWords().size());
//		assertEquals(1, secondLine.getWords().get(0).getParts().size());
//		assertEquals("autr", secondLine.getWords().get(0).getParts().get(0).getPart());
//		assertNull(secondLine.getWords().get(0).getWordSeparatorChar());
//
//		TextDisplayerLineI thirdLine = lines.get(2);
//		assertEquals(1, thirdLine.getWords().size());
//		assertEquals(1, thirdLine.getWords().get(0).getParts().size());
//		assertEquals("e", thirdLine.getWords().get(0).getParts().get(0).getPart());
//		assertEquals(Separator.NEWLINE, Separator.getSeparatorFor(thirdLine.getWords().get(0).getWordSeparatorChar()));
//
//		TextDisplayerLineI fourthLine = lines.get(3);
//		assertEquals(1, fourthLine.getWords().size());
//		assertEquals(1, fourthLine.getWords().get(0).getParts().size());
//		assertEquals("aft", fourthLine.getWords().get(0).getParts().get(0).getPart());
//		assertNull(fourthLine.getWords().get(0).getWordSeparatorChar());
//	}
//
//	@Test
//	public void testWrapImpossibleWidthTooSmall() {
//		List<WriterEntryI> entries = createEntries("test");
//		textDisplayer.getWriterEntries().addAll(entries);
//		List<TextDisplayerLineI> lines = TextDisplayerLineHelper.generateLines(textDisplayer, boundProvider, null, 8);
//		assertEquals(4, lines.size());
//
//		TextDisplayerLineI firstLine = lines.get(0);
//		assertEquals(1, firstLine.getWords().size());
//		assertEquals(1, firstLine.getWords().get(0).getParts().size());
//		assertEquals("t", firstLine.getWords().get(0).getParts().get(0).getPart());
//		assertNull(firstLine.getWords().get(0).getWordSeparatorChar());
//
//		TextDisplayerLineI secondLine = lines.get(1);
//		assertEquals(1, secondLine.getWords().size());
//		assertEquals(1, secondLine.getWords().get(0).getParts().size());
//		assertEquals("e", secondLine.getWords().get(0).getParts().get(0).getPart());
//		assertNull(secondLine.getWords().get(0).getWordSeparatorChar());
//
//		TextDisplayerLineI thirdLine = lines.get(2);
//		assertEquals(1, thirdLine.getWords().size());
//		assertEquals(1, thirdLine.getWords().get(0).getParts().size());
//		assertEquals("s", thirdLine.getWords().get(0).getParts().get(0).getPart());
//		assertNull(thirdLine.getWords().get(0).getWordSeparatorChar());
//
//		TextDisplayerLineI fourthLine = lines.get(3);
//		assertEquals(1, fourthLine.getWords().size());
//		assertEquals(1, fourthLine.getWords().get(0).getParts().size());
//		assertEquals("t", fourthLine.getWords().get(0).getParts().get(0).getPart());
//		assertNull(fourthLine.getWords().get(0).getWordSeparatorChar());
//	}
//
//	@Test
//	public void testWordWrapToMultipleLine() {
//		List<WriterEntryI> entries = createEntries("123456");
//		textDisplayer.getWriterEntries().addAll(entries);
//		List<TextDisplayerLineI> lines = TextDisplayerLineHelper.generateLines(textDisplayer, boundProvider, null, 25);
//		assertEquals(3, lines.size());
//
//		TextDisplayerLineI firstLine = lines.get(0);
//		assertEquals(1, firstLine.getWords().size());
//		assertEquals(1, firstLine.getWords().get(0).getParts().size());
//		assertEquals("12", firstLine.getWords().get(0).getParts().get(0).getPart());
//		assertNull(firstLine.getWords().get(0).getWordSeparatorChar());
//
//		TextDisplayerLineI secondLine = lines.get(1);
//		assertEquals(1, secondLine.getWords().size());
//		assertEquals(1, secondLine.getWords().get(0).getParts().size());
//		assertEquals("34", secondLine.getWords().get(0).getParts().get(0).getPart());
//		assertNull(secondLine.getWords().get(0).getWordSeparatorChar());
//
//		TextDisplayerLineI thirdLine = lines.get(2);
//		assertEquals(1, thirdLine.getWords().size());
//		assertEquals(1, thirdLine.getWords().get(0).getParts().size());
//		assertEquals("56", thirdLine.getWords().get(0).getParts().get(0).getPart());
//		assertNull(thirdLine.getWords().get(0).getWordSeparatorChar());
//	}
//
//	@Test
//	public void testWordWrapMultipleEntriesSameWord() {
//		// Test word on diff entries
//		List<WriterEntryI> entries = createEntries("123", "456");
//
//		textDisplayer.getWriterEntries().addAll(entries);
//		List<TextDisplayerLineI> lines = TextDisplayerLineHelper.generateLines(textDisplayer, boundProvider, null, 25);
//		assertEquals(3, lines.size());
//
//		TextDisplayerLineI firstLine = lines.get(0);
//		assertEquals(1, firstLine.getWords().size());
//		assertEquals(1, firstLine.getWords().get(0).getParts().size());
//		assertEquals("12", firstLine.getWords().get(0).getParts().get(0).getPart());
//		assertNull(firstLine.getWords().get(0).getWordSeparatorChar());
//
//		TextDisplayerLineI secondLine = lines.get(1);
//		assertEquals(1, secondLine.getWords().size());
//		assertEquals(2, secondLine.getWords().get(0).getParts().size());
//		assertEquals("3", secondLine.getWords().get(0).getParts().get(0).getPart());
//		assertEquals("4", secondLine.getWords().get(0).getParts().get(1).getPart());
//		assertNull(secondLine.getWords().get(0).getWordSeparatorChar());
//
//		TextDisplayerLineI thirdLine = lines.get(2);
//		assertEquals(1, thirdLine.getWords().size());
//		assertEquals(1, thirdLine.getWords().get(0).getParts().size());
//		assertEquals("56", thirdLine.getWords().get(0).getParts().get(0).getPart());
//		assertNull(thirdLine.getWords().get(0).getWordSeparatorChar());
//	}
//
//	@Test
//	public void testWordWrapMultipleEntriesSameWordLines() {
//		// Test word on diff entries
//		List<WriterEntryI> entries = createEntries("12", "34567", "89");
//
//		textDisplayer.getWriterEntries().addAll(entries);
//		List<TextDisplayerLineI> lines = TextDisplayerLineHelper.generateLines(textDisplayer, boundProvider, null, 35);
//		assertEquals(3, lines.size());
//
//		TextDisplayerLineI firstLine = lines.get(0);
//		assertEquals(1, firstLine.getWords().size());
//		assertEquals(2, firstLine.getWords().get(0).getParts().size());
//		assertEquals("12", firstLine.getWords().get(0).getParts().get(0).getPart());
//		assertEquals("3", firstLine.getWords().get(0).getParts().get(1).getPart());
//		assertNull(firstLine.getWords().get(0).getWordSeparatorChar());
//
//		TextDisplayerLineI secondLine = lines.get(1);
//		assertEquals(1, secondLine.getWords().size());
//		assertEquals(1, secondLine.getWords().get(0).getParts().size());
//		assertEquals("456", secondLine.getWords().get(0).getParts().get(0).getPart());
//		assertNull(secondLine.getWords().get(0).getWordSeparatorChar());
//
//		TextDisplayerLineI thirdLine = lines.get(2);
//		assertEquals(1, thirdLine.getWords().size());
//		assertEquals(2, thirdLine.getWords().get(0).getParts().size());
//		assertEquals("7", thirdLine.getWords().get(0).getParts().get(0).getPart());
//		assertEquals("89", thirdLine.getWords().get(0).getParts().get(1).getPart());
//		assertNull(thirdLine.getWords().get(0).getWordSeparatorChar());
//	}
//
//	@Test
//	public void testWordWrapMultipleEntriesMultipleWordLines() {
//		// Test word on diff entries
//		List<WriterEntryI> entries = createEntries("12 ", "3456 7", "8");
//
//		textDisplayer.getWriterEntries().addAll(entries);
//		List<TextDisplayerLineI> lines = TextDisplayerLineHelper.generateLines(textDisplayer, boundProvider, null, 35);
//		assertEquals(4, lines.size());
//
//		TextDisplayerLineI firstLine = lines.get(0);
//		assertEquals(1, firstLine.getWords().size());
//		assertEquals(1, firstLine.getWords().get(0).getParts().size());
//		assertEquals("12", firstLine.getWords().get(0).getParts().get(0).getPart());
//		assertEquals(Separator.SPACE, Separator.getSeparatorFor(firstLine.getWords().get(0).getWordSeparatorChar()));
//
//		TextDisplayerLineI secondLine = lines.get(1);
//		assertEquals(1, secondLine.getWords().size());
//		assertEquals(1, secondLine.getWords().get(0).getParts().size());
//		assertEquals("345", secondLine.getWords().get(0).getParts().get(0).getPart());
//		assertNull(secondLine.getWords().get(0).getWordSeparatorChar());
//
//		TextDisplayerLineI thirdLine = lines.get(2);
//		assertEquals(1, thirdLine.getWords().size());
//		assertEquals(1, thirdLine.getWords().get(0).getParts().size());
//		assertEquals("6", thirdLine.getWords().get(0).getParts().get(0).getPart());
//		assertEquals(Separator.SPACE, Separator.getSeparatorFor(thirdLine.getWords().get(0).getWordSeparatorChar()));
//
//		TextDisplayerLineI fourthLine = lines.get(3);
//		assertEquals(1, fourthLine.getWords().size());
//		assertEquals(2, fourthLine.getWords().get(0).getParts().size());
//		assertEquals("7", fourthLine.getWords().get(0).getParts().get(0).getPart());
//		assertEquals("8", fourthLine.getWords().get(0).getParts().get(1).getPart());
//		assertNull(fourthLine.getWords().get(0).getWordSeparatorChar());
//	}
//
//	@Test
//	public void testDoubleLine() {
//		List<WriterEntryI> entries = createEntries("a\n\n\nb");
//		textDisplayer.getWriterEntries().addAll(entries);
//		List<TextDisplayerLineI> lines = TextDisplayerLineHelper.generateLines(textDisplayer, boundProvider, null, 35);
//		assertEquals(4, lines.size());
//
//		assertEquals(1, lines.get(0).getWords().size());
//		assertEquals(1, lines.get(0).getWords().get(0).getParts().size());
//		assertEquals("a", lines.get(0).getWords().get(0).getParts().get(0).getPart());
//		assertEquals(Separator.NEWLINE, Separator.getSeparatorFor(lines.get(0).getWords().get(0).getWordSeparatorChar()));
//
//		assertEquals(1, lines.get(1).getWords().size());
//		assertEquals(0, lines.get(1).getWords().get(0).getParts().size());
//		assertEquals(Separator.NEWLINE, Separator.getSeparatorFor(lines.get(1).getWords().get(0).getWordSeparatorChar()));
//
//		assertEquals(1, lines.get(2).getWords().size());
//		assertEquals(0, lines.get(2).getWords().get(0).getParts().size());
//		assertEquals(Separator.NEWLINE, Separator.getSeparatorFor(lines.get(2).getWords().get(0).getWordSeparatorChar()));
//
//		assertEquals(1, lines.get(3).getWords().size());
//		assertEquals(1, lines.get(3).getWords().get(0).getParts().size());
//		assertEquals("b", lines.get(3).getWords().get(0).getParts().get(0).getPart());
//		assertNull(lines.get(3).getWords().get(0).getWordSeparatorChar());
//	}
//
//	@Test
//	public void testWrapLineTooSmallOneCharPerEntry() {
//		List<WriterEntryI> entries = createEntries("a", "b", "c", "d", "e");
//		textDisplayer.getWriterEntries().addAll(entries);
//		List<TextDisplayerLineI> lines = TextDisplayerLineHelper.generateLines(textDisplayer, boundProvider, null, 35);
//		assertEquals(2, lines.size());
//
//		assertEquals(1, lines.get(0).getWords().size());
//		assertEquals(3, lines.get(0).getWords().get(0).getParts().size());
//		assertEquals("a", lines.get(0).getWords().get(0).getParts().get(0).getPart());
//		assertEquals("b", lines.get(0).getWords().get(0).getParts().get(1).getPart());
//		assertEquals("c", lines.get(0).getWords().get(0).getParts().get(2).getPart());
//
//		assertEquals(1, lines.get(1).getWords().size());
//		assertEquals(2, lines.get(1).getWords().get(0).getParts().size());
//		assertEquals("d", lines.get(1).getWords().get(0).getParts().get(0).getPart());
//		assertEquals("e", lines.get(1).getWords().get(0).getParts().get(1).getPart());
//	}
//
//	@Test
//	public void testSplitOnSeparatorEndOfWord() {
//		List<WriterEntryI> entries = createEntries("test mot");
//		textDisplayer.getWriterEntries().addAll(entries);
//		List<TextDisplayerLineI> lines = TextDisplayerLineHelper.generateLines(textDisplayer, boundProvider, null, 45);
//		assertEquals(2, lines.size());
//
//		assertEquals(1, lines.get(0).getWords().size());
//		assertEquals(1, lines.get(0).getWords().get(0).getParts().size());
//		assertEquals("test", lines.get(0).getWords().get(0).getParts().get(0).getPart());
//		assertNull(lines.get(0).getWords().get(0).getWordSeparatorChar());
//
//		assertEquals(2, lines.get(1).getWords().size());
//		assertEquals(0, lines.get(1).getWords().get(0).getParts().size());
//		assertEquals(Separator.SPACE, Separator.getSeparatorFor(lines.get(1).getWords().get(0).getWordSeparatorChar()));
//		assertEquals(1, lines.get(1).getWords().get(1).getParts().size());
//		assertEquals("mot", lines.get(1).getWords().get(1).getParts().get(0).getPart());
//	}
//
//	private List<WriterEntryI> createEntries(String... vals) {
//		List<WriterEntryI> entries = new ArrayList<>();
//		for (String val : vals) {
//			entries.add(new WriterEntry(val, false));
//		}
//		return entries;
//	}
}
