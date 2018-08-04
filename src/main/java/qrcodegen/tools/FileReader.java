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
package qrcodegen.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 *
 * @author Stefan Ganzer
 */
public class FileReader {
	
	private static final String NEWLINE = String.format("%n"); //NOI18N
	private final File file;
	private final Charset charset;
	private String content;
	
	public FileReader(File f, Charset c){
		if(f == null){
			throw new NullPointerException();
		}
		if(c == null){
			throw new NullPointerException();
		}
		this.file = f;
		this.charset = c;
	}
	
	public void readFile() throws FileNotFoundException, IOException{
		BufferedReader reader = null;
		final int initialSize = file.length() >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) file.length();
		StringBuilder sb = new StringBuilder(initialSize);
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
			while(true){
				String line = reader.readLine();
				if(line == null){
					break;
				}
				sb.append(line).append(NEWLINE);
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ioe) {
					//swallowed
				}
			}
		}
		content = sb.toString();
	}
	
	public String getContent(){
		return content;
	}
}
