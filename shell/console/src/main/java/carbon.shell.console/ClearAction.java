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
package carbon.shell.console;

import carbon.shell.console.commands.AbstractAction;
import carbon.shell.console.commands.Command;


/**
 * A command to clear the console buffer
 */
@Command(scope = "console", name = "clear", description = "Clears the console buffer.")
public class ClearAction extends AbstractAction {

	protected Object doExecute() throws Exception {
		System.out.print("\33[2J");
		System.out.flush();
		System.out.print("\33[1;1H");
		System.out.flush();
		return null;
	}


}