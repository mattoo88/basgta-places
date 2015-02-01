package com.bagsta.places;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class FileCache {

	File appFolder;
	final static long CACHE_SIZE_LIMIT = (1024 * 1024) * 10;
	long currentCacheSize;
	Context context;
	
	FileCache(Context context) {
		this.context = context;
		
		if (android.os.Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			appFolder = new File(context.getExternalCacheDir(), "CouponDunia");
		} else 
		{
			appFolder = context.getCacheDir();
		}

		if (!appFolder.exists()) {
			// create cache dir in your application context
			boolean foldeCreationFailed = false;
			foldeCreationFailed = appFolder.mkdirs();
			if (!foldeCreationFailed) {
				Log.e("FileCache","Folder creation has failed "+ appFolder.getAbsolutePath());
			}
		} else {
			Log.d("FileCache", "Folder Exists : "+appFolder);
		}
		currentCacheSize = getCacheDirSize();
		Log.d("CACHE_SIZE_LIMIT", ""+(CACHE_SIZE_LIMIT/1024)+" KB");
	}

	File getFile(String fileName) {
		File file = new File(appFolder.getAbsolutePath(),
				String.valueOf(fileName.hashCode()));
		return file;
	}
	
	synchronized void checkCacheSize(File currentFile) {
		currentCacheSize = getCacheDirSize();

		if (currentCacheSize > CACHE_SIZE_LIMIT) {
			currentCacheSize -= freeCacheMemory(currentCacheSize
					- CACHE_SIZE_LIMIT, currentFile);
		}
	}
	
	long freeCacheMemory(long memoryToBeReleased ) {
		return freeCacheMemory(memoryToBeReleased, null);
	}

	long freeCacheMemory(long memoryToBeReleased, File currentFile) {
//		Log.d("memoryToBeReleased "+Thread.currentThread().getName(), (memoryToBeReleased / 1024) + " KB");
		long memoryReleased = 0;
		try {
			
			File[] files = appFolder.listFiles();
			Arrays.sort(files, LastModifiedDate);
			
			for (int i = 0; i <files.length; i++) {
				File file = files[i];
				
				if (currentFile != null) {
					if (currentFile.equals(file)) {
						continue;
					}
				}

				long fileSize = file.length();

				if (file.delete()) {
					memoryReleased += fileSize;
//					Log.d("File deleted : " + Thread.currentThread().getName(),
//							"" + file.getName());
				}

				if (memoryReleased > memoryToBeReleased) {
					break;
				}
			}

		} catch (Exception exception) {
			Log.e("EXCEPTION", "freeCacheMemory", exception);
		}
		return memoryReleased;
	}
	
	long getCacheDirSize() {
		long fileCacheDirSize = 0;
		File[] files = appFolder.listFiles();
		
		for (File file : files) {

			{
				fileCacheDirSize += file.length();	
			}
		}
		
		return fileCacheDirSize;
	}
	
	public void clearCache() {
		long memoryReleased = freeCacheMemory(getCacheDirSize());
		Toast.makeText(context,
				"Memory Freed " + (memoryReleased / 1024) + " KB",
				Toast.LENGTH_SHORT).show();
	}
	
	static final Comparator<File> LastModifiedDate = new Comparator<File>() {

		@Override
		public int compare(File lhs, File rhs) {
			if (lhs.lastModified() > rhs.lastModified()) {
				return 1;
			} else {
				return -1;
			}
		}
	};
}
