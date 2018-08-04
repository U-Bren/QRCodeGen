/*
 * Copyright (C) 2012 Stefan Ganzer
 *
 * This file is part of QRCodeGen.
 *
 * QRCodeGen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QRCodeGen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package qrcodegen.swing;

import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Component;
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
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.PrintJob;
import java.awt.Rectangle;
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
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Stefan Ganzer
 */
public class LocationSetterTest {

	private Toolkit toolkit;
	private Component c;
	private Component other;

	public LocationSetterTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		toolkit = new TestToolkit();
		c = new Component(){};
		other = new Component() {};
	}

	@After
	public void tearDown() {
		toolkit = null;
		c = null;
		other = null;
	}

	@Test
	public void constructorShouldCreateNewInstance() {
		c.setBounds(0, 0, 11, 7);
		LocationSetter ls = new LocationSetter(toolkit, c);
	}

	@Test
	public void shouldPositionThisComponentFromRightToOtherNextToOtherComponent() {
		final Rectangle otherRec = new Rectangle(0, 0, 100, 60);
		other.setBounds(otherRec);

		final Rectangle expRec = new Rectangle(100, 0, 100, 50);
		c.setBounds(400, 0, 100, 50);
		LocationSetter ls = new LocationSetter(toolkit, c);
		ls.setLocationNextTo(other, 1);
		assertThat(c.getBounds(), equalTo(expRec));
		assertThat(other.getBounds(), equalTo(otherRec));
	}

	@Test
	public void shouldPositionThisComponentFromSameToOtherNextToOtherComponent() {
		final Rectangle otherRec = new Rectangle(0, 0, 100, 60);
		other.setBounds(otherRec);
		
		final Rectangle expRec = new Rectangle(100, 0, 90, 50);
		c.setBounds(0, 0, 90, 50);
		LocationSetter ls = new LocationSetter(toolkit, c);
		ls.setLocationNextTo(other, 1);
		assertThat(c.getBounds(), equalTo(expRec));
		assertThat(other.getBounds(), equalTo(otherRec));
	}

	@Test
	public void shouldPositionThisComponentFromLeftToOtherNextToOtherComponent() {
		final Rectangle otherRec = new Rectangle(99, 0, 100, 60);
		other.setBounds(otherRec);
		
		final Rectangle expRec = new Rectangle(199, 0, 90, 50);
		c.setBounds(0, 0, 90, 50);
		LocationSetter ls = new LocationSetter(toolkit, c);
		ls.setLocationNextTo(other, 1);
		assertThat(c.getBounds(), equalTo(expRec));
		assertThat(other.getBounds(), equalTo(otherRec));
	}
	/**
	 * Test of setLocationNextTo method, of class LocationSetter.
	 */

	private static class TestToolkit extends Toolkit {

		//<editor-fold defaultstate="collapsed" desc="comment">
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
		protected ListPeer createList(List target) throws HeadlessException {
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
		//</editor-fold>

		@Override
		public Dimension getScreenSize() throws HeadlessException {
			return new Dimension(500, 200);
		}

		@Override
		public Insets getScreenInsets(GraphicsConfiguration gc) {
			return new Insets(5, 5, 5, 5);
		}
	}
}
