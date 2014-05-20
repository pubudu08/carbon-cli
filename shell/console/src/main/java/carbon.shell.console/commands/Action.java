package carbon.shell.console.commands;

import org.apache.felix.service.command.CommandSession;


/**
 * Created by pubudu on 5/20/14.
 */
public interface Action  {

    Object execute(CommandSession session)throws Exception;
}
