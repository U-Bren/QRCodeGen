# QRCodeGen
QRCodeGen is a portable java application providing advanced options for generating QRCodes.

Forked from https://sites.google.com/site/qrcodeforwn/

(Original description: )
The 'Portable QR-Code Generator' is a free Java program which generates QR Codes from 

* WLAN credentials: SSID, network key and and network type (WEP, WPA/WPA2, not encrypted)
	The so-called 'WiFi-Code' can optionally be printed as a folding card: after folding the printout the QR Code is on the front 	side, the credentials in plain text on the back side - this way you have all the information available you need to connect a 		device to the network, regardless of whether the device can read and interpret the code or not.
        The orientation of both the code and the text can be changed, also the font and the font size.
* VCard-Import
	Inlined images can optionally be removed to reduce the size of the generated QR Code
	VCards up to version 3 (inclusive) are supported
	You can import the cards via drag&drop or by selecting them in a file chooser dialog
	
* VCard generator
	Generates VCards of the upcoming v4 (RFC 6350). New: Saving and loading of VCards.
* Email addresses
* URLs
* Free text
* Geographic coordinates

Supports a broad range of character encodings (ISO-8859-1, UTF-8, UTF-16, ...) - the actual number depends on the Java Virtual Machine you are using.



The QR Code can be printed, saved as BMP, GIF or PNG, or copied to clipboard to use in other applications.

