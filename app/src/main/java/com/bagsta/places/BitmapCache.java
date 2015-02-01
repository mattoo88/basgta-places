package com.bagsta.places;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import android.graphics.Bitmap;

public class BitmapCache {
	
	long CACHE_SIZE_LIMIT;
	long cacheCurrentSize;
	
	Map<String, Bitmap> cache;
	
	BitmapCache() {
		cache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 2, true));
		setCacheLimit(Runtime.getRuntime().maxMemory()/4);
	}
	
	void setCacheLimit(long newLimit) {
		CACHE_SIZE_LIMIT = newLimit;
	}
	
	Bitmap get(String imageUrl) {
		return cache.get(imageUrl);
	}
	
	void put(String imageUrl, Bitmap bitmap) {
		cache.put(imageUrl, bitmap);
		cacheCurrentSize += getBitmapSizeInBytes(bitmap);
		checkCacheSize();
	}
	
	long getBitmapSizeInBytes(Bitmap bitmap) {
		long size = bitmap.getRowBytes() * bitmap.getHeight();
		return size;
	}
	
	void checkCacheSize() {
		if(cacheCurrentSize > CACHE_SIZE_LIMIT) {
			cacheCurrentSize -= freeCacheMemory(cacheCurrentSize - CACHE_SIZE_LIMIT);
		}
	}
	
	long freeCacheMemory(long memoryToBeReleased) {
		
		long memoryReleased = 0;
		
		Set<Entry<String, Bitmap>> entries = cache.entrySet();
		Iterator<Entry<String, Bitmap>> iterator = entries.iterator();
		
		while (iterator.hasNext()) {
			
			Entry<String, Bitmap> entry = iterator.next();
			Bitmap bitmap = entry.getValue();
			memoryReleased += getBitmapSizeInBytes(bitmap);
			iterator.remove();
			
			if (memoryReleased > memoryToBeReleased) {
				break;
			}
		}
		
		return memoryReleased;
	}
	
	void clearCache() {
		cache.clear();
		cacheCurrentSize = 0;
	}
}
