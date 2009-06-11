package by.bsu.fami.etl.engine.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class FileSystemHelper {

	public static List<File> listFiles(File directory, FilenameFilter filter,
			boolean recurse) {
		List<File> files = new ArrayList<File>();
		File[] entries = directory.listFiles();
		for (File entry : entries) {
			if (filter == null || filter.accept(directory, entry.getName())) {
				files.add(entry);
			}
			if (recurse && entry.isDirectory()) {
				files.addAll(listFiles(entry, filter, recurse));
			}
		}
		return files;
	}

}
