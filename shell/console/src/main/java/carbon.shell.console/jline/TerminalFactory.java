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

import jline.NoInterruptUnixTerminal;
import jline.Terminal;

public class TerminalFactory {
	private Terminal terminal;

	/**
	 * retrieve terminal instance
	 *
	 * @return
	 * @throws Exception
	 */
	public synchronized Terminal getTerminal() throws Exception {
		if (terminal == null) {
			init();
		}
		return terminal;
	}

	/**
	 * Init terminal instance
	 */
	public void init() {
		jline.TerminalFactory.registerFlavor(jline.TerminalFactory.Flavor.UNIX,
		  NoInterruptUnixTerminal.class);
		terminal = jline.TerminalFactory.create();
	}

	/**
	 * Destroy terminal instance
	 *
	 * @throws Exception
	 */
	public synchronized void destroy() throws Exception {
		if (terminal != null) {
			terminal.restore();
			terminal = null;
		}
	}
}
