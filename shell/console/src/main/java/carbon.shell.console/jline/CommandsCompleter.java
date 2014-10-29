/*
*  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package carbon.shell.console.jline;

import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import org.apache.felix.gogo.runtime.CommandSessionImpl;
import org.apache.felix.service.command.CommandSession;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class CommandsCompleter implements Completer {


	private CommandSession commandSession;
	private final Set<String> commandList = new CopyOnWriteArraySet<String>();

	public CommandsCompleter(CommandSession commandSession) {
		this.commandSession = commandSession;
	}


	@Override
	public int complete(String buffer, int cursor, List<CharSequence> candidates) {
		if (commandSession != null) {
			//TODO Create a global commandSession
			//get the global command session, in this case we have to maintain a global
			// commandSession
		}
		extractCommands();
		int result = new StringsCompleter(commandList).complete(buffer, cursor, candidates);
		return result;
	}

	private void extractCommands() {
		if (commandList.isEmpty()) {
			Set<String> namesOfCommands = new HashSet<String>((Set<String>) commandSession.get
			  (CommandSessionImpl.COMMANDS));
			for (String name : namesOfCommands) {
				commandList.add(name);
				if (name.indexOf(':') > 0) {
					//commandList.add(name.substring(0,name.indexOf(':')));
				}
			}
		}
	}
}
