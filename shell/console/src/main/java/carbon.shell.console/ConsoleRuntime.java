package carbon.shell.console;

import org.apache.felix.gogo.runtime.CommandProcessorImpl;

import java.io.IOException;

/**
 *
 */
public interface ConsoleRuntime {
    /**
     *
     * @return
     * @throws java.io.IOException
     */
    public void initiateConsole() throws Exception;

    /**
     *
     * @param promptString
     * @return
     */
    public void initiateBasicCommands(CommandProcessorImpl commandProcessor, ClassLoader classLoader) throws IOException, ClassNotFoundException;

    /**
     *
     */
    public void run() throws Exception ;



}
