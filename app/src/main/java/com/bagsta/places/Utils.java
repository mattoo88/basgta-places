package com.bagsta.places;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {

	static void copyStream(InputStream is, OutputStream os) throws IOException {
		byte [] buffer = new byte[1024]; //1kb buffer size
		int count = 0;
		
		while ((count = is.read(buffer)) != -1) {
			os.write(buffer, 0, count);
		}
	}
}
