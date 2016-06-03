package com.riverdevs.whosonmywifi.nmap;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

/**
 * The general workflow for this program is:<ol>
 * <li>User initiates scan from PIPSActivity, which creates a thread of type Scan.</li>
 * <li>The Scan thread sends messages to PIPSActivity.handler with errors or success, and includes output in Message.obj</li>
 * <li></li></ol>
 */
public class Scan {

//	private final transient String binaryDirectory;
//	private final transient String target;
//	private final transient String arguments;
//	private final transient int help;
//	private final transient String shell;
//
//	/**
//	 * Thread that handles the actual scanning. Yes, it's a little more
//	 * intimidating than I had wanted when I first started this rewrite, but
//	 * it's not nearly as bad as what I had before.
//	 * 
//	 * @param binaryDirectory
//	 *            Directory the binaries will be stored. See Utilities class.
//	 * @param hasRoot
//	 *            Whether the user has access to root or not. See Utilities
//	 *            class.
//	 * @param target
//	 *            Target the user wants to scan.
//	 * @param arguments
//	 *            Arguments the user wants. The program will probably break if
//	 *            the user specifies -oA.
//	 * @param help
//	 *            Whether or not you want -h command-line help. 1 for help, 0 for normal output.
//	 * @param context
//	 *            Context the activity is running from (required to save results to database).
//	 */
//	public Scan(final String binaryDirectory,
//			final String target, final String arguments, final int help, final Context context) {
//		super();
//		this.binaryDirectory = binaryDirectory;
//		this.target = target;
//		this.arguments = arguments;
//		this.help = help;
//
//		this.shell = "sh";
//	}
//
//	public List<String> performScan() {
//		
//		ArrayList<String> foundIpList = new ArrayList<String>();
//		String line;
//		Process process = null;
//		DataOutputStream outputStream = null;
//		BufferedReader inputStream, errorStream;
//		inputStream = errorStream = null;
//		StringBuilder command;
//		
//		command = new StringBuilder(binaryDirectory);
//		command.append("nmap ");
//		command.append(arguments);
//		command.append(' ');
//		command.append(target);
//
//		if (help == 1 && !arguments.contains("-h")) {
//			command.append(" -h ");
//		}
//
//		try {
//			process = Runtime.getRuntime().exec(shell);
//			if (process == null) {
//				return null;
//			} 
//
//			outputStream = new DataOutputStream(process.getOutputStream());
//			inputStream = new BufferedReader(new InputStreamReader(
//					process.getInputStream()));
//			errorStream = new BufferedReader(new InputStreamReader(
//					process.getErrorStream()));
//
//			outputStream.writeBytes(command.toString() + "\n");
//			outputStream.flush();
//			outputStream.writeBytes("exit\n");
//			outputStream.flush();
//
//			while ((line = inputStream.readLine()) != null) {
//				if(line.contains("Nmap scan report for")){
//					String[] parts = line.split("\\s+");
//					foundIpList.add(parts[4]);
//				}
//			}
//
//			return foundIpList;
//			
//		} catch (Exception ex){
//			
//			return null;
//			
//		} finally {
//
//			if (process != null) {
//				process.destroy();
//			}
//
//			try {
//				outputStream.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			try {
//				errorStream.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//		}
//	}
}
