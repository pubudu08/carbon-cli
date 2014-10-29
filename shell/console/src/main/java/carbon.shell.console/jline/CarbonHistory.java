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

import jline.console.history.FileHistory;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

public class CarbonHistory extends FileHistory {

	boolean failed = false;
	boolean loading = false;

	public CarbonHistory(File file) throws IOException {
		super(file);
	}

	@Override
	public void add(CharSequence item) {
		if (!loading) {
			item = item.toString().replaceAll("\\!", "\\\\!");
		}
		super.add(item);
	}

	@Override
	public void load(Reader reader) throws IOException {
		loading = true;
		try {
			super.load(reader);
		} finally {
			loading = false;
		}
	}

	@Override
	public void flush() throws IOException {
		if (!failed) {
			try {
				super.flush();
			} catch (IOException e) {
				failed = true;
				LOGGER.debug("Could not write history file: "+ getFile(), e);
			}
		}
	}

	@Override
	public void purge() throws IOException {
		if (!failed) {
			try {
				super.purge();
			} catch (IOException e) {
				failed = true;
				LOGGER.debug("Could not delete history file: "+ getFile(), e);
			}
		}
	}

}
