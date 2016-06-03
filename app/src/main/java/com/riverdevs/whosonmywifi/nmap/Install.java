package com.riverdevs.whosonmywifi.nmap;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.Resources;

import com.riverdevs.whosinmywifi.R;

public class Install {

//	private static final int BUFFER_SIZE = 8192;
//
//	private class InstallerBinary {
//		public transient String filename;
//		public transient int files[];
//		public transient boolean executable;
//
//		public InstallerBinary(final String filename, final int files[], 
//				final boolean executable) {
//			this.filename = filename;
//			this.files = files.clone();
//			this.executable = executable;
//		}
//	}
//
//	private final transient InstallerBinary installerBinaries[] = {
//			new InstallerBinary("nmap", new int[] { R.raw.nmap_aa,
//					R.raw.nmap_ab, R.raw.nmap_ac }, true),
//			new InstallerBinary("nmap-os-db", new int[] { R.raw.nmap_os_db_aa,
//					R.raw.nmap_os_db_ab, R.raw.nmap_os_db_ac }, false),
//			new InstallerBinary("nmap-payloads",
//					new int[] { R.raw.nmap_payloads }, false),
//			new InstallerBinary("nmap-protocols",
//					new int[] { R.raw.nmap_protocols }, false),
//			new InstallerBinary("nmap-rpc", new int[] { R.raw.nmap_rpc }, false),
//			new InstallerBinary("nmap-service-probes",
//					new int[] { R.raw.nmap_service_probes_aa,
//							R.raw.nmap_service_probes_ab }, false),
//			new InstallerBinary("nmap-services",
//					new int[] { R.raw.nmap_services }, false),
//			new InstallerBinary("nmap-mac-prefixes",
//					new int[] { R.raw.nmap_mac_prefixes }, false) 
//			};
//
//	private final transient String binaryDirectory;
//	private final transient Resources appResources;
//
//	/**
//	 * 
//	 * @param context Context of the activity launching this installer.
//	 * @param binaryDirectory Location to save binaries.
//	 * @param hasRoot Does user have root access or not.
//	 */
//	public Install(final Context context, final String binaryDirectory) {
//		super();
//		this.appResources = context.getResources();
//		this.binaryDirectory = binaryDirectory;
//	}
//
//	private void deleteExistingFile(final File myFile) {
//		if (myFile.exists()) {
////			PipsError.log(myFile.getAbsolutePath() + " exists. Deleting...");
//			if (myFile.delete()) {
////				PipsError.log("...deleted.");
//			} else {
////				PipsError.log("...unable to delete.");
//			}
//		}
//	}
//
//	private void writeNewFile(final File myFile, final int fileResources[]) throws Exception{
//		final byte[] buf = new byte[BUFFER_SIZE];
//
//		OutputStream out;
//			out = new FileOutputStream(myFile);
//			for (int resource : fileResources) {
//				final InputStream inputStream = appResources
//						.openRawResource(resource);
//				while (inputStream.read(buf) > 0) {
//					out.write(buf);
//				}
//				inputStream.close();
//			}
//			out.close();
//	}
//
//	private void setExecutable(final File myFile) throws Exception{
//		final String shell = "sh";
//		
//			final Process process = Runtime.getRuntime().exec(shell);
//			final DataOutputStream outputStream = new DataOutputStream(
//					process.getOutputStream());
//			final BufferedReader inputStream = new BufferedReader(
//					new InputStreamReader(process.getInputStream()),
//					BUFFER_SIZE);
//			final BufferedReader errorStream = new BufferedReader(
//					new InputStreamReader(process.getErrorStream()),
//					BUFFER_SIZE);
//
//			outputStream.writeBytes("cd " + this.binaryDirectory + "\n");
//
//			outputStream.writeBytes("chmod 555 " + myFile.getAbsolutePath()
//					+ " \n");
//
//			outputStream
//					.writeBytes("chmod 777 " + this.binaryDirectory + " \n");
//
//			outputStream.writeBytes("exit\n");
//
//			final StringBuilder feedback = new StringBuilder();
//			String input, error;
//			while ((input = inputStream.readLine()) != null) {
//				feedback.append(input);
//			}
//			while ((error = errorStream.readLine()) != null) {
//				feedback.append(error);
//			}
//
//			final String chmodResult = feedback.toString();
//
//			outputStream.close();
//			inputStream.close();
//			errorStream.close();
//			process.waitFor();
//			process.destroy();
//
//			if (chmodResult.length() > 0) {
//				throw new Exception("Error when installing executables");
//			}
//	}
//
//	public boolean installNmapFiles() {
//		File myFile;
//
//		try{
//		for (InstallerBinary install : installerBinaries) {
//			final String filename = binaryDirectory + install.filename;
//
//			myFile = new File(filename);
//
//			if(!myFile.exists()){
//				deleteExistingFile(myFile);
//	
//				writeNewFile(myFile, install.files);
//	
//				if (install.executable) {
//					setExecutable(myFile);
//				}
//			}
//		}
//		}catch(Exception ex){
//			ex.printStackTrace();
//			return false;
//		}
//		
//		return true;
//	}
}
