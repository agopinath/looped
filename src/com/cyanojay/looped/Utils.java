package com.cyanojay.looped;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {
	public static void printHTTPResponse(InputStream entityStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(entityStream));
        String result = "";
            
        while((result = reader.readLine()) != null)
            System.out.println(result);
	}
}
