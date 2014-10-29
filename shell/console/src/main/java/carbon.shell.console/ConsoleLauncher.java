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


import carbon.shell.console.commands.AbstractCommand;
import carbon.shell.console.commands.Action;
import carbon.shell.console.commands.Command;
import carbon.shell.console.jline.TerminalFactory;
import jline.Terminal;
import org.apache.felix.gogo.runtime.CommandProcessorImpl;
import org.apache.felix.gogo.runtime.threadio.ThreadIOImpl;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Function;
import org.fusesource.jansi.AnsiConsole;

import java.io.*;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;

public class ConsoleLauncher implements ConsoleRuntime {

    private final String USER= "admin";
    private final String APPLICATION = "carbon";

    private TerminalFactory terminalFactory;
    private Terminal terminal;
    private CommandSession session;
    private Console console;

    /**
     *
     * @throws Exception
     */
    public void initiateConsole () throws Exception {

        ThreadIOImpl threadIO = new ThreadIOImpl() ;
        threadIO.start();

        InputStream in = unwrap(System.in);
        PrintStream out = wrap(unwrap(System.out));
        PrintStream err = wrap(unwrap(System.err));

        CommandProcessorImpl commandProcessor = new CommandProcessorImpl(threadIO);
        ClassLoader classLoader = Test.class.getClassLoader();

        initiateBasicCommands(commandProcessor, classLoader);

        terminalFactory = new TerminalFactory();
        terminal = terminalFactory.getTerminal();

        console =new Console(commandProcessor,terminal,in,out,err);
        session = console.getSession();

    }

    /**
     *
     * @param commandProcessor
     * @param classLoader
     * @throws java.io.IOException
     */
    public void initiateBasicCommands(CommandProcessorImpl commandProcessor, ClassLoader classLoader) throws IOException, ClassNotFoundException {
        Enumeration<URL> urlEnumeration  = classLoader.getResources("META-INF/command") ;
        while (urlEnumeration.hasMoreElements()){

            URL url = urlEnumeration.nextElement();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = bufferedReader.readLine();
            while (line !=null){
                line = line.trim();
                if(line.length()>0 && line.charAt(0) !='#'){
                    final Class <Action> actionClass = (Class<Action>) classLoader.loadClass(line);
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
                    } ;

                    //Adding commands
                    commandProcessor.addCommand(command.scope(),function,command.name());
                }
                line = bufferedReader.readLine();

            }
            bufferedReader.close();
        }
    }

    /**
     *
     * @throws Exception
     */
    public void run() throws Exception {
        initiateConsole();
        session.put("USER", USER);
        session.put("APPLICATION", APPLICATION);
        session.put(".jline.terminal", terminal);

        console.init(session);
        console.run();
        terminalFactory.destroy();

    }

    private static PrintStream wrap(PrintStream printStream) {
        // AnsiConsole Print Stream wrapper
        OutputStream outputStream = AnsiConsole.wrapOutputStream(printStream);
        if (outputStream instanceof PrintStream) {
            return ((PrintStream) outputStream);
        } else {
            return new PrintStream(outputStream);
        }
    }

    private static <T> T unwrap(T stream) {
        try {
            Method mth = stream.getClass().getMethod("getRoot");
            return (T) mth.invoke(stream);
        } catch (Throwable t) {
            return stream;
        }
    }
}
