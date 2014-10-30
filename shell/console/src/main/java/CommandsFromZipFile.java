/*
*  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

import jline.console.ConsoleReader;
import jline.console.completer.FileNameCompleter;
import jline.console.completer.StringsCompleter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fusesource.jansi.Ansi;

import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class CommandsFromZipFile {
	private static Log LOGGER = LogFactory.getLog(CommandsFromZipFile.class);

	/**
	 *  Sample test to retrieve command value using zip file
	 * @param args
	 */
	public static void main(String[] args) {
		//by default it sets to Ctrl+D
		System.setProperty("jline.shutdownhook", "true");
		try {
			ConsoleReader consoleReader = new ConsoleReader();
			consoleReader.setPrompt("carbon>");
			consoleReader.addCompleter(new StringsCompleter(IOUtils.readLines(new GZIPInputStream
			  (CommandsFromZipFile.class.getResourceAsStream("commandList.txt.gz")))));
			consoleReader.addCompleter(new FileNameCompleter());
			String line = "";
			String colored = "";

			while ((line = consoleReader.readLine()) != null) {
				if ("clear".equals(line.trim())) {
					System.out.print("\33[2J");
					System.out.flush();
					System.out.print("\33[1;1H");
					System.out.flush();
				} else if ("aback".equals(line.trim())) {
					colored = Ansi.ansi().fg(Ansi.Color.RED).a("Entered command : ").a(Ansi
					  .Attribute.INTENSITY_BOLD).a(line).a(Ansi.Attribute.INTENSITY_BOLD_OFF).fg
					  (Ansi.Color.DEFAULT).toString();
					consoleReader.println(colored);
				} else {
					consoleReader.println(line);
				}
			}
		} catch (IOException exception) {
			LOGGER.error(" Unable to load commands from the zip file ", exception);
		} finally {
			try {
				jline.TerminalFactory.get().restore();
			} catch (Exception exception) {
				LOGGER.error(" Unable to restore the terminal ", exception);
			}
		}
	}
}
