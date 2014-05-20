import carbon.shell.console.Console;
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


/**
 * Created by pubudu on 5/20/14.
 */
public class Main {
    private String application ="root";
    private String user = "carbon";


    /**
     *
     * @param args
     * @throws Exception
     */
    public void run(String args[]) throws Exception{

        ThreadIOImpl threadIO = new ThreadIOImpl() ;
        threadIO.start();
        InputStream in = unwrap(System.in);
        PrintStream out = wrap(unwrap(System.out));
        PrintStream err = wrap(unwrap(System.err));
        CommandProcessorImpl commandProcessor = new CommandProcessorImpl(threadIO);
        ClassLoader classLoader = Main.class.getClassLoader();

        //Discover custom carbon.shell.console.commands by going through the text file 'classPath.txt'
        discoverCommands(commandProcessor, classLoader);
        final TerminalFactory terminalFactory = new TerminalFactory();
        final Terminal terminal = terminalFactory.getTerminal();
        Console console =new Console(commandProcessor,terminal,in,out,err);
        CommandSession session = console.getSession();
        session.put("USER", user);
        session.put("APPLICATION", application);
        session.put(".jline.terminal", terminal);
        console.run();
        terminalFactory.destroy();
    }
    private static PrintStream wrap(PrintStream stream) {
        OutputStream o = AnsiConsole.wrapOutputStream(stream);
        if (o instanceof PrintStream) {
            return ((PrintStream) o);
        } else {
            return new PrintStream(o);
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

    /**
     *
     * @param commandProcessor
     * @param classLoader
     */
    public void discoverCommands(CommandProcessorImpl commandProcessor, ClassLoader classLoader) throws ClassNotFoundException,IOException{
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
                    addCommand(command, function, commandProcessor);


                }
                line = bufferedReader.readLine();

            }
            bufferedReader.close();
        }


    }



    /**
     *
     * @param command
     * @param function
     * @param commandProcessor
     */
    protected void addCommand(Command command,Function function,CommandProcessorImpl commandProcessor){
        commandProcessor.addCommand(command.scope(),function,command.name());

    }
    public static void main(String[] args) {
        System.setProperty("jline.shutdownhook","true");
        Main main = new Main();
        try {
            main.run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


