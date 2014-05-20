package carbon.shell.console.commands;

import org.apache.felix.service.command.CommandSession;

/**
 * Created by pubudu on 5/20/14.
 */
public abstract class AbstractAction implements Action{
   // protected final Logger log = LoggerFactory.getLogger(getClass());
    protected CommandSession session;

    public Object execute(CommandSession session) throws Exception {
        this.session = session;
        return doExecute();
    }

    protected abstract Object doExecute() throws Exception;
}
