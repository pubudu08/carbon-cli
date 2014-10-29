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

import carbon.shell.console.Console;
import carbon.shell.console.commands.AbstractCommand;
import carbon.shell.console.commands.Action;
import carbon.shell.console.commands.Command;
import carbon.shell.console.jline.TerminalFactory;
import jline.Terminal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.gogo.runtime.CommandProcessorImpl;
import org.apache.felix.gogo.runtime.threadio.ThreadIOImpl;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Function;
import org.fusesource.jansi.AnsiConsole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;

public class Main {
	private static Log LOGGER = LogFactory.getLog(Main.class);
	private String application = "carbon";
	private String user = "root";


	/**
	 * Will build custom console for a given instructions
	 * @param args
	 * @throws Exception
	 */
	public void run(String args[]) throws Exception {

		ThreadIOImpl threadIO = new ThreadIOImpl();
		threadIO.start();
		InputStream in = unwrap(System.in);
		PrintStream out = wrap(unwrap(System.out));
		PrintStream err = wrap(unwrap(System.err));
		CommandProcessorImpl commandProcessor = new CommandProcessorImpl(threadIO);
		ClassLoader classLoader = Main.class.getClassLoader();

		//Discover custom carbon.shell.console.commands by going
		// through the text file 'classPath.txt'
		discoverCommands(commandProcessor, classLoader);
		final TerminalFactory terminalFactory = new TerminalFactory();
		final Terminal terminal = terminalFactory.getTerminal();
		Console console = new Console(commandProcessor, terminal, in, out, err);
		CommandSession session = console.getSession();
		session.put("USER", user);
		session.put("APPLICATION", application);
		session.put(".jline.terminal", terminal);
		console.init(session);
		console.run();
		terminalFactory.destroy();
	}

	/**
	 * This method responsible of discovering commands via text file
	 * @param commandProcessor
	 * @param classLoader
	 */
	public void discoverCommands(CommandProcessorImpl commandProcessor,
	                             ClassLoader classLoader)
	  throws ClassNotFoundException,IOException {
		Enumeration<URL> urlEnumeration = classLoader.getResources("META-INF/command");
		while (urlEnumeration.hasMoreElements()) {
			URL url = urlEnumeration.nextElement();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url
			  .openStream()));
			String line = bufferedReader.readLine();
			while (line != null) {
				line = line.trim();
				if (line.length() > 0 && line.charAt(0) != '#') {
					final Class<Action> actionClass = (Class<Action>) classLoader.loadClass(line);
					Command command = actionClass.getAnnotation(Command.class);
					Function function = new AbstractCommand() {
						@Override
						public Action createNewAction() {
							try {
								return ((Class<? extends Action>) actionClass).newInstance();
							} catch (InstantiationException e) {
								throw new RuntimeException(e);
							} catch (IllegalAccessException e) {
								throw new RuntimeException(e);
							}
						}
					};
					addCommand(command, function, commandProcessor);
				}
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
		}
	}


	/**
	 * @param command
	 * @param function
	 * @param commandProcessor
	 */
	protected void addCommand(Command command, Function function,
	                          CommandProcessorImpl commandProcessor) {
		commandProcessor.addCommand(command.scope(), function, command.name());
	}

	/**
	 * Sample Main method to test the console feature.
	 * @param args
	 */
	public static void main(String[] args) {
		Main main = new Main();
		try {
			main.run(args);
		} catch (Exception exception) {
			LOGGER.error(" Console cannot be rendered,",exception );
		}
	}

	/**
	 *
	 * @param printStream
	 * @return
	 */
	private static PrintStream wrap(PrintStream printStream) {
		OutputStream outputStream = AnsiConsole.wrapOutputStream(printStream);
		if (outputStream instanceof PrintStream) {
			return ((PrintStream) outputStream);
		} else {
			return new PrintStream(outputStream);
		}
	}

	/**
	 *
	 * @param stream
	 * @param <T>
	 * @return
	 */
	private static <T> T unwrap(T stream) {
		try {
			Method mth = stream.getClass().getMethod("getRoot");
			return (T) mth.invoke(stream);
		} catch (Throwable t) {
			return stream;
		}
	}
}


