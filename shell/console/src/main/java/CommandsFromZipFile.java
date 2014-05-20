
import jline.console.ConsoleReader;
import jline.console.completer.FileNameCompleter;
import jline.console.completer.StringsCompleter;
import org.apache.commons.io.IOUtils;
import org.fusesource.jansi.Ansi;

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
                            IOUtils.readLines(new GZIPInputStream(CommandsFromZipFile.class.getResourceAsStream("commandList.txt.gz")))
                    )
            );
            consoleReader.addCompleter(new FileNameCompleter());

            String line = "";
            String colored ="";
            while ((line=consoleReader.readLine())!=null){

                if("clear".equals(line.trim())){
                    System.out.print("\33[2J");
                    System.out.flush();
                    System.out.print("\33[1;1H");
                    System.out.flush();
                }else if("aback".equals(line.trim())){
                    colored = Ansi.ansi()
                            .fg(Ansi.Color.RED)
                            .a("Entered command : ")
                            .a(Ansi.Attribute.INTENSITY_BOLD)
                            .a(line)
                            .a(Ansi.Attribute.INTENSITY_BOLD_OFF)
                            .fg(Ansi.Color.DEFAULT).toString();
                    consoleReader.println(colored);
                }else{
                   consoleReader.println(line);
                }



            }

        }catch(IOException exception){
               exception.printStackTrace();
        } finally{
            try {
                jline.TerminalFactory.get().restore();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}
