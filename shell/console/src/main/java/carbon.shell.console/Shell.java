package carbon.shell.console;



/*
import jline.ArgumentCompletor;
import jline.ConsoleReader;
import jline.SimpleCompletor;
*/

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by pubudu on 5/16/14.
 */
public class Shell  {

//    private String[] commandList;
//
//    public void init(){
//        commandList = new String[]{"help","osgi","server-shutdown"} ;
//    }
//
//    public void run() throws IOException{
//        printWelcomeMessage();
//        ConsoleReader reader = new ConsoleReader();
//        reader.setBellEnabled(false);
//        List completors = new LinkedList();
//
//        completors.add(new SimpleCompletor(commandList));
//        reader.addCompletor(new ArgumentCompletor(completors));
//
//        String line;
//        PrintWriter out = new PrintWriter(System.out);
//
//        while ((line = readLine(reader, "")) != null) {
//            if ("help".equals(line)) {
//                printHelp();
//            } else if ("action1".equals(line)) {
//                System.out.println("You have selection action1");
//            } else if ("action2".equals(line)) {
//                System.out.println("You have selection action2");
//            } else if ("exit".equals(line)) {
//                System.out.println("Exiting application");
//                return;
//            } else {
//                System.out
//                        .println("Invalid command, For assistance press TAB or type \"help\" then hit ENTER.");
//            }
//            out.flush();
//        }
//
//
//
//    }
//    private void printWelcomeMessage() {
//        System.out
//                .println("Welcome to jLine Sample App. For assistance press TAB or type \"help\" then hit ENTER.");
//
//    }
//    private void printHelp() {
//        System.out.println("help - Show help");
//        System.out.println("action1 - Execute action1");
//        System.out.println("action2 - Execute action2");
//        System.out.println("exit - Exit the app");
//
//    }
//    private String readLine(ConsoleReader reader, String promtMessage)
//            throws IOException {
//        String line = reader.readLine(promtMessage + "\nshell> ");
//        return line.trim();
//    }
//
//    public static void main(String[] args) throws IOException {
//        Shell shell = new Shell();
//        shell.init();
//        shell.run();
//    }

}
