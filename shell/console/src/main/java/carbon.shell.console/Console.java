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
package carbon.shell.console;

import carbon.shell.console.jline.CarbonHistory;
import carbon.shell.console.jline.CommandsCompleter;
import jline.Terminal;
import jline.UnsupportedTerminal;
import jline.console.ConsoleReader;
import jline.console.history.MemoryHistory;
import jline.console.history.PersistentHistory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.gogo.runtime.CommandNotFoundException;
import org.apache.felix.gogo.runtime.CommandProcessorImpl;
import org.apache.felix.gogo.runtime.Parser;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Converter;
import org.fusesource.jansi.Ansi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.concurrent.BlockingQueue;

public class Console implements Runnable {
	private static Log LOGGER = LogFactory.getLog(Console.class);

	private CommandProcessorImpl commandProcessor;
	private Terminal terminal;
	protected CommandSession session;
	private ConsoleReader reader;
	private Thread thread;
	private InputStream consoleInput;
	private InputStream in;
	private PrintStream out;
	private PrintStream err;
	private Thread pipe;
	private boolean running;
	private boolean interrupt;
	private BlockingQueue<Integer> queue;

	/**
	 * Console Constructor
	 *
	 * @param commandProcessor
	 * @param terminal
	 * @param in
	 * @param out
	 * @param err
	 * @throws IOException
	 */
	public Console(CommandProcessorImpl commandProcessor, Terminal terminal, InputStream in,
	               PrintStream out, PrintStream err) throws IOException {
		this.in = in;
		this.out = out;
		this.err = err;
		this.commandProcessor = commandProcessor;
		this.terminal = terminal == null ? new UnsupportedTerminal() : terminal;
		this.session = commandProcessor.createSession(this.consoleInput, this.out, this.err);
	}

	/**
	 * Retrieve console session object
	 *
	 * @return
	 */
	public CommandSession getSession() {
		return session;
	}

	/**
	 * Thread interrupt
	 */
	private void interrupt() {
		interrupt = true;
		thread.interrupt();
	}

	/**
	 * Initialization of Console instant using a given CommandSession
	 *
	 * @param session
	 * @throws IOException
	 */
	public void init(CommandSession session) throws IOException {
		reader = new ConsoleReader(this.in, this.out, this.terminal);
		reader.setPrompt(createPrompt(session.get("USER") + "@" + session.get("APPLICATION")));
		reader.addCompleter(new CommandsCompleter(session));

		File file = getHistoryFile();
		try {
			file.getParentFile().mkdir();
			file.createNewFile();
			reader.setHistory(new CarbonHistory(file));
		} catch (Exception e) {
			LOGGER.error("Can not read history from file " + file + ". Using in memory history",
			  e);
		}
		if (reader != null && reader.getHistory() instanceof MemoryHistory) {
			//TODO implement if Shell history maxvalue reached, tell carbon to use MemoryHistory
		}
		session.put(".jline.reader", reader);
		session.put(".jline.history", reader.getHistory());
		pipe = new Thread();
		pipe.setName("gogo shell pipe thread");
		pipe.setDaemon(true);
	}

	@Override
	public void run() {
		thread = Thread.currentThread();
		running = true;
		pipe.start();
		welcome();
		while (running) {
			try {
				String command = readAndParseCommand();
				if (command == null) {
					break;
				}
				Object result = session.execute(command);
				closeConsole();
				if (result != null) {
					session.getConsole().println(session.format(result, Converter.INSPECT));
				}
			} catch (IOException e) {
				LOGGER.error("Unable to read commands form text file, ", e);
			} catch (Throwable throwable) {
				logException(throwable);
			}


		}

	}

	/**
	 * Terminate console
	 */
	public void closeConsole() {
		if (!running) {
			return;
		}
		if (reader.getHistory() instanceof PersistentHistory) {
			try {
				((PersistentHistory) reader.getHistory()).flush();
			} catch (IOException e) {
				LOGGER.error("Unable to terminate console,", e);
			}
		}
	}

	/**
	 * Read and include commands in to the command processor
	 *
	 * @return
	 * @throws IOException
	 */
	private String readAndParseCommand() throws IOException {
		String command = null;
		boolean loop = true;
		boolean first = true;
		while (loop) {
			//TODO checkInterrupt() issue is, if VK_ESCAPE keystroke happens whole program wil not
			// proceed
			//checkInterrupt();
			String line = reader.readLine();
			if (line == null) {
				break;
			}
			if (command == null) {
				command = line;
			} else {
				command += " " + line;
			}
			if (reader.getHistory().size() == 0) {
				reader.getHistory().add(command);
			} else {
				// jline doesn't add blank lines to the history so we don't
				// need to replace the command in jline's console history with
				// an indented one
				if (command.length() > 0 && !" ".equals(command)) {
					reader.getHistory().replace(command);
				}
			}
			try {
				new Parser(command).program();
				loop = false;
			} catch (Exception e) {
				loop = true;
				first = false;
			}
		}
		return command;
	}

	/**
	 * Return Carbon History file
	 *
	 * @return
	 */
	public File getHistoryFile() {
		String carbonCommandHistory = new File(System.getProperty("user.home"),
		  ".carbon/carbon.history").toString();
		return new File(System.getProperty("carbon.history", carbonCommandHistory));
	}

	/**
	 * Log console based info/error/warn as ANSI logs
	 *
	 * @param throwable
	 */
	private void logException(Throwable throwable) {
		if (throwable instanceof CommandNotFoundException) {
			String str = Ansi.ansi().fg(Ansi.Color.RED).a("[Info] ").a(Ansi.Attribute
			  .INTENSITY_BOLD).a(((CommandNotFoundException) throwable).getCommand()).a(Ansi
			  .Attribute.INTENSITY_BOLD_OFF).a(", Command not found. Please input valid command.")
			  .fg(Ansi.Color.DEFAULT).toString();
			session.getConsole().println(str);
		}


	}

	/**
	 * Basic welcome message
	 * #TODO read via using a config
	 */
	protected void welcome() {
		//TODO welcome msg
		String msg = Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a("Carbon 5 (C5)").a(Ansi
		  .Attribute.INTENSITY_BOLD_OFF).a(" - The next generation of WSO2 Carbon Platform")
		  .newline().toString();
		System.out.println("\n" + msg);

	}

	/**
	 * Construct a prompt based on the user
	 *
	 * @param promptString
	 * @return
	 */
	private String createPrompt(String promptString) {
		String prompt = Ansi.ansi().fg(Ansi.Color.BLUE).a(Ansi.Attribute.INTENSITY_BOLD).a
		  (promptString + ">").a(Ansi.Attribute.INTENSITY_BOLD_OFF).fg(Ansi.Color.DEFAULT)
		  .toString();
		return prompt;
	}
}
