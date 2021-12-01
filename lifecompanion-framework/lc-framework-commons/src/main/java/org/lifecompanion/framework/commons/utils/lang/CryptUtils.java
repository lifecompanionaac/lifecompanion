/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2021 CMRRF KERPAPE (Lorient, France)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.lifecompanion.framework.commons.utils.lang;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that keep all the crypt/encode/decode method
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class CryptUtils {
	private final static Logger logger = LoggerFactory.getLogger(CryptUtils.class);

	private CryptUtils() {}

	/**
	 * To encode a string.<br>
	 * To from <a href="http://stackoverflow.com/questions/5220761/fast-and-simple-string-encrypt-decrypt-in-java">StackOverflow</a>
	 * @param text the text to encode
	 * @return the encoded text
	 */
	public static String encodeString(final String password, final String text) {
		try {
			DESKeySpec keySpec = new DESKeySpec(password.getBytes("UTF8"));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey key = keyFactory.generateSecret(keySpec);
			byte[] cleartext = text.getBytes("UTF8");
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return new String(Base64.getEncoder().encode(cipher.doFinal(cleartext)));
		} catch (Exception e) {
			CryptUtils.logger.error("Problem while encoding a text.", e);
			return CryptUtils.xorEncrypt(text, password);
		}
	}

	/**
	 * To decode a string.<br>
	 * From <a href="http://stackoverflow.com/questions/5220761/fast-and-simple-string-encrypt-decrypt-in-java">StackOverflow</a>
	 * @param text the text to decode
	 * @return the decoded text
	 */
	public static String decodeString(final String password, final String text) {
		try {
			DESKeySpec keySpec = new DESKeySpec(password.getBytes("UTF8"));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey key = keyFactory.generateSecret(keySpec);
			byte[] encrypedPwdBytes = Base64.getDecoder().decode(text);
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] txtBytes = cipher.doFinal(encrypedPwdBytes);
			return new String(txtBytes, "UTF8");
		} catch (Exception e) {
			CryptUtils.logger.error("Problem while decoding a text.", e);
			return CryptUtils.xorDecrypt(text, password);
		}
	}

	/**
	 * Save Base64 encoded XML
	 * @param xmlElement the xml element to save as encoded String
	 * @param path the path to the saved file
	 * @throws UnsupportedEncodingException if saving fail
	 * @throws IOException if saving fail
	 */
	public static void saveEncodedXML(final Element xmlElement, final File path) throws UnsupportedEncodingException, IOException {
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		String xml = out.outputString(xmlElement);
		FileOutputStream fos = new FileOutputStream(path);
		fos.write(Base64.getEncoder().encode(xml.getBytes("UTF-8")));
		fos.close();
	}

	/**
	 * Load Base64 encoded XML
	 * @param path the path to the encoded XML file
	 * @return the decoded xml element
	 * @throws JDOMException if loading fail
	 * @throws IOException if loading fail
	 */
	public static Element loadEncodedXML(final File path) throws JDOMException, IOException {
		String xmlb64 = IOUtils.getFileContent(path);
		String xmlDecoded = new String(Base64.getDecoder().decode(xmlb64.getBytes("UTF-8")));
		SAXBuilder sxb = new SAXBuilder();
		Document doc = sxb.build(new StringReader(xmlDecoded));
		Element root = doc.getRootElement();
		return root;
	}

	/**
	 * Hash a text with SHA256
	 * @param txt text to hash
	 * @return the hash, or null if a exception happen
	 */
	public static String getSHA256(final String txt) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(txt.getBytes());
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Base64 object encoding
	 * @param o the object to encode, must be serializable
	 * @return the string that represent this object, null if fail
	 */
	public static String encodeObject(final Object o) {
		try {
			//Saving into stream
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(o);
			oos.close();
			//Convert to Base64
			String str = new String(Base64.getEncoder().encode(baos.toByteArray()), "UTF-8");
			return str;
		} catch (Exception e) {
			CryptUtils.logger.error("Couldn't encode the given object as String", e);
			return null;
		}
	}

	/**
	 * Base64 object decoding
	 * @param str the string that contains object
	 * @return the read object, null if fail
	 */
	public static Object decodeObject(final String str) {
		try {
			//Decode bytes
			ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(str.getBytes("UTF-8")));
			//Get object
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object o = ois.readObject();
			ois.close();
			return o;
		} catch (Exception e) {
			CryptUtils.logger.error("Couldn't decode the String String in Object", e);
			return null;
		}
	}

	/**
	 * Basic function to do xor message encoding if strong encryption fail
	 */
	public static String xorEncrypt(final String message, final String key) {
		try {
			if (message == null || key == null) {
				return null;
			}

			char[] keys = key.toCharArray();
			char[] mesg = message.toCharArray();

			int ml = mesg.length;
			int kl = keys.length;
			char[] newmsg = new char[ml];

			for (int i = 0; i < ml; i++) {
				newmsg[i] = (char) (mesg[i] ^ keys[i % kl]);
			}
			mesg = null;
			keys = null;
			String temp = new String(newmsg);
			return new String(Base64.getEncoder().encode(temp.getBytes()));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Basic function to do xor message decoding if strong encryption fail
	 */
	public static String xorDecrypt(String message, final String key) {
		try {
			if (StringUtils.isBlank(message) || key == null) {
				return null;
			}
			char[] keys = key.toCharArray();
			message = new String(Base64.getDecoder().decode(message));
			char[] mesg = message.toCharArray();

			int ml = mesg.length;
			int kl = keys.length;
			char[] newmsg = new char[ml];

			for (int i = 0; i < ml; i++) {
				newmsg[i] = (char) (mesg[i] ^ keys[i % kl]);
			}
			mesg = null;
			keys = null;
			return new String(newmsg);
		} catch (Exception e) {
			return null;
		}
	}
}
