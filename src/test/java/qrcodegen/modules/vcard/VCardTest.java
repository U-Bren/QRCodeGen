/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qrcodegen.modules.vcard;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardTest {

	public VCardTest() {
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
	}

//	/**
//	 * Test of toString method, of class VCard.
//	 */
//	@Test
//	public void testToString() {
//		System.out.println("toString");
//		VCard instance;
//		
//		VCardProperty fn = VCardProperty.newInstance(Property.FN);
//		fn.addEntry(new FNProperty.Builder("Dr.Dr. Martina Musterfrau").build());
//		
//		VCardProperty n = VCardProperty.newInstance(Property.N);
//		n.addEntry(NProperty.Builder.newInstance()
//				.givenName("Martina")
//				.familyName("Musterfrau")
//				.additionalName("Mia").additionalName("Sophie")
//				.honorificPrefix("Dr.med.").honorificPrefix("Dr.med.dent.")
//				.build());
//		
//		VCardProperty nickname = VCardProperty.newInstance(Property.NICKNAME);
//		nickname.addEntry(new NicknameProperty.Builder().nickname("Mia").type(TypeParameter.HOME).pref(1).build());
//		nickname.addEntry(new NicknameProperty.Builder().nickname("Boss").type(TypeParameter.WORK).type(TypeParameter.HOME).build());
//		
//		VCardProperty adr = VCardProperty.newInstance(Property.ADR);
//		adr.addEntry(AdrProperty.Builder.newInstance().street("Lange Zeile 1").locality("Erlangen").code("91052").label("Dr. Martina Musterfrau\nInstitute of Computer Science: 1A\nLange Zeile 1\n91052 Erlangen").build());
//		
//		VCardProperty email = VCardProperty.newInstance(Property.EMAIL);
//		email.addEntry(new EMailProperty.Builder("martina.musterfrau@ics.com").pref(2).type(TypeParameter.WORK).build());
//		
//		VCardProperty tel = VCardProperty.newInstance(Property.TEL);
//		tel.addEntry(new TelProperty.Builder("+49 9131 123456").type(TypeParameter.WORK).type(TypeParameter.VOICE).build());
//		tel.addEntry(new TelProperty.Builder("+49 177 12345678").type(TypeParameter.WORK).type(TypeParameter.CELL).type(TypeParameter.VOICE).build());
//		
//		VCardProperty note = VCardProperty.newInstance(Property.NOTE);
//		note.addEntry(new NoteProperty.Builder("This fax number is operational 0800 to 1715 EST, Mon-Fri.").build());
//		
//		instance = new VCard.Builder().propertyEntry(n).propertyEntry(nickname).propertyEntry(adr).propertyEntry(email).propertyEntry(tel).propertyEntry(note).build();
//		System.out.println(instance.toString());
//	}
	/**
	 * Test of toString method, of class VCard.
	 */
	@Test
	public void testToString() {
		System.out.println("toString");
		VCard instance;

		List<FNProperty> fn = new ArrayList<FNProperty>();
		fn.add(new FNProperty.Builder("Dr.Dr. Martina Musterfrau").build());

		List<NProperty> n = new ArrayList<NProperty>();
		n.add(NProperty.Builder.newInstance()
				.givenName("Martina")
				.familyName("Musterfrau")
				.additionalName("Mia").additionalName("Sophie")
				.honorificPrefix("Dr.med.").honorificPrefix("Dr.med.dent.")
				.build());

		List<NicknameProperty> nickname = new ArrayList<NicknameProperty>();
		NicknameProperty.Builder nnpb = new NicknameProperty.Builder().nickname("Mia");
		nnpb.type(TypeParameter.HOME).pref(1);
		nickname.add(nnpb.build());
		nnpb = new NicknameProperty.Builder().nickname("Boss");
		nnpb.type(TypeParameter.WORK).type(TypeParameter.HOME);
		nickname.add(nnpb.build());

		List<AdrProperty> adr = new ArrayList<AdrProperty>();
		adr.add(AdrProperty.Builder.newInstance().street("Lange Zeile 1").locality("Erlangen").code("91052").label("Dr. Martina Musterfrau\nInstitute of Computer Science: 1A\nLange Zeile 1\n91052 Erlangen").build());

		List<EMailProperty> email = new ArrayList<EMailProperty>();
		EMailProperty.Builder epb = new EMailProperty.Builder("martina.musterfrau@ics.com");
		epb.pref(2).type(TypeParameter.WORK);
		email.add(epb.build());

		List<TelProperty> tel = new ArrayList<TelProperty>();
		TelProperty.Builder tpb = new TelProperty.Builder("+49 9131 123456");
		tpb.type(TypeParameter.WORK).type(TypeParameter.VOICE);
		tel.add(tpb.build());
		tpb = new TelProperty.Builder("+49 177 12345678");
		tpb.type(TypeParameter.WORK).type(TypeParameter.CELL).type(TypeParameter.VOICE);
		tel.add(tpb.build());

		List<NoteProperty> note = new ArrayList<NoteProperty>();
		note.add(new NoteProperty.Builder("This fax number is operational 0800 to 1715 EST, Mon-Fri.").build());

		instance = new VCard.Builder().propertyEntry(fn).propertyEntry(n).propertyEntry(nickname).propertyEntry(adr).propertyEntry(email).propertyEntry(tel).propertyEntry(note).build();
		System.out.println(instance.toString());
	}

	@Test
	public void testToString2() {
		System.out.println("toString2");
		VCard instance;

		List<FNProperty> fn = new ArrayList<FNProperty>();
		fn.add(new FNProperty.Builder("Dr.Dr. Martina Musterfrau").build());

		List<NProperty> n = new ArrayList<NProperty>();
		n.add(NProperty.Builder.newInstance()
				.givenName("Martina")
				.familyName("Musterfrau")
				.additionalName("Mia").additionalName("Sophie")
				.honorificPrefix("Dr.med.").honorificPrefix("Dr.med.dent.")
				.build());

		List<NicknameProperty> nickname = new ArrayList<NicknameProperty>();
		NicknameProperty.Builder nnpb = new NicknameProperty.Builder().nickname("Mia");
		nnpb.type(TypeParameter.HOME).pref(1);
		nickname.add(nnpb.build());
		nnpb = new NicknameProperty.Builder().nickname("Boss");
		nnpb.type(TypeParameter.WORK).type(TypeParameter.HOME);
		nickname.add(nnpb.build());

		List<AdrProperty> adr = new ArrayList<AdrProperty>();
		adr.add(AdrProperty.Builder.newInstance().street("Lange Zeile 1").locality("Erlangen").code("91052").label("Dr. Martina Musterfrau\nInstitute of Computer Science: 1A\nLange Zeile 1\n91052 Erlangen").build());

		instance = new VCard.Builder().propertyEntry(fn).propertyEntry(n).propertyEntry(nickname).propertyEntry(adr).build();
		System.out.println(instance.toString());
	}
}
