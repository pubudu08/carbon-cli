package carbon.shell.console;



/**
 * Created by pubudu on 6/5/14.
 */
public class ConsoleRuntimeFactory {

    /**
     *
     * @return
     */
    public static ConsoleRuntime createConsoleRuntime(){

        ConsoleRuntime consoleRuntime = new ConsoleLauncher();
        return  consoleRuntime;

    }

}
