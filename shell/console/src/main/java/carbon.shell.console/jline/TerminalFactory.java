package carbon.shell.console.jline;

import jline.NoInterruptUnixTerminal;
import jline.Terminal;

/**
 * Created by pubudu on 5/20/14.
 */
public class TerminalFactory {

    private Terminal terminal;

    public synchronized Terminal getTerminal() throws Exception{
        if(terminal==null){
            init();
        }
        return terminal;
    }

    public void init(){
        jline.TerminalFactory.registerFlavor(jline.TerminalFactory.Flavor.UNIX, NoInterruptUnixTerminal.class);
        terminal = jline.TerminalFactory.create();

    }

    public synchronized void destroy()throws Exception{
        if(terminal!=null){
            terminal.restore();
            terminal=null;

        }

    }
}
