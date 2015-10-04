package com.debugtoday.htmldecoder.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.debugtoday.htmldecoder.exception.GeneralException;

public class FileUtil {

	public static final Date createDate(File file) throws GeneralException {
		String fileName = file.getName();
		
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(String.format("cmd /C dir %s /tc", file.getAbsolutePath()));
		} catch (IOException e) {
			throw new GeneralException("fail to read create date of file [" + fileName + "]", e);
		}
		
		try (
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				) {
			String inLine;
			while ((inLine = reader.readLine()) != null) {
				if (inLine.toLowerCase().indexOf(fileName.toLowerCase()) > 0) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
					return sdf.parse(inLine.substring(0, 17));
				}
			}
		} catch (IOException | ParseException e) {
			throw new GeneralException("fail to read create date of file [" + fileName + "]", e);
		}
		
		return null;
	}
	
	/**
	 * copy directory recursively
	 * @param from
	 * @param to
	 * @throws GeneralException 
	 */
	public static void copyDirectory(File from, File to) throws GeneralException {
		if (!from.isDirectory()) {
			throw new GeneralException("invalid directory[" + from.getPath() + "]");
		}
		
		if (!to.exists()) {
			to.mkdirs();
		}
		
		for (File file : from.listFiles()) {
			File destFile;
			try {
				destFile = new File(to.getAbsolutePath() + File.separator + relativePath(from, file));
			} catch (IOException e) {
				throw new GeneralException(e);
			}
			if (file.isFile()) {
				copy(file, destFile);
			} else {
				copyDirectory(file, destFile);
			}
		}
	}
	
	public static void copy(File from, File to) throws GeneralException {
		if (!from.isFile()) {
			throw new GeneralException("invalid file[" + from.getPath() + "]");
		}
		
		if (!to.exists()) {
			try {
				to.createNewFile();
			} catch (IOException e) {
				throw new GeneralException("fail to create new file[" + to.getPath() + "]", e);
			}
		}
		
		try (
				FileInputStream fis = new FileInputStream(from);
				FileOutputStream fos = new FileOutputStream(to);
				) {
			byte[] data = new byte[1024];
			int length;
			while ((length = fis.read(data)) != -1) {
				fos.write(data, 0, length);
			}
		} catch (IOException e) {
			throw new GeneralException("fail to copy file from [" + from.getPath() + "] to [" + to.getPath() + "]", e);
		}
		
	}
	
	/**
	 * get relative path of <i>child</i> to <i>parent</i>.<br>
	 * To be noticed, relative returned NOT start with "/" and file separator will be replaced by "/" 
	 * @param parent
	 * @param child
	 * @return
	 * @throws IOException
	 */
	public static final String relativePath(File parent, File child) throws IOException {
		String parentPath = parent.getCanonicalPath();
		String childPath = child.getCanonicalPath();

		if (parentPath.equals(childPath)) {
			return "";
		} else {
			return childPath.substring(parentPath.length() + 1).replace(File.separator, "/");
		}
	}
	
	public static final String fileName(File file) {
		String fileName = file.getName();
		
		int index = fileName.lastIndexOf(".");
		
		return index == -1 ? fileName : fileName.substring(0, index);
	}
	
	public static final String fileExtensionName(File file) {
		String fileName = file.getName();
		
		int index = fileName.lastIndexOf(".");
		
		return index == -1 || fileName.endsWith(".") ? "" : fileName.substring(index + 1);	
	}
}
