/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qrcodegen.modules.vcardgenpanel.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import qrcodegen.modules.vcard.FNProperty;
import qrcodegen.modules.vcard.Property;
import qrcodegen.modules.vcard.PropertyEntry;
import qrcodegen.modules.vcard.VCard;
import qrcodegen.modules.vcardgenpanel.IllegalInputException;
import qrcodegen.modules.vcardgenpanel.VCardPropertyProvider;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardModel extends AbstractModel {

	public static final String CONTENT_CHANGED = "Content";
	private static final String EMPTY_STRING = "";
	private static final int PROPERTY_PROVIDER = 6;
	private final VCardAddressModel addressModel;
	private final VCardBDayModel bdayModel;
	private final VCardNameModel nameModel;
	private final VCardNoteModel noteModel;
	private final VCardUrlModel urlModel;
//	private final VCardUrlModel2 urlModel2;
	private final VCardEMailModel eMailModel;
	private final VCardTelModel telModel;
	private final List<VCardPropertyProvider> propertyProvider;
	private final PropertyChangeEvent contentChanged = new PropertyChangeEvent(this, "Content", null, null);
	private VCard vCard;

	public VCardModel() {
		PropertyChangeListener modelListener = new ModelListener();
		this.addressModel = new VCardAddressModel();
		this.bdayModel = new VCardBDayModel();
		this.nameModel = new VCardNameModel();
		this.noteModel = new VCardNoteModel();
		this.urlModel = new VCardUrlModel();
//		this.urlModel2 = new VCardUrlModel2(3, 3, 3);
		this.telModel = new VCardTelModel(3, 3, 3);
		this.eMailModel = new VCardEMailModel(3, 3, 3);
		propertyProvider = new ArrayList<VCardPropertyProvider>(PROPERTY_PROVIDER);
		propertyProvider.add(addressModel);
		propertyProvider.add(bdayModel);
		propertyProvider.add(nameModel);
		propertyProvider.add(noteModel);
		propertyProvider.add(urlModel);
//		propertyProvider.add(urlModel2);
		propertyProvider.add(telModel);
		propertyProvider.add(eMailModel);
		for (VCardPropertyProvider p : propertyProvider) {
			((AbstractModel) p).addPropertyChangeListener(modelListener);
		}
	}

	public VCardAddressModel getAddressModel() {
		assert addressModel != null;
		return addressModel;
	}

	public VCardBDayModel getBDayModel() {
		assert bdayModel != null;
		return bdayModel;
	}

	public VCardNameModel getNameModel() {
		assert nameModel != null;
		return nameModel;
	}

	public VCardNoteModel getNoteModel() {
		assert noteModel != null;
		return noteModel;
	}

	public VCardUrlModel getUrlModel() {
		assert urlModel != null;
		return urlModel;
	}

//	public VCardUrlModel2 getUrlModel2() {
//		assert urlModel2 != null;
//		return urlModel2;
//	}

	public VCardTelModel getTelModel() {
		assert telModel != null;
		return telModel;
	}

	public VCardEMailModel getEMailModel() {
		assert eMailModel != null;
		return eMailModel;
	}

	/**
	 * Sets all fields of this VCardModel from the given VCard.
	 *
	 * @param vCard
	 */
	public void setFromVCard(VCard vCard) {
		if (vCard == null) {
			throw new NullPointerException();
		}
		if (!vCard.getAdrProperties().isEmpty()) {
			addressModel.setFromPropertyEntry(vCard.getAdrProperties().get(0));
		}

		noteModel.setFromPropertyEntries(vCard.getNoteProperties());

		nameModel.setFromPropertyEntries(vCard.getFNProperties(), vCard.getNProperties(), vCard.getNicknameProperties(), vCard.getOrgProperties());
		bdayModel.setFromPropertyEntries(vCard.getBDayProperties());
		urlModel.setFromPropertyEntries(vCard.getUrlProperties());
//		urlModel2.setFromPropertyEntries(vCard.getUrlProperties());
		telModel.setFromPropertyEntries(vCard.getTelProperties());
		eMailModel.setFromPropertyEntries(vCard.getEMailProperties());
	}

	public void createVCard() {
		boolean hasFNProperty = false;
		boolean hasOptionalProperties = false;
		boolean atLeastOneException = false;

		VCard vcard;

		List<PropertyEntry> allPropertyEntries = new ArrayList<PropertyEntry>(propertyProvider.size() * 3);
		for (int i = 0; i < propertyProvider.size(); i++) {
			VCardPropertyProvider pp = propertyProvider.get(i);
			try {
				List<PropertyEntry> m = pp.getPropertyEntries();
				for (PropertyEntry entry : m) {
					//assert !optionalProperties.containsKey(entry.getKey()) : "Have: " + optionalProperties.get(entry.getKey()) + ", new: " + entry.getValue();
					if (Property.FN == entry.getProperty()) {
						hasFNProperty = true;
					} else {
						hasOptionalProperties = true;
					}
				}
				allPropertyEntries.addAll(m);
			} catch (IllegalInputException iie) {
				System.err.println(iie);
				atLeastOneException = true;
			}
		}
		if (atLeastOneException) {
			vcard = null;
			assert true : "This VCardModel is supposed to be in a consistent state at all times";
		} else {
			// A VCard v4 has to have an FNProperty, but it can be empty
			if (hasOptionalProperties && !hasFNProperty) {
				allPropertyEntries.add(new FNProperty.Builder(EMPTY_STRING).build());
			}
			// Only create a VCard if there is any non-null property
			if (allPropertyEntries.isEmpty()) {
				vcard = null;
			} else {
				VCard.Builder builder = new VCard.Builder();
				builder.propertyEntry(allPropertyEntries);
				vcard = builder.build();
			}
		}
		this.vCard = vcard;
	}

	public VCard getVCard() {
		return vCard;
	}

	private class ModelListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			vCard = null;
			firePropertyChange(contentChanged);
		}
	}
}
