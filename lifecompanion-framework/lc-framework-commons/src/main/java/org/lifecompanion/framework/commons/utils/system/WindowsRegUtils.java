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

package org.lifecompanion.framework.commons.utils.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tool class to modify Windows registry.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class WindowsRegUtils {
	private static final String WINDOWS_REG_SEP = "    ";

	private static Logger LOGGER = LoggerFactory.getLogger(WindowsRegUtils.class);

	//Only static methods
	private WindowsRegUtils() {}

	// TODO : add different command for edit/open (open -> launch, edit -> configure)

	/**
	 * Associate the given extension to the given executable path.<br>
	 * This method should be called with Windows admin privileges.<br>
	 * Running it on a Unix system may have unknowns consequences.
	 * @param extension the file extension (without ".")
	 * @param executableFile the path to the executable file to associate
	 * @param softwareName the software name (should respect Windows registry naming convention : "MyCompany.MySoftware")
	 * @param description the description for the file type
	 * @throws WindowsRegException if the association can't be created
	 */
	public static void createFileAssociation(final String extension, final File executableFile, final String softwareName, final String description)
			throws WindowsRegException {
		String typeName = softwareName + ".1";
		WindowsRegUtils.addRegKey("HKEY_CLASSES_ROOT\\." + extension, null, "REG_SZ", typeName);
		WindowsRegUtils.addRegKey("HKEY_CLASSES_ROOT\\." + extension, "PerceivedType", "REG_SZ", "Application");
		WindowsRegUtils.addRegKey("HKEY_CLASSES_ROOT\\" + typeName, null, "REG_SZ", null);
		WindowsRegUtils.addRegKey("HKEY_CLASSES_ROOT\\" + typeName + "\\", null, "REG_SZ", description);
		WindowsRegUtils.addRegKey("HKEY_CLASSES_ROOT\\" + typeName + "\\DefaultIcon", null, "REG_SZ",
				"\\\"" + executableFile.getAbsolutePath() + "\\\",0");
		String openEditCmdStr = "\\\"" + executableFile.getAbsolutePath() + "\\\" \\\"%1\\\" %*";
		WindowsRegUtils.addRegKey("HKEY_CLASSES_ROOT\\" + typeName + "\\shell\\open\\command", null, "REG_SZ", openEditCmdStr);
		// addRegKey("HKEY_CLASSES_ROOT\\" + typeName + "\\shell\\edit\\command", null, "REG_SZ", openEditCmdStr);
	}

	public static String[] executeRegeditCmd(String cmd, List<String> args, boolean readResult) throws WindowsRegException {
		// Create cmd
		ArrayList<String> cmds = new ArrayList<>();
		cmds.add("reg");
		cmds.add(cmd);
		cmds.addAll(args);

		// Prepare stream files
		try {
			File tmpFileErr = File.createTempFile("reg-cmd-result", ".txt");
			File tmpFileOut = readResult ? File.createTempFile("reg-cmd-result", ".txt") : null;
			Process process = new ProcessBuilder().command(cmds).redirectError(Redirect.to(tmpFileErr))
					.redirectOutput(readResult ? Redirect.to(tmpFileOut) : Redirect.DISCARD).start();
			int result = process.waitFor();
			if (result != 0) {
				throw new WindowsRegException("Windows reg command error\n" + IOUtils.readFileLines(tmpFileErr, "UTF-8") + "\n returned " + result);
			} else {
				return readResult ? IOUtils.readFileLines(tmpFileOut, "UTF-8").split(WINDOWS_REG_SEP) : null;
			}
		} catch (InterruptedException | IOException e) {
			LOGGER.error("Problem calling Windows registry command", e);
			throw new WindowsRegException("Problem calling Windows registry command");
		}
	}

	public static String getRegKeyValue(final String keyName, final String valueName) {
		return valueName;
		//HKEY_CURRENT_USER\SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\Shell Folders - Personal or Desktop
	}

	private static void addRegKey(final String keyName, final String valueName, final String dataType, final String data) throws WindowsRegException {
		// Cmds array
		List<String> cmds = new ArrayList<>();
		cmds.add("reg");
		cmds.add("add");
		cmds.add(keyName);
		cmds.add("/f");// Never ask confirm
		// Optionnal parameters
		WindowsRegUtils.addOption(cmds, "v", valueName);
		WindowsRegUtils.addOption(cmds, "t", dataType);
		WindowsRegUtils.addOption(cmds, "d", data);
		// Execute
		WindowsRegUtils.LOGGER.info("Will execute the reg add command : {}", cmds);
		Process exec;
		try {
			exec = Runtime.getRuntime().exec(cmds.toArray(new String[cmds.size()]));
		} catch (IOException e) {
			throw new WindowsRegException("Can't run the reg command");
		}
		String errorMsg = WindowsRegUtils.readAllStream(exec.getErrorStream());
		WindowsRegUtils.readAllStream(exec.getInputStream());
		//LOGGER.info("Registry add output result : {}", outMsg);
		int result;
		try {
			result = exec.waitFor();
		} catch (InterruptedException e) {
			throw new WindowsRegException("Can't wait for reg command to end");
		}
		if (result != 0) {
			throw new WindowsRegException(errorMsg + " (" + result + ")");
		}
	}

	private static void addOption(final List<String> cmds, final String option, final String value) {
		if (value != null) {
			cmds.add("/" + option);
			cmds.add(value);
		}
	}

	//reg query HKU\S-1-5-19

	private static String readAllStream(final InputStream is) {
		StringBuilder sb = new StringBuilder();
		try (Scanner scan = new Scanner(is)) {
			while (scan.hasNextLine()) {
				sb.append(scan.nextLine()).append(scan.hasNextLine() ? "\n" : "");
			}
		}
		return sb.toString();
	}
}
