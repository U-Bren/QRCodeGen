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
@javax.xml.bind.annotation.XmlSchema(
	namespace = "http://www.opengis.net/kml/2.2", 
        xmlns = { @javax.xml.bind.annotation.XmlNs( prefix = "gx",
                  namespaceURI = "http://www.google.com/kml/ext/2.2" ),
		@javax.xml.bind.annotation.XmlNs( prefix = "kml",
                  namespaceURI = "http://www.google.com/kml/2.2" ),
		@javax.xml.bind.annotation.XmlNs( prefix = "atom",
                  namespaceURI = "http://www.w3.org/2005/Atom" ),},
	elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED)
package qrcodegen.kml;
