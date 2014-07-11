/***********************************************************************************************************************
 * Copyright (C) 2014 by Sebastian Kruse
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package de.hpi.isg;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class FileConcatenator {

	private static final int BUFFER_SIZE = 1024;
	
	public static void main(String[] args) throws IOException {
		File dir = parseArgs(args);

		File[] partialFiles = retrieveOrderedPartialFiles(dir);
		
		File tempResultFile = concatenatePartialFiles(dir, partialFiles);
		
		deleteRecursive(dir);
		
		tempResultFile.renameTo(dir);

	}

	private static File parseArgs(String[] args) {
		if (args.length != 1) {
			System.err.println("Illegal command line.");
			System.exit(1);
		}
	
		File dir = new File(args[0]);
		if (!dir.exists() || !dir.isDirectory()) {
			System.err.println("Not a directory: " + dir);
			System.exit(2);
		}
		return dir;
	}

	private static File concatenatePartialFiles(File dir, File[] partialFiles) {
		String baseDir = dir.getParent();
		File tempResultFile = new File(baseDir, dir.getName() + ".temp");
		
		try (FileOutputStream fos = new FileOutputStream(tempResultFile, false)) {

			copyContents(partialFiles, fos);
		} catch (IOException e) {
		
			System.err.println("Error while copying.");
			e.printStackTrace();
			tempResultFile.delete();
			System.exit(3);
		}
		return tempResultFile;
	}

	private static void copyContents(File[] partialFiles, FileOutputStream fos)
			throws IOException, FileNotFoundException {
		byte[] buffer = new byte[BUFFER_SIZE];
		for (File partialFile : partialFiles) {
			try (FileInputStream fis = new FileInputStream(partialFile)) {
				int readBytes;
				while ((readBytes = fis.read(buffer)) > 0) {
					fos.write(buffer, 0, readBytes);
				}
			}
		}
	}

	private static File[] retrieveOrderedPartialFiles(File dir) {
		File[] partialFiles = dir.listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().matches("\\d+");
			}
		});

		Arrays.sort(partialFiles, new Comparator<File>() {

			public int compare(File f1, File f2) {
				return Integer.compare(Integer.parseInt(f1.getName()),
						Integer.parseInt(f2.getName()));
			}
		});
		return partialFiles;
	}

	private static void deleteRecursive(File file) {
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			for (File child : children) {
				deleteRecursive(child);
			}
		}
		file.delete();
	}

}
