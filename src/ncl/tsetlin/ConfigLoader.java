package ncl.tsetlin;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

public class ConfigLoader {
	
	public static HashMap<String, String> loadConfigValues(String path) {
		try {
			HashMap<String, String> values = new HashMap<>();
			Scanner in = new Scanner(new File(path));
			while(in.hasNextLine()) {
				String[] s = in.nextLine().trim().split("\\s*:\\s*", 2);
				if(s.length==2)
					values.put(s[0], s[1]);
			}
			in.close();
			return values;
		}
		catch(Exception e) {
			return null;
		}
	}
	
	public static int getInt(String value, int min, int max, int fallback) {
		if(value==null)
			return fallback;
		try {
			int n = Integer.parseInt(value);
			if(n<min || n>max)
				return fallback;
			return n;
		}
		catch(NumberFormatException e) {
			return fallback;
		}
	}
	
	public static float getFloat(String value, float min, float max, float fallback) {
		if(value==null)
			return fallback;
		try {
			float x = Float.parseFloat(value);
			if(x<min || x>max)
				return fallback;
			return x;
		}
		catch(NumberFormatException e) {
			return fallback;
		}
	}

	public static double getDouble(String value, double min, double max, double fallback) {
		if(value==null)
			return fallback;
		try {
			double x = Double.parseDouble(value);
			if(x<min || x>max)
				return fallback;
			return x;
		}
		catch(NumberFormatException e) {
			return fallback;
		}
	}

	public static boolean getBoolean(String value, boolean fallback) {
		if(value==null)
			return fallback;
		if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes"))
			return true;
		else if(value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no"))
			return false;
		else
			return fallback;
	}
}
