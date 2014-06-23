package carbon.shell.console;

/**
 * Created by pubudu on 6/5/14.
 */
public class Test {
    public static void main(String[] args) {

        ConsoleRuntime consoleRuntime = ConsoleRuntimeFactory.createConsoleRuntime();
        try {
            consoleRuntime.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
