/*
 Copyright 2011, 2012, 2013 Stefan Ganzer

 This file is part of QRCodeGen.

 QRCodeGen is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 QRCodeGen is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package qrcodegen;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.decoder.Mode;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;
import qrcodegen.qrcode.QRCodeFactory;
import qrcodegen.qrcode.QRCodeInterface;
import qrcodegen.qrcode.renderer.FixedSizeRenderer;
import qrcodegen.qrcode.renderer.ModuleSizeRenderer;
import qrcodegen.qrcode.renderer.OptimalSizeRenderer;
import qrcodegen.qrcode.renderer.Renderable;
import qrcodegen.tools.ImmutableDimension;
import qrcodegen.tools.StaticTools;
import qrcodegen.tools.TriState;

/**
 * Creates QR Codes from objects implementing the {@link qrcodegen.Encodable}
 * interface.
 *
 * @author Stefan Ganzer
 */
public final class QRCodeGenerator {

	public static final String REQUESTED_DIMENSION_PROPERTY = Renderable.REQUESTED_DIMENSION_PROPERTY;
	public static final String MODULE_SIZE_PROPERTY = Renderable.MODULE_SIZE_PROPERTY;
	public static final String RESULT_PROPERTY = Renderable.RESULT_STATE_PROPERTY;
	public static final String EXCEEDS_REQUESTED_DIMENSION_PROPERTY = Renderable.EXCEEDS_REQUESTED_DIMENSION_PROPERTY;
	public static final String ACTUAL_DIMENSION_PROPERTY = Renderable.ACTUAL_DIMENSION_PROPERTY;
	public static final String MODUS_PROPERTY = "Modus";
	public static final String VERSION_PROPERTY = "Version";
	public static final String CONTENT_PROPERTY = "Content";
	public static final String CHARACTER_ENCODING_PROPERTY = "CharacterEncoding";
	public static final String ERROR_CORRECTION_LEVEL_PROPERTY = "ErrorCorrectionLevel";
	public static final String WAS_ENCODABLE_PROPERTY = "Encodable";
	public static final String WAS_ASCII_ONLY_PROPERTY = "ASCII";
	/** The default Charset this encoder uses. */
	private static final Charset DEFAULT_CHARACTER_SET = Charset.forName("ISO-8859-1");
	private static final ErrorCorrectionLevel DEFAULT_ERROR_CORRECTION_LEVEL = ErrorCorrectionLevel.L;
	private static final EncodeHintType ERROR_CORRECTION_HINT = EncodeHintType.ERROR_CORRECTION;
	private static final EncodeHintType CHARACTER_SET_HINT = EncodeHintType.CHARACTER_SET;
	private static final ImmutableDimension MAX_IMAGE_SIZE = new ImmutableDimension(800, 800);
	private final CharsetEncoder ASCII_ENCODER = Charset.forName("ASCII").newEncoder();
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private final Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
	private final Object privateLock = new Object();
	private final ImmutableDimension maxImageSize;
	private final PropertyChangeListener rendererListener;
	private QRCode code;
	/** The requested dimension of the qr code. This is only the size of the
	 * image if Modus.FIXED_SIZE */
	private ImmutableDimension dimension = BarCodeSize.LARGE.getImmutableDimension();
	/** The modus this QRCodeGenerator works in */
	private Modus modus;
	/** The currently set charset, or null if none is set. */
	private Charset currentCharset = DEFAULT_CHARACTER_SET;
	/** True if the last invocation of generate() showed that the content was
	 * encodable by the current character encoding, false or not_applicable
	 * otherwise. */
	private TriState wasEncodable = TriState.NOT_APPLICABLE;
	private TriState wasAscii = TriState.NOT_APPLICABLE;
	private Renderable renderer;
	private QRCodeInterface qrCodeInterface;
	private String content;

	public enum Modus {

		FIXED_SIZE(ResourceBundle.getBundle("qrcodegen/QRCodeGenerator").getString("FIXED_SIZE")),
		BEST_FIT(ResourceBundle.getBundle("qrcodegen/QRCodeGenerator").getString("BEST_FIT")),
		MODULE_SIZE(ResourceBundle.getBundle("qrcodegen/QRCodeGenerator").getString("MODULE_SIZE")),;
		private final String displayName;

		private Modus(String displayName) {
			assert displayName != null;
			this.displayName = displayName;
		}

		@Override
		public String toString() {
			return displayName;
		}
	}

	/**
	 * Constructs a new QRCodeGenerator instance.
	 *
	 * The inital ImmutableDimension of the QR Code to create is
	 * BarCodeSize.LARGE.
	 *
	 * @throws NullPointerException if encodable is null
	 */
	public QRCodeGenerator() {
		this(MAX_IMAGE_SIZE);
	}

	/**
	 * Constructs a new QRCodeGenerator instance.
	 *
	 * The inital ImmutableDimension of the QR Code to create is
	 * BarCodeSize.LARGE.
	 *
	 * @param maxSize the maximum image size this QRCodeGenerator creates. {@link #generateCode()
	 * } will throw an WriterException if the generated code will be bigger than
	 * maxSize.
	 *
	 * @throws NullPointerException if encodable is null
	 */
	public QRCodeGenerator(ImmutableDimension maxSize) {
		if (maxSize == null) {
			throw new NullPointerException();
		}
		maxImageSize = maxSize;
		modus = Modus.FIXED_SIZE;
		renderer = new FixedSizeRenderer(maxImageSize);
		hints.put(ERROR_CORRECTION_HINT, DEFAULT_ERROR_CORRECTION_LEVEL);
		hints.put(CHARACTER_SET_HINT, DEFAULT_CHARACTER_SET.name());
		rendererListener = new RendererListener();
		renderer.addPropertyChangeListener(rendererListener);
		qrCodeInterface = QRCodeFactory.getNullInstance();
	}

	public void setContent(String s) {
		String oldContent = content;
		this.content = s;
		if (!StaticTools.bothNullOrEqual(oldContent, content)) {
			resetState();
			pcs.firePropertyChange(CONTENT_PROPERTY, oldContent, content);
		}
	}

	/**
	 * Returns the content of the Encodable.
	 *
	 * @return the content of the Encodable. May return null if there is no
	 * content.
	 */
	public String getContent() {
		return content;
	}

	public boolean hasContent() {
		return content != null;
	}

	/**
	 * Returns the length of the Encodable's content.
	 *
	 * @return the length of the Encodable's content
	 *
	 * @throws IllegalStateException if {@link #hasContent() } returns
	 * {@code  false}
	 */
	public int getContentLength() {
		if (content == null) {
			throw new IllegalStateException();
		}
		return content.length();
	}

	/**
	 * Sets the error correction level used to create the QR Codes.
	 *
	 * @param ecLevel the error correction level used to create QR Codes
	 *
	 * @throws NullPointerException if ecLevel is null
	 */
	public void setErrorCorrectionLevel(ErrorCorrectionLevel ecLevel) {
		if (ecLevel == null) {
			throw new NullPointerException();
		}
		Object oldLevel = hints.put(ERROR_CORRECTION_HINT, ecLevel);
		assert hints.get(ERROR_CORRECTION_HINT) != null;
		if (oldLevel == null || oldLevel != ecLevel) {
			resetState();
			pcs.firePropertyChange(ERROR_CORRECTION_LEVEL_PROPERTY, oldLevel, ecLevel);
		}
	}

	/**
	 * Returns the ErrorCorrectionLevel if one is set, null otherwise.
	 *
	 * @return the ErrorCorrectionLevel if one is set, null otherwise
	 */
	public ErrorCorrectionLevel getErrorCorrectionLevel() {
		return (ErrorCorrectionLevel) hints.get(ERROR_CORRECTION_HINT);
	}

	/**
	 * Sets the charset used to create the QR Codes.
	 *
	 * @param charset the charset used to create the QR Codes
	 *
	 * @throws NullPointerException if charset is null
	 */
	public void setCharacterEncoding(Charset charset) {
		if (charset == null) {
			throw new NullPointerException();
		}
		Charset oldCharset = currentCharset;
		currentCharset = charset;
		String newCharsetName = charset.name();
		Object oldCharsetName = hints.put(CHARACTER_SET_HINT, newCharsetName);
		assert (oldCharsetName == null ? oldCharset == null : oldCharsetName.equals(oldCharset.name())) : oldCharset + " " + oldCharsetName;
		if (oldCharset == null || !oldCharset.equals(charset)) {
			resetState();
			pcs.firePropertyChange(CHARACTER_ENCODING_PROPERTY, oldCharset, charset);
		}
	}

	/**
	 * Returns the character encoding if one is explicitely set, null otherwise.
	 *
	 * @return the character encoding if one is explicitely set, null otherwise
	 */
	public Charset getCharacterEncoding() {
		assert (currentCharset == null ? hints.get(CHARACTER_SET_HINT) == null : currentCharset.name().equals((String) hints.get(CHARACTER_SET_HINT)));
		return currentCharset;
	}

	/**
	 * Returns the default character encoding that is used if there is none
	 * explicetly set.
	 *
	 * @return the default character encoding that is used if there is none
	 * explicetly set.
	 */
	public static Charset getDefaultCharacterEncoding() {
		return DEFAULT_CHARACTER_SET;
	}

	/**
	 * Removes the explicitely set character encoding that is used to create the
	 * QR Codes. This method is safe to call if there is no explicitely set
	 * character encoding.
	 */
	public void removeCharacterEncoding() {
		Object oldCharsetName = hints.remove(CHARACTER_SET_HINT);
		Charset oldCharset = currentCharset;
		assert (oldCharset == null ? oldCharsetName == null : oldCharset.name().equals(oldCharsetName));
		currentCharset = null;
		if (oldCharsetName != null) {
			resetState();
			pcs.firePropertyChange(CHARACTER_ENCODING_PROPERTY, oldCharset, null);
		}
	}

	/**
	 * Returns true if the given
	 * <code>String</code> can be encoded by the explicitely set character
	 * encoding, or, if none is set, by the default character encoding.
	 *
	 * @param s the string to test
	 *
	 * @return true if the given <code>String</code> can be encoded by the
	 * explicitely set character encoding, or, if none is set, by the default
	 * character encoding
	 *
	 * @throws NullPointerException if s is null
	 */
	public boolean canBeEncoded(String s) {
		if (s == null) {
			throw new NullPointerException();
		}
		CharsetEncoder encoder;
		if (currentCharset == null) {
			encoder = DEFAULT_CHARACTER_SET.newEncoder();
		} else {
			encoder = currentCharset.newEncoder();
		}
		return encoder.canEncode(s);
	}

	private void setWasEncodable(String s) {
		TriState oldValue = wasEncodable;
		if (s == null) {
			wasEncodable = TriState.NOT_APPLICABLE;
		} else {
			wasEncodable = TriState.fromBoolean(canBeEncoded(s));
		}
		pcs.firePropertyChange(WAS_ENCODABLE_PROPERTY, oldValue, wasEncodable);
	}

	/**
	 * Returns true if the last invocation of {@link #generate()} showed that
	 * the content was encodable by the current character encoding, false
	 * otherwise.
	 *
	 * @return Returns true if the last invocation generate() showed that the
	 * content was encodable by the current character encoding, false otherwise
	 */
	public TriState getWasEncodable() {
		return wasEncodable;
	}

	private void setAsciiState(String s, QRCode code) {
		TriState oldState = wasAscii;
		TriState newState;
		if (code == null) {
			newState = TriState.NOT_APPLICABLE;
		} else if (Mode.ALPHANUMERIC == code.getMode() || Mode.NUMERIC == code.getMode()) {
			newState = TriState.TRUE;
		} else {
			assert s != null;
			synchronized (privateLock) {
				newState = TriState.fromBoolean(ASCII_ENCODER.canEncode(s));
			}
		}
		wasAscii = newState;
		pcs.firePropertyChange(WAS_ASCII_ONLY_PROPERTY, oldState, newState);
	}

	/**
	 * Returns TRUE if the input consists of ASCII characters only. If the input
	 * contains non-ASCII characters, FALSE is returned. If
	 * {@link #isValidState()} returns false, NOT_APPLICABLE is returned. This
	 * method never returns null.
	 *
	 * @return TRUE if the input consists of ASCII characters only
	 */
	public TriState getWasAsciiOnly() {
		return wasAscii;
	}

	/**
	 * Sets the size of the QR Code to create.
	 *
	 * @param size the size of the QR Code to create
	 *
	 * @throws NullPointerException if size is null
	 */
	public void setRequestedSize(BarCodeSize size) {
		if (size == null) {
			throw new NullPointerException();
		}
		setRequestedDimension(size.getImmutableDimension());
	}

	/**
	 * Sets the dimension of the QR Code to create.
	 *
	 * @param d the dimension of the QR Code to create
	 *
	 * @throws NullPointerException if d is null
	 * @throws IllegalStateException if {@code getModus() == Modus.MODULE_SIZE}
	 */
	public void setRequestedDimension(ImmutableDimension d) {
		renderer.setRequestedDimension(d);
		dimension = d;
	}

	/**
	 * Returns the dimension of the QR Code to create.
	 *
	 * @return the dimension of the QR Code to create. Returns never null.
	 */
	public ImmutableDimension getRequestedDimension() {
		return renderer.getRequestedDimension();
	}

	/**
	 * Returns the actual dimension, or null if the actual dimension in unknown,
	 * e.g. after invoking {@link #setModuleSize()}
	 *
	 * @return the actual dimension, or null if the actual dimension in unknown
	 */
	public ImmutableDimension getActualDimension() {
		return renderer.getActualDimension();
	}

	/**
	 * Returns {@link TriState TRUE} if the actual dimension of the symbol
	 * exceeds the requested dimension, {@link TriState FALSE} if the actual
	 * dimension is lower or equal to the requested dimension, or
	 * {@link TriState NOT_APPLICABLE} if modus is MODULE_SIZE.
	 *
	 * @return
	 */
	public TriState exceedsRequestedDimension() {
		return renderer.exceedsRequestedDimension();
	}

	/**
	 * Sets the module size in pixels.
	 *
	 * @param size the module size in pixel. Size must be &gt;= 0.
	 *
	 * @throws IllegalArgumentException if size &lt; 1
	 * @throws IllegalStateException if modus != Modus.MODULE_SIZE
	 */
	public void setModuleSize(int size) {
		renderer.setModuleSize(size);
	}

	/**
	 * Returns the module size in pixels.
	 *
	 * @return the module size in pixels
	 */
	public int getModuleSize() {
		return renderer.getModuleSize();
	}

	/**
	 * Sets the modus.
	 *
	 * @param modus the modus
	 *
	 * @throws NullPointerException if modus is null
	 */
	public void setModus(Modus modus) {
		if (modus == null) {
			throw new NullPointerException();
		}
		Modus oldModus = this.modus;
		this.modus = modus;
		if (oldModus != this.modus) {
			Renderable oldRenderer = renderer;
			oldRenderer.removePropertyChangeListener(rendererListener);
			switch (modus) {
				case BEST_FIT:
					renderer = new OptimalSizeRenderer(maxImageSize);
					renderer.setRequestedDimension(dimension);
					break;
				case FIXED_SIZE:
					renderer = new FixedSizeRenderer(maxImageSize);
					renderer.setRequestedDimension(dimension);
					break;
				case MODULE_SIZE:
					renderer = new ModuleSizeRenderer(maxImageSize);
					renderer.setModuleSize(oldRenderer.getModuleSize());
			}
			renderer.addPropertyChangeListener(rendererListener);
			pcs.firePropertyChange(MODUS_PROPERTY, oldModus, modus);
		}
	}

	/**
	 * Returns the modus.
	 *
	 * @return the modus. Never returns null.
	 */
	public Modus getModus() {
		return modus;
	}

	/**
	 * Generates the QR Code representing the set content. The image of this QR
	 * Code can be obtained by calling {@link #getImage()}.
	 *
	 * @throws WriterException
	 * @throws CodeSizeException
	 */
	public void generateCode() throws WriterException, CodeSizeException {
		ErrorCorrectionLevel errorCorrectionLevel = (ErrorCorrectionLevel) hints.get(EncodeHintType.ERROR_CORRECTION);
		setWasEncodable(content);
		try {
			code = Encoder.encode(content, errorCorrectionLevel, hints);
		} finally {
			setAsciiState(content, code);
			qrCodeInterface = QRCodeFactory.getInstance(code);
		}
		renderer.renderResult(code);
		pcs.firePropertyChange(RESULT_PROPERTY, null, null);
	}

	/**
	 * Returns the image of the QR Code generated by invoking
	 * {@link #generateCode()}.
	 *
	 * @return the image of the QR Code generated by invoking
	 * {@link #generateCode()}
	 *
	 * @throws IllegalStateException if no code has been generated, i.e.
	 * {@link #generateCode()} hasn't been called before.
	 */
	public BufferedImage getImage() {
		if (!renderer.hasResult()) {
			throw new IllegalStateException();
		}
		return MatrixToImageWriter.toBufferedImage(renderer.getResult());
	}

	/**
	 * Returns the version of this QR Code.
	 *
	 * @return the of this QR Code, or -1 if generateCode() hasn't been called
	 * yet or after {@link #resetState()} has been called.
	 */
	public int getVersion() {
		return qrCodeInterface.getVersionNumber();
	}

	/**
	 * Returns the {@link Mode} in which data is encoded after the last
	 * invocation of {@link #generateCode()}.
	 *
	 * @return the {@link Mode} in which data is encoded after the last
	 * invocation of {@link #generateCode()}. May be null if generateCode()
	 * hasn't been called yet or after {@link #resetState()} has been called.
	 */
	public Mode getMode() {
		return qrCodeInterface.getMode();
	}

	/**
	 * Returns the number of total bytes in the QR Code.
	 *
	 * @return the number of total bytes in the QR Code, or -1 if generateCode()
	 * hasn't been called yet or after {@link #resetState()} has been called.
	 */
	public int getNumTotalBytes() {
		return qrCodeInterface.getNumTotalBytes();
	}

	/**
	 * Returns the number of bytes in the QR Code.
	 *
	 * @return the number of bytes in the QR Code, or -1 if generateCode()
	 * hasn't been called yet or after {@link #resetState()} has been called.
	 */
	public int getNumDataBytes() {
		return qrCodeInterface.getNumDataBytes();
	}

	/**
	 * Returns the number of error correction bytes in the QR Code.
	 *
	 * @return the number of error correction bytes in the QR Code, or -1 if
	 * generateCode() hasn't been called yet or after {@link #resetState()} has
	 * been called.
	 */
	public int getNumECBytes() {
		return qrCodeInterface.getNumECBytes();
	}

	/**
	 * Returns the number of Reed-Solomon blocks in the QR Code.
	 *
	 * @return the number of Reed-Solomon blocks in the QR Code, or -1 if
	 * generateCode() hasn't been called yet or after {@link #resetState()} has
	 * been called.
	 */
	public int getNumRSBlocks() {
		return qrCodeInterface.getNumRSBlocks();
	}

	/**
	 * Returns the QRCodeGenerator to a state before invoking
	 * {@link #generateCode()}, i.e. invoking {@link #getImage()} after calling
	 * this method will throw an IllegalStateException.
	 */
	public void resetState() {
		code = null;
		qrCodeInterface = QRCodeFactory.getInstance(code);
		setWasEncodable(null);
		setAsciiState(null, null);
		renderer.resetState(); // fires RESULT_STATE event if applicable
	}

	/**
	 * Returns true if an {@link #generateCode() } has been invoked
	 * successfully, false otherwise. This method will also return false after
	 * invoking resetState or any of the setter-methods.
	 *
	 * @return true if an {@link #generateCode() } has been invoked
	 * successfully, false otherwise
	 */
	public boolean isValidState() {
		return renderer.hasResult();
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	private class RendererListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			pcs.firePropertyChange(evt);
		}
	}
}
