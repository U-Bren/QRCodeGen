/*
 * Copyright (C) 2013 Stefan Ganzer
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
package qrcodegen.kml;

import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Stefan Ganzer
 */
@XmlRootElement //(namespace="http://www.opengis.net/kml/2.2")
public class Kml {

	private Placemark placemark;
	private Folder folder;
	private Document document;

	public void setPlacemark(Placemark pm) {
		this.placemark = pm;
	}

	@XmlElement( name = "Placemark")
	public Placemark getPlacemark() {
		return placemark;
	}

	public void setFolder(Folder folder) {
		this.folder = folder;
	}

	@XmlElement( name = "Folder")
	public Folder getFolder() {
		return folder;
	}

	public void setDocument(Document d) {
		this.document = d;
	}

	@XmlElement( name = "Document")
	public Document getDocument() {
		return document;
	}

	@XmlTransient
	public Placemark getFirstPlacemark() {
		if (getPlacemark() != null) {
			return getPlacemark();
		}
		if (getFolder() != null) {
			Placemark pm = getFirstPlacemark(Arrays.asList(getFolder()));
			if (pm != null) {
				return pm;
			}
		}
		Document doc = getDocument();
		if (doc != null) {
			List<Placemark> pms = doc.getPlacemarks();
			if (pms != null && !pms.isEmpty()) {
				return pms.get(0);
			}
			List<Folder> f = doc.getFolders();
			if (f != null) {
				return getFirstPlacemark(f);
			}
		}
		return null;

	}

	private Placemark getFirstPlacemark(List<Folder> folders) {
		Placemark result = null;
		for (Folder f : folders) {
			List<Placemark> pm = f.getPlacemarks();
			if (pm != null && !pm.isEmpty()) {
				result = pm.get(0);
				break;
			}
			result = getFirstPlacemark(f.getFolders());
			if (result != null) {
				break;
			}
			List<Document> docs = f.getDocuments();
			// TODO Dive into docs
		}
		return result;

	}

	@Override
	public String toString() {
		return "KML Root";
	}
}
