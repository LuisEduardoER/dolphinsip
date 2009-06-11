package by.bsu.fami.etl.scheduler.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class FileSystemHelper {

	public static List<File> listFiles(File directory, FilenameFilter filter,
			boolean recurse) {
		if (directory != null) {
			List<File> files = new ArrayList<File>();
			File[] entries = directory.listFiles();
			if (entries != null) {
				for (File entry : entries) {
					if (entry != null) {
						if (filter == null
								|| filter.accept(directory, entry.getName())) {
							files.add(entry);
						}
						if (recurse && entry.isDirectory()) {
							List<File> listFiles = listFiles(entry, filter,
									recurse);
							if (listFiles != null) {
								files.addAll(listFiles);
							}
						}
					}
				}
				return files;
			}
			return null;
		}
		return null;
	}

}
