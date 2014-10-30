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
import org.fusesource.jansi.Ansi;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

@Command(scope = "console", name = "info", description = " System information")
public class SystemInfoAction extends AbstractAction {

	private NumberFormat fmtI = new DecimalFormat("###,###",
	  new DecimalFormatSymbols(Locale.ENGLISH));
	private NumberFormat fmtD = new DecimalFormat("###,##0.000",
	  new DecimalFormatSymbols(Locale.ENGLISH));

	@Override
	protected Object doExecute() throws Exception {
		int nameLength = 25;
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();

		System.out.println();
		System.out.println("Carbon ");
		System.out.println(" - Will be back soon !");
		//print carbon information
		System.out.println();
		System.out.println("Operating system");
		formatValuesToPrint("Name", nameLength, operatingSystemMXBean.getName());
		formatValuesToPrint("Version", nameLength, operatingSystemMXBean.getVersion());
		formatValuesToPrint("Architecture", nameLength, operatingSystemMXBean.getArch());
		formatValuesToPrint("Family", nameLength, System.getProperty("os.name").toLowerCase());
		formatValuesToPrint("Processors", nameLength, Integer.toString(operatingSystemMXBean
		  .getAvailableProcessors()));
		System.out.println();
		System.out.println("JVM Information");
		formatValuesToPrint("Java Virtual Machine", nameLength, runtimeMXBean.getVmName());
		formatValuesToPrint("VM version", nameLength, runtimeMXBean.getVmVersion());
		formatValuesToPrint("JDK Version", nameLength, System.getProperty("java.version"));
		formatValuesToPrint("Vendor", nameLength, runtimeMXBean.getVmVendor());
		formatValuesToPrint("Process Id", nameLength, getPid());
		formatValuesToPrint("JVM Up time", nameLength, printDuration(runtimeMXBean.getUptime()));
		return null;
	}

	/**
	 * Method to build time values
	 * @param uptime
	 * @return
	 */
	private String printDuration(double uptime) {
		uptime /= 1000;
		if (uptime < 60) {
			return fmtD.format(uptime) + " seconds";
		}
		uptime /= 60;
		if (uptime < 60) {
			long minutes = (long) uptime;
			String s = fmtI.format(minutes) + (minutes > 1 ? " minutes" : " minute");
			return s;
		}
		uptime /= 60;
		if (uptime < 24) {
			long hours = (long) uptime;
			long minutes = (long) ((uptime - hours) * 60);
			String s = fmtI.format(hours) + (hours > 1 ? " hours" : " hour");
			if (minutes != 0) {
				s += " " + fmtI.format(minutes) + (minutes > 1 ? " minutes" : " minute");
			}
			return s;
		}
		uptime /= 24;
		long days = (long) uptime;
		long hours = (long) ((uptime - days) * 24);
		String formatter = fmtI.format(days) + (days > 1 ? " days" : " day");
		if (hours != 0) {
			formatter += " " + fmtI.format(hours) + (hours > 1 ? " hours" : " hour");
		}
		return formatter;
	}

	/**
	 * Retrieve process id
	 * @return
	 */
	private String getPid() {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		String[] parts = name.split("@");
		return parts[0];
	}

	/**
	 * Responsible of formatting data based on input
	 * @param name
	 * @param size
	 * @param value
	 */
	public void formatValuesToPrint(String name, int size, String value) {
		System.out.println(Ansi.ansi().a(" ").a(Ansi.Attribute.INTENSITY_BOLD).a(name).a
		  (formatSpace(size - name.length())).a(Ansi.Attribute.INTENSITY_BOLD_OFF).fg(Ansi.Color
		  .RED).a(value).fg(Ansi.Color.DEFAULT).toString());
	}

	/**
	 * Format space inputs
	 * @param size
	 * @return
	 */
	public String formatSpace(int size) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int x = 0; x < size; x++) {
			stringBuilder.append(' ');
		}
		return stringBuilder.toString();
	}
}
