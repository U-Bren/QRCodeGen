/*
 Copyright 2012 Stefan Ganzer

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
package qrcodegen.modules.vcard.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.EnumSet;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import qrcodegen.modules.vcard.AdrProperty;
import qrcodegen.modules.vcard.BDayProperty;
import qrcodegen.modules.vcard.EMailProperty;
import qrcodegen.modules.vcard.FNProperty;
import qrcodegen.modules.vcard.IllegalCharacterException;
import qrcodegen.modules.vcard.KindProperty;
import qrcodegen.modules.vcard.NProperty;
import qrcodegen.modules.vcard.NicknameProperty;
import qrcodegen.modules.vcard.NoteProperty;
import qrcodegen.modules.vcard.OrgProperty;
import qrcodegen.modules.vcard.TelProperty;
import qrcodegen.modules.vcard.TypeParameter;
import qrcodegen.modules.vcard.UrlProperty;
import qrcodegen.modules.vcard.VCard;
import qrcodegen.modules.vcard.VCardTools;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardReaderTest {

	private static final Charset UTF_8 = Charset.forName("UTF-8");
	private final File path;
	private VCardReader reader;

	public VCardReaderTest() throws URISyntaxException {
		path = new File(VCardReaderTest.class.getResource("files").toURI());
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
		reader = null;
	}

	@Test
	public void testParseIllegalInputNTwice() throws FileNotFoundException, IllegalCharacterException, IOException {
		File f = new File(path, "Test_N_twice.vcf");
		String input = VCardTools.readCard(f, UTF_8);
		reader = new VCardReader(input);
		reader.parseInput();
		assertFalse(reader.foundValidVCard());
	}

	/**
	 * Test of parseInput method, of class VCardReader.
	 */
	@Test
	public void testParseInput() throws FileNotFoundException, IllegalCharacterException, IOException {
		File f = new File(path, "Test_valid.vcf");
		System.out.println(f.getAbsolutePath());
		String input = VCardTools.readCard(f, UTF_8);

		reader = new VCardReader(input);
		reader.parseInput();
		assertTrue(reader.foundValidVCard());
		VCard vc = reader.getVCard();

//		List<KindProperty> kindProp = vc.getKindProperties();
//		List<OrgProperty> orgProps = vc.getOrgProperties();
		List<FNProperty> fnProp = vc.getFNProperties();
		List<NProperty> nProps = vc.getNProperties();
		List<BDayProperty> bdayProp = vc.getBDayProperties();
		List<AdrProperty> adrProps = vc.getAdrProperties();
		List<EMailProperty> emailProps = vc.getEMailProperties();
		List<TelProperty> telProps = vc.getTelProperties();
		List<NoteProperty> noteProp = vc.getNoteProperties();
		List<NicknameProperty> nicknameProp = vc.getNicknameProperties();
		List<UrlProperty> urlProp = vc.getUrlProperties();

//		assertEquals(1, kindProp.size());
//		assertEquals(1, orgProps.size());
		assertEquals(1, fnProp.size());
		assertEquals(1, nProps.size());
		assertEquals(1, bdayProp.size());
		assertEquals(1, adrProps.size());
		assertEquals(1, nicknameProp.size());
		assertEquals(2, emailProps.size());
		assertEquals(2, telProps.size());
		assertEquals(1, noteProp.size());
		assertEquals(1, urlProp.size());

//		String kind = kindProp.get(0).getKind();
//		assertEquals("individual", kind);

//		OrgProperty orgProp = orgProps.get(0);
//		assertTrue(orgProp.hasOrganizationName());
//		String organizationName = orgProp.getOrganizationName();
//		assertEquals("Meditech, Inc.", organizationName);
//		assertTrue(orgProp.hasUnitNames());
//		List<String> unitNames = orgProp.getUnitNames();
//		assertEquals(2, unitNames.size());
//		assertEquals("Zahnheilkunde", unitNames.get(0));
//		assertEquals("Implantologie, und mehr", unitNames.get(1));

		String fn = fnProp.get(0).getFormattedName();
		assertEquals("Dr.med. Dr.med.dent. Marlene Musterfrau", fn);

		NProperty nProp = nProps.get(0);
		String surname = nProp.getLastName();
		String firstName = nProp.getFirstName();
		String addNames = nProp.getAdditionalNames();
		String honorPref = nProp.getHonorificPrefixes();
		String honorSuff = nProp.getHonorificSuffixes();
		assertEquals("Musterfrau", surname);
		assertEquals("Marlene", firstName);
		assertEquals("Mia,Sophie", addNames);
		assertEquals("Dr.med.,Dr.med.dent.", honorPref);
		assertTrue(honorSuff.isEmpty());

		BDayProperty bdProp = bdayProp.get(0);
		assertFalse(bdProp.hasYear());
		assertTrue(bdProp.hasMonth());
		assertTrue(bdProp.hasDay());
		assertEquals(7, bdProp.getMonth());
		assertEquals(7, bdProp.getDay());
		
		AdrProperty adrProp = adrProps.get(0);
		assertTrue(adrProp.hasLabel());
		String label = adrProp.getLabel();
		assertEquals("Dr. Marlene Musterfrau\nLange Zeile 63\n91056 Erlangen\nFederal Republic of Germany", label);
		String pobox = adrProp.getPoBox();
		String postalCode = adrProp.getPostalCode();
		String locality = adrProp.getLocality();
		String street = adrProp.getStreet();
		String country = adrProp.getCountryName();
		String extAddress = adrProp.getExtendedAddress();
		String region = adrProp.getRegion();
		assertTrue(pobox.isEmpty());
		assertTrue(extAddress.isEmpty());
		assertTrue(region.isEmpty());
		assertEquals("91052",postalCode);
		assertEquals("Lange Zeile 63", street);
		assertEquals("Deutschland", country);
		assertEquals("Erlangen", locality);
		assertEquals(EnumSet.of(TypeParameter.HOME), adrProp.getType());

		String nickname = nicknameProp.get(0).getNickname();
		assertEquals("Misa", nickname);

		EMailProperty emailProp1 = emailProps.get(0);
		assertEquals("misa.musterfrau@gmail.com", emailProp1.getEMail());
		assertEquals(EnumSet.of(TypeParameter.HOME, TypeParameter.WORK), emailProp1.getType());
		EMailProperty emailProp2 = emailProps.get(1);
		assertEquals("doktor_musterfrau@musterfrau.de", emailProp2.getEMail());
		assertEquals(EnumSet.of(TypeParameter.HOME, TypeParameter.WORK), emailProp2.getType());

		TelProperty telProp1 = telProps.get(0);
		assertEquals("(09131)12-34-56-78", telProp1.getText());
		assertEquals(EnumSet.of(TypeParameter.VOICE, TypeParameter.FAX, TypeParameter.HOME), telProp1.getType());
		TelProperty telProp2 = telProps.get(1);
		assertEquals("+49(177)12-34-56-78", telProp2.getText());
		assertEquals(EnumSet.of(TypeParameter.CELL, TypeParameter.HOME), telProp2.getType());
		
		String note = noteProp.get(0).getNote();
		assertEquals("You can send me a FAX, too!\nBut don't call me late at night, please.", note);
		
		String url = urlProp.get(0).getUrlAsString();
		assertEquals("HTTP://institut.musterfrau.de", url);
		assertEquals(EnumSet.of(TypeParameter.WORK), urlProp.get(0).getType());
	}

	@Test
	public void testInvalidBegin() throws FileNotFoundException, IllegalCharacterException, IOException {
		File f = new File(path, "Test_invalid_begin.vcf");
		System.out.println(f.getAbsolutePath());
		String input = VCardTools.readCard(f, UTF_8);

		reader = new VCardReader(input);
		reader.parseInput();
		assertFalse(reader.foundValidVCard());
	}

	@Test
	public void testInvalidEnd() throws FileNotFoundException, IllegalCharacterException, IOException {
		File f = new File(path, "Test_invalid_end.vcf");
		System.out.println(f.getAbsolutePath());
		String input = VCardTools.readCard(f, UTF_8);

		reader = new VCardReader(input);
		reader.parseInput();
		assertFalse(reader.foundValidVCard());
	}

	/**
	 * Test of parseInput method, of class VCardReader.
	 */
	@Test
	public void testParameterParsing() throws FileNotFoundException, IllegalCharacterException, IOException {
		File f = new File(path, "Test_main_parser_parameter.vcf");
		System.out.println(f.getAbsolutePath());
		String input = VCardTools.readCard(f, UTF_8);

		reader = new VCardReader(input);
		reader.parseInput();
		assertTrue(reader.foundValidVCard());
		VCard vc = reader.getVCard();
		assertEquals(10, vc.getEMailProperties().size());
	}

	@Test
	public void testUnknownCalscale() throws FileNotFoundException, IllegalCharacterException, IOException {
		File f = new File(path, "Test_unknown_calscale.vcf");
		System.out.println(f.getAbsolutePath());
		String input = VCardTools.readCard(f, UTF_8);

		reader = new VCardReader(input);
		reader.parseInput();
		assertTrue(reader.foundValidVCard());
		VCard vc = reader.getVCard();
		List<FNProperty> fnProps = vc.getFNProperties();
		assertEquals(1, fnProps.size());
		String fn = fnProps.get(0).getFormattedName();
		assertEquals("John Doe", fn);
		List<BDayProperty> bdayProp = vc.getBDayProperties();
		assertTrue(bdayProp.isEmpty());

	}
	/**
	 * Test of foundValidVCard method, of class VCardReader.
	 */
	/**
	 * Test of getVCard method, of class VCardReader.
	 */
	/**
	 * Test of getMalformedLine method, of class VCardReader.
	 */
	/**
	 * Test of getUnknownProperties method, of class VCardReader.
	 */
	/**
	 * Test of hasUnknownProperties method, of class VCardReader.
	 */
	/**
	 * Test of hasUnknownTypeParameters method, of class VCardReader.
	 */
	/**
	 * Test of getUnknownTypeParameters method, of class VCardReader.
	 */
	/**
	 * Test of getInput method, of class VCardReader.
	 */
	/**
	 * Test of setState method, of class VCardReader.
	 */
	/**
	 * Test of getNewState method, of class VCardReader.
	 */
	/**
	 * Test of getBeginState method, of class VCardReader.
	 */
	/**
	 * Test of getVersionState method, of class VCardReader.
	 */
	/**
	 * Test of getFnState method, of class VCardReader.
	 */
	/**
	 * Test of getBodyState method, of class VCardReader.
	 */
	/**
	 * Test of getEndState method, of class VCardReader.
	 */
	/**
	 * Test of getValidVCardState method, of class VCardReader.
	 */
	/**
	 * Test of getMalformedVCardState method, of class VCardReader.
	 */
	/**
	 * Test of main method, of class VCardReader.
	 */
}
