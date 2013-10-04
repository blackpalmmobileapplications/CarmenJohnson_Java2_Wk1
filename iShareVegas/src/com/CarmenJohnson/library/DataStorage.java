package com.CarmenJohnson.library;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.content.Context;

public class DataStorage {
	
	//store data
	public static void  storeData(String key, String[] hs, Context context)
	{
		try {
			//convert string array to comma separated string
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<hs.length;i++)
			{
				sb.append(hs[i]);
				if(i<hs.length-1)
					sb.append(",");
			}
			//writing to internal file
			FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
			fos.write(sb.toString().getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//store data
	public static String[] getData(String key, Context context)
	{
		try {
			//read from internal file
			FileInputStream fis = context.openFileInput(key);
			BufferedInputStream bin = new BufferedInputStream(fis);
			byte[] contentBytes = new byte[1024];
			int bytesRead = 0;
			String content;
			StringBuffer contentBuffer = new StringBuffer();
			while( (bytesRead = bin.read(contentBytes)) != -1){
				content = new String(contentBytes,0,bytesRead);
				contentBuffer.append(content);			
			}
			// return string array by splitting with comma
			return contentBuffer.toString().split(",");
		}
		catch (FileNotFoundException e) {
			// return empty array in case of exception
			e.printStackTrace();
			return new String[0];
		} catch (IOException e) {
			// return empty array in case of exception
			e.printStackTrace();
			return new String[0];
		}
		
	}
	
}
