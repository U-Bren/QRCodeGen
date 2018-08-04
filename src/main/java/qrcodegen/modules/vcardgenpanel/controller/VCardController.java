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
package qrcodegen.modules.vcardgenpanel.controller;

import java.awt.Component;
import java.awt.Image;
import java.awt.print.Printable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeListener;
import qrcodegen.ContentModule;
import qrcodegen.modules.vcardgenpanel.PropertyProviderViews;
import qrcodegen.modules.vcardgenpanel.model.VCardModel;
import qrcodegen.modules.vcardgenpanel.presentationmodel.AddressPresentationModel;
import qrcodegen.modules.vcardgenpanel.presentationmodel.NamePresentationModel;
import qrcodegen.modules.vcardgenpanel.presentationmodel.NotePresentationModel;
import qrcodegen.modules.vcardgenpanel.presentationmodel.TelephonePresentationModel;
import qrcodegen.modules.vcardgenpanel.presentationmodel.VCardBDayPresentationModel;
import qrcodegen.modules.vcardgenpanel.presentationmodel.VCardEMailPresentationModel;
import qrcodegen.modules.vcardgenpanel.presentationmodel.VCardPresentationModel;
import qrcodegen.modules.vcardgenpanel.presentationmodel.VCardUrlPresentationModel;
import qrcodegen.modules.vcardgenpanel.view.VCardAddressPanel;
import qrcodegen.modules.vcardgenpanel.view.VCardBDayPanel;
import qrcodegen.modules.vcardgenpanel.view.VCardEMailPanel;
import qrcodegen.modules.vcardgenpanel.view.VCardNamePanel;
import qrcodegen.modules.vcardgenpanel.view.VCardNotePanel;
import qrcodegen.modules.vcardgenpanel.view.VCardTelephonePanel;
import qrcodegen.modules.vcardgenpanel.view.VCardUrlPanel;
import qrcodegen.modules.vcardgenpanel.view.VCardView;

/**
 * This class is designed to work in conjunction with a VCardView.
 *
 * @author Stefan Ganzer
 */
public class VCardController implements ContentModule {

	private final Set<Charset> CHARSETS = Collections.singleton(Charset.forName("UTF-8"));
	/** Lazily created stateless event to inform listeners that something has
	 * changed. */
	private final VCardView view;
	private final List<PropertyProviderViews> views;
	private final VCardPresentationModel presentationModel;

	{

		final VCardModel vcModel = new VCardModel();
		presentationModel = new VCardPresentationModel(vcModel);


		AddressPresentationModel addressPM = new AddressPresentationModel(vcModel.getAddressModel());
		presentationModel.addPresentationModel(addressPM);
		VCardAddressPanel addressPanel = new VCardAddressPanel(addressPM);

		NamePresentationModel namePM = new NamePresentationModel(vcModel.getNameModel());
		presentationModel.addPresentationModel(namePM);
		VCardNamePanel namePanel = new VCardNamePanel(namePM);

		NotePresentationModel notePM = new NotePresentationModel(vcModel.getNoteModel());
		presentationModel.addPresentationModel(notePM);
		VCardNotePanel notePanel = new VCardNotePanel(notePM);

		VCardBDayPresentationModel bdyPM = new VCardBDayPresentationModel(vcModel.getBDayModel());
		presentationModel.addPresentationModel(bdyPM);
		VCardBDayPanel bdayPanel = new VCardBDayPanel(bdyPM);

		VCardUrlPresentationModel urlPM = new VCardUrlPresentationModel(vcModel.getUrlModel());
		presentationModel.addPresentationModel(urlPM);
		VCardUrlPanel urlPanel = new VCardUrlPanel(urlPM);

//		VCardUrlPresentationModel2 urlPM = new VCardUrlPresentationModel2(vcModel.getUrlModel2());
//		presentationModel.addPresentationModel(urlPM);
//		VCardUrlPanel2 urlPanel = new VCardUrlPanel2(urlPM);

		TelephonePresentationModel telPM = new TelephonePresentationModel(vcModel.getTelModel());
		presentationModel.addPresentationModel(telPM);
		VCardTelephonePanel telPanel = new VCardTelephonePanel(telPM);

		VCardEMailPresentationModel emailPM = new VCardEMailPresentationModel(vcModel.getEMailModel());
		presentationModel.addPresentationModel(emailPM);
		VCardEMailPanel emailPanel = new VCardEMailPanel(emailPM);


		List<PropertyProviderViews> v = new ArrayList<PropertyProviderViews>(7);
		v.add(namePanel);
		v.add(addressPanel);
		v.add(telPanel);
		v.add(emailPanel);
		v.add(notePanel);
		v.add(urlPanel);
		v.add(bdayPanel);
		views = v;
	}

	VCardController() {
		this.view = VCardView.newInstance(presentationModel);
		initView();
	}

	private void initView() {
		int tabIndex = 0;
		for (PropertyProviderViews p : views) {
			view.addTab(p.getJPanel().getName(), null, p.getJPanel(), p.getJPanel().getName());
			view.setTabMnemonic(tabIndex, p.getMnemonic());
			tabIndex = tabIndex + 1;
		}
	}

	public static VCardController newInstance() {
		return new VCardController();
	}

	@Override
	public Component getComponent() {
		return view;
	}

	@Override
	public int getMnemonic() {
		return view.getMnemonic();
	}

	@Override
	public boolean restrictsEncoding() {
		return true;
	}

	@Override
	public Set<Charset> getEncodingSubset() {
		return CHARSETS;
	}

	@Override
	public Printable getPrintable(Image qrcode) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getJobName() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public synchronized void addChangeListener(ChangeListener listener) {
		presentationModel.addChangeListener(listener);
	}

	@Override
	public synchronized void removeChangeListener(ChangeListener listener) {
		presentationModel.removeChangeListener(listener);
	}

	@Override
	public String getContent() {
		return presentationModel.getContent();
	}
}
