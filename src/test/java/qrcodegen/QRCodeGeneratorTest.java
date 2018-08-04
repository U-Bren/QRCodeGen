package qrcodegen;

import qrcodegen.tools.ImmutableDimension;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.common.StringUtils;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.image.BufferedImage;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import qrcodegen.QRCodeGenerator.Modus;
import qrcodegen.tools.TriState;

/**
 *
 * @author Stefan Ganzer
 */
public class QRCodeGeneratorTest {

	private static final Charset DEFAULT_CHARSET = Charset.forName("ISO-8859-1");

	public QRCodeGeneratorTest() {
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
	}

	/**
	 * Test of setRequestedSize method, of class QRCodeGenerator.
	 */
	@Test
	public void testSetSize() throws WriterException, CodeSizeException {
		System.out.println("setSize");
		String content = "jjjjjjjjjjjjjjjjj";
		final Charset charset_UTF8 = Charset.forName("UTF-8");
		final ErrorCorrectionLevel ecLevel = ErrorCorrectionLevel.L;
		final BarCodeSize size = BarCodeSize.LARGE;

		QRCodeGenerator generator = new QRCodeGenerator();

		generator.setCharacterEncoding(charset_UTF8);
		generator.setErrorCorrectionLevel(ecLevel);
		generator.setRequestedSize(size);
		generator.setContent(content);
		generator.generateCode();

		assertTrue(generator.isValidState());

		generator.setRequestedSize(BarCodeSize.SMALL);
		assertFalse(generator.isValidState());
	}

	/**
	 * Test of generateCode method, of class QRCodeGenerator. Tests for an error
	 * in the ZXing library that was fixed in r2279. This error caused the
	 * QRCodeWriter to throw an exception in some cases due to choosing
	 *
	 * @see http://code.google.com/p/zxing/source/detail?r=2279
	 */
	@Test
	public void testGenerateCodeR2279Fix() throws Exception {

		System.out.println("generateCode");

		/**
		 * The smallest string length that caused the exception with Mode.BYTE,
		 * ErrorCorrectionLevel.L. and an EncodeHintType UTF-8. Lower case
		 * letters to force the encoder to choose Mode.BYTE.
		 */
		String content = "jjjjjjjjjjjjjjjjj";
		doTest(content, "UTF-8", ErrorCorrectionLevel.L, Modus.FIXED_SIZE, 350, 350, 1, TriState.TRUE, TriState.FALSE);
		doTest(content, "UTF-8", ErrorCorrectionLevel.L, Modus.BEST_FIT, 350, 350, 1, TriState.TRUE, TriState.FALSE);
		doTest(content, "UTF-8", ErrorCorrectionLevel.L, Modus.MODULE_SIZE, 350, 350, 1, TriState.TRUE, TriState.NOT_APPLICABLE);
		doTest(content, "ISO-8859-1", ErrorCorrectionLevel.L, Modus.FIXED_SIZE, 350, 350, 1, TriState.TRUE, TriState.FALSE);
		doTest(content, "ISO-8859-1", ErrorCorrectionLevel.L, Modus.BEST_FIT, 350, 350, 1, TriState.TRUE, TriState.FALSE);
		doTest(content, "ISO-8859-1", ErrorCorrectionLevel.L, Modus.MODULE_SIZE, 350, 350, 1, TriState.TRUE, TriState.NOT_APPLICABLE);
	}

	/**
	 * Sometimes the decoder guesses the encoding wrong if the content was
	 * encoded using ISO-8859-1. Nothing we can do about it on the encoding
	 * side. We just try to figure out if the guessing algorithm has changed.
	 *
	 * @throws Exception
	 */
	@Test
	public void testGenerateCodeIso_8859_1Umlaute() throws Exception {

		System.out.println("generateCodeIso_8859_1Umlaute");

		String content = "WIFI:S:abcdefg;T:WEP;P:הצü;;";
		final Charset charset = Charset.forName("ISO-8859-1");
		final ErrorCorrectionLevel ecLevel = ErrorCorrectionLevel.L;
		final BarCodeSize size = BarCodeSize.LARGE;

		QRCodeGenerator generator = new QRCodeGenerator();

		generator.setCharacterEncoding(charset);
		generator.setErrorCorrectionLevel(ecLevel);
		generator.setRequestedSize(size);
		generator.setContent(content);
		generator.generateCode();

		BufferedImage image = generator.getImage();
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		EnumMap<DecodeHintType, Object> decodingHints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
		decodingHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
		//decodingHints.put(DecodeHintType.CHARACTER_SET, charset.name());

		assertEquals("UTF-8", Charset.forName(StringUtils.guessEncoding(content.getBytes(charset), null)).name());
		Result result = new QRCodeReader().decode(bitmap, decodingHints);
		assertFalse("StringUtils.guessEncoding(String) changed?", content.equals(result.getText()));


		content = "WIFI:S:abcdefg;T:WEP;P:הצüִ;;";
		doTest(content, "ISO-8859-1", ErrorCorrectionLevel.L, Modus.FIXED_SIZE, 120, 120, 1, TriState.TRUE, TriState.FALSE);
		doTest(content, "ISO-8859-1", ErrorCorrectionLevel.L, Modus.BEST_FIT, 120, 120, 1, TriState.TRUE, TriState.FALSE);
		doTest(content, "ISO-8859-1", ErrorCorrectionLevel.L, Modus.MODULE_SIZE, 120, 120, 1, TriState.TRUE, TriState.NOT_APPLICABLE);
	}

	/**
	 * Sometimes the decoder guesses the encoding wrong if the content was
	 * encoded using ISO-8859-1. Giving the decoder a DecodeHint should solve
	 * the problem.
	 *
	 * @throws Exception
	 */
	@Test
	public void testGenerateCodeIso_8859_1UmlauteDecodeHint() throws Exception {

		System.out.println("generateCodeIso_8859_1Umlaute");

		String content = "WIFI:S:abcdefg;T:WEP;P:הצü;;";
		doTest(content, "ISO-8859-1", ErrorCorrectionLevel.L, Modus.FIXED_SIZE, 350, 350, 1, TriState.TRUE, TriState.FALSE);
		doTest(content, "ISO-8859-1", ErrorCorrectionLevel.L, Modus.BEST_FIT, 350, 350, 1, TriState.TRUE, TriState.FALSE);
		doTest(content, "ISO-8859-1", ErrorCorrectionLevel.L, Modus.MODULE_SIZE, 350, 350, 1, TriState.TRUE, TriState.NOT_APPLICABLE);
	}

	/**
	 * Sometimes the decoder guesses the encoding wrong if the content was
	 * encoded using ISO-8859-1. Giving the decoder a DecodeHint should solve
	 * the problem.
	 *
	 * @throws Exception
	 */
	@Test
	public void testGenerateCodeExceedsRequestedSize() throws Exception {

		System.out.println("generateCodeExceedsRequestedSize");

		String content = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		doTest(content, "ISO-8859-1", ErrorCorrectionLevel.L, Modus.FIXED_SIZE, 29, 29, 1, TriState.TRUE, TriState.TRUE);
		doTest(content, "ISO-8859-1", ErrorCorrectionLevel.L, Modus.BEST_FIT, 29, 29, 1, TriState.TRUE, TriState.TRUE);
		doTest(content, "ISO-8859-1", ErrorCorrectionLevel.L, Modus.MODULE_SIZE, 29, 29, 1, TriState.TRUE, TriState.NOT_APPLICABLE);
	}

	/**
	 * Sometimes the decoder guesses the encoding wrong if the content was
	 * encoded using ISO-8859-1. Giving the decoder a DecodeHint should solve
	 * the problem.
	 *
	 * @throws Exception
	 */
	@Test
	public void testGenerateCode_Unicode() throws Exception {

		System.out.println("generateCode_Unicode");

		String content = "\u0600"; // ARABIC NUMBER SIGN
		doTest(content, "ISO-8859-1", ErrorCorrectionLevel.L, Modus.FIXED_SIZE, 350, 350, 1, TriState.FALSE, TriState.FALSE);
		doTest(content, "UTF-8", ErrorCorrectionLevel.L, Modus.FIXED_SIZE, 350, 350, 1, TriState.TRUE, TriState.FALSE);
		doTest(content, "SHIFT_JIS", ErrorCorrectionLevel.L, Modus.FIXED_SIZE, 350, 350, 1, TriState.FALSE, TriState.FALSE);

		content = "\uff71"; // Halfwidth Katakana Letter A
		doTest(content, "ISO-8859-1", ErrorCorrectionLevel.L, Modus.FIXED_SIZE, 350, 350, 1, TriState.FALSE, TriState.FALSE);
		doTest(content, "UTF-8", ErrorCorrectionLevel.L, Modus.FIXED_SIZE, 350, 350, 1, TriState.TRUE, TriState.FALSE);
		doTest(content, "SHIFT_JIS", ErrorCorrectionLevel.L, Modus.FIXED_SIZE, 350, 350, 1, TriState.TRUE, TriState.FALSE);
	}

	private static void doTest(String content, String charsetName, ErrorCorrectionLevel ecLevel, Modus modus, int width, int height, int moduleSize, TriState isEncodable, TriState exceedsRequestedDimension)
			throws WriterException, NotFoundException, ChecksumException, FormatException, CodeSizeException {
		final Charset charset = Charset.forName(charsetName);
		final ImmutableDimension dim = new ImmutableDimension(width, height);
		QRCodeGenerator generator = new QRCodeGenerator();
		assertEquals(TriState.NOT_APPLICABLE, generator.exceedsRequestedDimension());
		assertEquals(DEFAULT_CHARSET, generator.getCharacterEncoding());
		generator.removeCharacterEncoding();
		assertNull(generator.getCharacterEncoding());

		generator.setContent(content);
		if (content == null) {
			boolean gotException;
			try {
				generator.getContentLength();
				gotException = false;
			} catch (IllegalStateException ise) {
				gotException = true;
			}
			assertTrue(gotException);

		} else {
			assertEquals(content.length(), generator.getContentLength());
		}
		assertEquals(content, generator.getContent());
		assertFalse(generator.isValidState());

		generator.setCharacterEncoding(charset);
		assertEquals(charset, generator.getCharacterEncoding());
		generator.setErrorCorrectionLevel(ecLevel);
		assertEquals(ecLevel, generator.getErrorCorrectionLevel());
		generator.setModus(modus);
		assertEquals(modus, generator.getModus());
		if (modus == Modus.BEST_FIT || modus == Modus.FIXED_SIZE) {
			generator.setRequestedDimension(dim);
		} else if (modus == Modus.MODULE_SIZE) {
			generator.setModuleSize(moduleSize);
		} else {
			fail();
		}
		if (modus == Modus.MODULE_SIZE) {
			assertNull(generator.getActualDimension());
		} else {
			assertEquals(dim, generator.getRequestedDimension());
		}
		generator.generateCode();
		assertTrue(generator.isValidState());
		assertEquals(isEncodable, generator.getWasEncodable());
		assertEquals(exceedsRequestedDimension, generator.exceedsRequestedDimension());

		BufferedImage image = generator.getImage();
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		EnumMap<DecodeHintType, Object> decodingHints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
		decodingHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
		decodingHints.put(DecodeHintType.CHARACTER_SET, charset.name());

		Result result = new QRCodeReader().decode(bitmap, decodingHints);
		if (TriState.TRUE == isEncodable) {
			assertEquals("StringUtils.guessEncoding(String) changed?", content, result.getText());
		}
	}

//	@Test
//	public void testForFalseDefaultEncoding() throws WriterException, CodeSizeException {
//		QRCodeGenerator generator = new QRCodeGenerator(newEncodable("test"));
//		generator.generateCode();
//	}
	@Test
	public void testEmptyContent() throws WriterException, NotFoundException, ChecksumException, FormatException, CodeSizeException {
		doTest("", "ISO-8859-1", ErrorCorrectionLevel.L, Modus.BEST_FIT, 100, 100, 1, TriState.TRUE, TriState.FALSE);
	}

//	@Test(expected = NullPointerException.class)
//	public void testNullContent1() throws WriterException, NotFoundException, ChecksumException, FormatException, CodeSizeException {
//		QRCodeGenerator generator = new QRCodeGenerator(newEncodable(null));
//		generator.generateCode();
//	}
	@Test(expected = NullPointerException.class)
	public void testNullContent2() throws WriterException, NotFoundException, ChecksumException, FormatException {
		new QRCodeGenerator(null);
	}

	@Test(expected = NullPointerException.class)
	public void testNullContent3() throws WriterException, NotFoundException, ChecksumException, FormatException, CodeSizeException {
		doTest(null, "ISO-8859-1", ErrorCorrectionLevel.L, Modus.BEST_FIT, 100, 100, 1, TriState.TRUE, TriState.FALSE);
	}

	@Test
	public void aTestToWrite() throws WriterException, CodeSizeException {
		String content = "";
		for (int i = 0; i < 4000; i++) {
			content = content.concat("t");
		}
		QRCodeGenerator gen = new QRCodeGenerator();

		gen.setContent("test");
		assertFalse(gen.isValidState());
		gen.generateCode();
		assertTrue(gen.isValidState());
		gen.setContent(content);
		assertFalse(gen.isValidState());
		boolean catchedWriterException = false;
		try {
			gen.generateCode();
		} catch (WriterException we) {
			catchedWriterException = true;
		} catch (CodeSizeException cse) {
			fail();
		}
		assertEquals(catchedWriterException, true);
		assertFalse(gen.isValidState());
	}
}
