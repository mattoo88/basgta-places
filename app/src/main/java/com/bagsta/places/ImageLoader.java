package com.bagsta.places;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bagsta.places.R;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader {
	BitmapCache bitmapCache;
	FileCache fileCache;
	Map<ImageView, String> imageViewsMap;
	
	Handler handler = new Handler();
	ExecutorService executorService;

	boolean isImageToBeScaled;

	ImageLoader(Context context) {
		bitmapCache = new BitmapCache();
		fileCache = new FileCache(context);
		imageViewsMap = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
		executorService = Executors.newFixedThreadPool(5);
	}

	void displayBitmap(ImageView imageView, String imageUrl,
			boolean isImageToBeScaled) {

		this.isImageToBeScaled = isImageToBeScaled;
		Bitmap bitmap = bitmapCache.get(imageUrl);
		imageViewsMap.put(imageView, imageUrl);
		
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			queueToDownLoad(imageView, imageUrl);
			imageView.setImageResource(R.drawable.ic_launcher);
		}
	}

	void queueToDownLoad(ImageView imageView, String imageUrl) {
		PhotoToLoad photoToLoad = new PhotoToLoad(imageView, imageUrl);
		executorService.submit(new PhotoLoader(photoToLoad));
	}

	class PhotoToLoad {
		ImageView imageView;
		String imageUrl;

		public PhotoToLoad(ImageView imageView, String imageUrl) {
			this.imageView = imageView;
			this.imageUrl = imageUrl;
		}
	}

	class PhotoLoader implements Runnable {

		PhotoToLoad photoToLoad;

		public PhotoLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {

			if (imageViewReused(photoToLoad)) {
				return;
			}

			Bitmap bitmap = null;
			try {
				bitmap = getBitmap(photoToLoad.imageUrl);
			} catch (IOException exception) {
			}

			bitmapCache.put(photoToLoad.imageUrl, bitmap);

			if (imageViewReused(photoToLoad)) {
				return;
			}

			handler.post(new BitmapDisplayer(photoToLoad, bitmap));
		}
	}

	Bitmap getBitmap(String imageUrl) throws IOException {

		Bitmap bitmap = null;
		File file = fileCache.getFile(imageUrl);
		FileInputStream is = null;
		
		try {
			if (file != null && file.exists()) {
				is = new FileInputStream(file);
				bitmap = BitmapFactory.decodeStream(is);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if (bitmap == null) {

			InputStream inputStream = null;
			FileOutputStream fileOutputStream = null;
			
			try {
					
				inputStream = HttpRequestService.requestInpustStream(imageUrl);
				fileOutputStream = new FileOutputStream(file);
				Utils.copyStream(inputStream, fileOutputStream);
				
				inputStream.close();
				fileOutputStream.close();
				
				fileCache.checkCacheSize(file);
				bitmap = decodeBitmap(file);
				
			} catch (IOException exception) {
				Log.e("ImageLoader", "" + Log.getStackTraceString(exception));
			} finally {
				if (inputStream != null)
					inputStream.close();
				if (fileOutputStream != null)
					fileOutputStream.close();
			}
		}
		return bitmap;
	}

	Bitmap decodeBitmap(String imageUrl) {
		File file = fileCache.getFile(imageUrl);
		return decodeBitmap(file);
	}
	
	Bitmap decodeBitmap(File file) {
		Bitmap bitmap = null;
		try {

			if (isImageToBeScaled) {

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				FileInputStream fileInputStream = new FileInputStream(file);
				BitmapFactory.decodeStream(fileInputStream, null, options);
				fileInputStream.close();

				int imgWidth = options.outWidth;
				int imgHeight = options.outHeight;

				final int IMAGE_SIZE = 80;
				int scale = 1;

				while (IMAGE_SIZE < imgWidth / 2 || IMAGE_SIZE < imgHeight / 2) {
					imgHeight /= 2;
					imgWidth /= 2;
					scale *= 2;
				}

				BitmapFactory.Options options2 = new BitmapFactory.Options();
				options2.inSampleSize = scale;
				FileInputStream fileInputStream2 = new FileInputStream(file);
				bitmap = BitmapFactory.decodeStream(fileInputStream2, null,
						options2);
				fileInputStream2.close();
			} else {
				bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
			}
		} catch (IOException exception) {
			Log.e("ImageLoader", "" + Log.getStackTraceString(exception));
		}
		return bitmap;
	}

	class BitmapDisplayer implements Runnable {

		PhotoToLoad photoToLoad;
		Bitmap bitmap;

		public BitmapDisplayer(PhotoToLoad photoToLoad, Bitmap bitmap) {
			this.photoToLoad = photoToLoad;
			this.bitmap = bitmap;
		}

		@Override
		public void run() {
			
			if (imageViewReused(photoToLoad))
				return;
			
			photoToLoad.imageView.setImageBitmap(bitmap);
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String imageUrl = imageViewsMap.get(photoToLoad.imageView);
		if (imageUrl == null || !imageUrl.equals(photoToLoad.imageUrl))
			return true;
		else
			return false;
	}
	
	void clearCache() {
		bitmapCache.clearCache();
		fileCache.clearCache();
	}
}
