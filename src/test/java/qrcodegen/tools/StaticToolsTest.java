/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qrcodegen.tools;

import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.PrintJob;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.awt.im.InputMethodHighlight;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CanvasPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.DesktopPeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.PanelPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.WindowPeer;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.KeyStroke;
import javax.swing.MutableComboBoxModel;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Rule;

/**
 *
 * @author Stefan Ganzer
 */
public class StaticToolsTest {

	private StaticTools tools;

	public StaticToolsTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
		tools = null;
	}

	/**
	 * Test of getKeyCodeForString method, of class StaticTools.
	 */
	/**
	 * Test of getExtension method, of class StaticTools.
	 */
	@Test
	public void testGetExtension() {
		System.out.println("getExtension");
		File f = new File("text.txt");
		String expResult = "txt";
		String result = StaticTools.getExtension(f);
		assertEquals(expResult, result);

		f = new File("text.TXT");
		expResult = "txt";
		result = StaticTools.getExtension(f);
		assertEquals(expResult, result);

		f = new File("");
		expResult = "";
		result = StaticTools.getExtension(f);
		assertEquals(expResult, result);

		f = new File("file w/o extension");
		expResult = "";
		result = StaticTools.getExtension(f);
		assertEquals(expResult, result);

		f = new File("test.txt.Gz");
		expResult = "gz";
		result = StaticTools.getExtension(f);
		assertEquals(expResult, result);


	}

	/**
	 * Test of getFileNameWOExtension method, of class StaticTools.
	 */
	@Test
	public void testGetFileNameWOExtension() {
		System.out.println("getFileNameWOExtension");

		String input = "C:\\home\\test.txt";
		String expResult = "test";
		File f = new File(input);

		String result = StaticTools.getFileNameWOExtension(f);
		assertEquals(expResult, result);

		input = "test.txt";
		expResult = "test";
		f = new File(input);
		result = StaticTools.getFileNameWOExtension(f);
		assertEquals(expResult, result);

		input = ".txt";
		expResult = "";
		f = new File(input);
		result = StaticTools.getFileNameWOExtension(f);
		assertEquals(expResult, result);

		input = "txt";
		expResult = "txt";
		f = new File(input);
		result = StaticTools.getFileNameWOExtension(f);
		assertEquals(expResult, result);

		input = ".txt";
		expResult = "";
		f = new File(input);
		result = StaticTools.getFileNameWOExtension(f);
		assertEquals(expResult, result);

		input = ".gif.jpg";
		expResult = ".gif";
		f = new File(input);
		result = StaticTools.getFileNameWOExtension(f);
		assertEquals(expResult, result);

		input = ".";
		expResult = "";
		f = new File(input);
		result = StaticTools.getFileNameWOExtension(f);
		assertEquals(expResult, result);

	}

	/**
	 * Test of getFileNameWOExtension method, of class StaticTools.
	 */
	@Test(expected = NullPointerException.class)
	public void testGetFileNameWOExtensionNullInput() {
		System.out.println("getFileNameWOExtensionNullInput");

		StaticTools.getFileNameWOExtension(null);
	}

	@Test
	public void testGetFileNameWOExtensionEmptyFile() {
		System.out.println("getFileNameWOExtensionEmptyFile");
		String input = "";
		String expResult = "";
		File f = new File(input);
		String result = StaticTools.getFileNameWOExtension(f);
		assertEquals(expResult, result);

	}

	/**
	 * Test of clipString method, of class StaticTools.
	 */
	@Test
	public void testClipString() {
		System.out.println("clipString");
		String s = "a test string";
		int maxLength = 6;
		String expResult = "a test";
		String result = StaticTools.clipString(s, maxLength);
		assertEquals(expResult, result);
	}

	/**
	 * Test of clipString method, of class StaticTools.
	 */
	@Test
	public void testClipStringSameResult() {
		System.out.println("clipStringSameResult");
		String input = "a test";
		int maxLength = 6;
		String result = StaticTools.clipString(input, maxLength);
		assertSame(input, result);

		maxLength = -1;
		result = StaticTools.clipString(input, maxLength);
		assertSame(input, result);
	}

	/**
	 * Test of clipString method, of class StaticTools.
	 */
	@Test
	public void testClipStringNullInput() {
		System.out.println("clipStringNullInput");

		String input = null;
		int maxLength = 10;
		String result = StaticTools.clipString(input, maxLength);
		assertSame(input, result);
	}

	/**
	 * Test of clipString method, of class StaticTools.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testClipStringIllegalMaxLength() {
		System.out.println("clipStringIllegalMaxLength");

		String s = "a test string";
		int maxLength = -2;
		StaticTools.clipString(s, maxLength);
	}

	@Test
	public void testInsertElementInRightOrder() {
		doTestInsertElementInRightOrder(new DefaultComboBoxModel(), new Number[]{1, 3, 5}, new Number[]{6, 4, 0}, new Number[]{0, 1, 3, 4, 5, 6});
		doTestInsertElementInRightOrder(new DefaultComboBoxModel(), new Number[]{}, new Number[]{}, new Number[]{});
		doTestInsertElementInRightOrder(new DefaultComboBoxModel(), new Number[]{-1, 0, 1}, new Number[]{2, -3, 4}, new Number[]{-3, -1, 0, 1, 2, 4});
		doTestInsertElementInRightOrder(new DefaultComboBoxModel(), new Number[]{}, new Number[]{4, 0, 6, 5}, new Number[]{0, 4, 5, 6});
		doTestInsertElementInRightOrder(new DefaultComboBoxModel(), new Number[]{1, 3, 5}, new Number[]{}, new Number[]{1, 3, 5});
		doTestInsertElementInRightOrder(new DefaultComboBoxModel(), new Number[]{1, 3, 5}, new Number[]{6, 4, 0}, new Number[]{0, 1, 3, 4, 5, 6});
		doTestInsertElementInRightOrder(new DefaultComboBoxModel(), new Number[]{1, 3, 5}, new Number[]{1, 3, 5}, new Number[]{1, 1, 3, 3, 5, 5});
	}

	private static void doTestInsertElementInRightOrder(MutableComboBoxModel model, Number[] numbersBefore, Number[] numbersToAdd, Number[] expectedResult) {
		for (Number n : numbersBefore) {
			model.addElement(n);
		}
		checkOrder(model);
		for (Number n : numbersToAdd) {
			SwingTools.insertElementInRightOrder(model, n);
		}
		checkOrder(expectedResult, model);
	}

	private static void checkOrder(ComboBoxModel model) {
		Number previousNumber = null;
		for (int i = 0; i < model.getSize(); i++) {
			Number currentNumber = (Number) model.getElementAt(i);
			if (previousNumber == null) {
				previousNumber = currentNumber;
			} else {
				assertTrue(previousNumber.doubleValue() <= currentNumber.doubleValue());
				previousNumber = currentNumber;
			}
		}
	}

	private static void checkOrder(Number[] expected, ComboBoxModel model) {
		for (int i = 0; i < model.getSize(); i++) {
			Number currentNumber = (Number) model.getElementAt(i);
			System.out.println(currentNumber);
			assertEquals(expected[i].doubleValue(), currentNumber.doubleValue(), expected[i].doubleValue() / 1000000);
		}
	}

	@Test
	public void getPlatformSpecificKeyStrokeForStringShouldReturnMetaKeyUnderMacOS() {
		Toolkit toolkit = new MacToolkit();
		String osName = "MacOSX";
		String input = "control shift S";
		String expectedInput = "meta shift S";
		tools = new StaticTools(toolkit, osName);
		KeyStroke result = tools.getPlatformSpecificKeyStrokeForString(input);
		KeyStroke expectedResult = KeyStroke.getKeyStroke(expectedInput);
		assertThat(result, equalTo(expectedResult));
	}

	@Test
	public void getPlatformSpecificKeyStrokeForStringShouldReturnCtrlKeyUnderWindows() {
		Toolkit toolkit = new WinToolkit();
		String osName = "WindowsXP";
		String input = "control shift S";
		String expectedInput = input;
		tools = new StaticTools(toolkit, osName);
		KeyStroke result = tools.getPlatformSpecificKeyStrokeForString(input);
		KeyStroke expectedResult = KeyStroke.getKeyStroke(expectedInput);
		assertThat(result, equalTo(expectedResult));
	}

	@Test
	public void getPlatformSpecificKeyStrokeForStringShouldReturnCtrlKeyUnderLinux() {
		Toolkit toolkit = new WinToolkit();
		String osName = "Linux";
		String input = "control shift S";
		String expectedInput = input;
		tools = new StaticTools(toolkit, osName);
		KeyStroke result = tools.getPlatformSpecificKeyStrokeForString(input);
		KeyStroke expectedResult = KeyStroke.getKeyStroke(expectedInput);
		assertThat(result, equalTo(expectedResult));
	}

	private static class WinToolkit extends Toolkit {

		@Override
		protected DesktopPeer createDesktopPeer(Desktop target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected ButtonPeer createButton(Button target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected TextFieldPeer createTextField(TextField target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected LabelPeer createLabel(Label target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected ListPeer createList(java.awt.List target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected CheckboxPeer createCheckbox(Checkbox target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected ScrollbarPeer createScrollbar(Scrollbar target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected ScrollPanePeer createScrollPane(ScrollPane target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected TextAreaPeer createTextArea(TextArea target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected ChoicePeer createChoice(Choice target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected FramePeer createFrame(Frame target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected CanvasPeer createCanvas(Canvas target) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected PanelPeer createPanel(Panel target) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected WindowPeer createWindow(Window target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected DialogPeer createDialog(Dialog target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected MenuBarPeer createMenuBar(MenuBar target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected MenuPeer createMenu(Menu target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected PopupMenuPeer createPopupMenu(PopupMenu target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected MenuItemPeer createMenuItem(MenuItem target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected FileDialogPeer createFileDialog(FileDialog target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected FontPeer getFontPeer(String name, int style) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Dimension getScreenSize() throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public int getScreenResolution() throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public ColorModel getColorModel() throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String[] getFontList() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public FontMetrics getFontMetrics(Font font) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void sync() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Image getImage(String filename) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Image getImage(URL url) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Image createImage(String filename) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Image createImage(URL url) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean prepareImage(Image image, int width, int height, ImageObserver observer) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public int checkImage(Image image, int width, int height, ImageObserver observer) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Image createImage(ImageProducer producer) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Image createImage(byte[] imagedata, int imageoffset, int imagelength) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public PrintJob getPrintJob(Frame frame, String jobtitle, Properties props) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void beep() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Clipboard getSystemClipboard() throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected EventQueue getSystemEventQueueImpl() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent dge) throws InvalidDnDOperationException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean isModalityTypeSupported(ModalityType modalityType) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean isModalExclusionTypeSupported(ModalExclusionType modalExclusionType) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Map<TextAttribute, ?> mapInputMethodHighlight(InputMethodHighlight highlight) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public int getMenuShortcutKeyMask() {
			return KeyEvent.CTRL_MASK;
		}
	}

	private static class MacToolkit extends Toolkit {

		@Override
		protected DesktopPeer createDesktopPeer(Desktop target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected ButtonPeer createButton(Button target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected TextFieldPeer createTextField(TextField target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected LabelPeer createLabel(Label target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected ListPeer createList(java.awt.List target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected CheckboxPeer createCheckbox(Checkbox target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected ScrollbarPeer createScrollbar(Scrollbar target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected ScrollPanePeer createScrollPane(ScrollPane target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected TextAreaPeer createTextArea(TextArea target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected ChoicePeer createChoice(Choice target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected FramePeer createFrame(Frame target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected CanvasPeer createCanvas(Canvas target) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected PanelPeer createPanel(Panel target) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected WindowPeer createWindow(Window target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected DialogPeer createDialog(Dialog target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected MenuBarPeer createMenuBar(MenuBar target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected MenuPeer createMenu(Menu target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected PopupMenuPeer createPopupMenu(PopupMenu target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected MenuItemPeer createMenuItem(MenuItem target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected FileDialogPeer createFileDialog(FileDialog target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem target) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected FontPeer getFontPeer(String name, int style) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Dimension getScreenSize() throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public int getScreenResolution() throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public ColorModel getColorModel() throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String[] getFontList() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public FontMetrics getFontMetrics(Font font) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void sync() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Image getImage(String filename) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Image getImage(URL url) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Image createImage(String filename) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Image createImage(URL url) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean prepareImage(Image image, int width, int height, ImageObserver observer) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public int checkImage(Image image, int width, int height, ImageObserver observer) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Image createImage(ImageProducer producer) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Image createImage(byte[] imagedata, int imageoffset, int imagelength) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public PrintJob getPrintJob(Frame frame, String jobtitle, Properties props) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void beep() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Clipboard getSystemClipboard() throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		protected EventQueue getSystemEventQueueImpl() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent dge) throws InvalidDnDOperationException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean isModalityTypeSupported(ModalityType modalityType) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean isModalExclusionTypeSupported(ModalExclusionType modalExclusionType) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Map<TextAttribute, ?> mapInputMethodHighlight(InputMethodHighlight highlight) throws HeadlessException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public int getMenuShortcutKeyMask() {
			return KeyEvent.META_MASK;
		}
	}

	@Test
	public void testBothNullOrEqual() {
		Object a = new Object();
		Object b = new Object();

		bothNullOrEqualTest(null, null, true);
		bothNullOrEqualTest(null, b, false);
		bothNullOrEqualTest(b, null, false);
		bothNullOrEqualTest(a, b, false);
		bothNullOrEqualTest(b, a, false);
		bothNullOrEqualTest(a, a, true);
		bothNullOrEqualTest(b, b, true);
	}

	private static void bothNullOrEqualTest(Object a, Object b, boolean expectedResult) {
		boolean actualResult = StaticTools.bothNullOrEqual(a, b);
		assertThat(actualResult, equalTo(expectedResult));
	}
}
