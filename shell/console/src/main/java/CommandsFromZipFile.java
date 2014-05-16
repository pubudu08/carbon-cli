import jline.TerminalFactory;
import jline.console.ConsoleReader;
import jline.console.completer.FileNameCompleter;
import jline.console.completer.StringsCompleter;
import org.apache.commons.io.IOUtils;



import java.io.IOException;
import java.util.zip.GZIPInputStream;



/**
 * Created by pubudu on 5/16/14.
 */
public class CommandsFromZipFile {
    public static void main(String[] args)  {
        //by default it sets to Ctrl+D
        System.setProperty("jline.shutdownhook","true");
        try{
            ConsoleReader consoleReader = new ConsoleReader();
            consoleReader.setPrompt("carbon>");
            consoleReader.addCompleter(
                    new StringsCompleter(
                            IOUtils.readLines(new GZIPInputStream(CommandsFromZipFile.class.getResourceAsStream("wordlist.txt.gz")))
                    )
            );
            consoleReader.addCompleter(new FileNameCompleter());

            String line = "";
            while ((line=consoleReader.readLine())!=null){
                consoleReader.println(line);
            }

        }catch(IOException exception){
               exception.printStackTrace();
        } finally{
            try {
                TerminalFactory.get().restore();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}
