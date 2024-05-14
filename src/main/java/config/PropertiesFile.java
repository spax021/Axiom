package config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesFile {

	private static String fileLocation = ".\\src\\test\\resources\\application.properties";
	private static Properties prop;

	public static void readPropertiesFile() {
		prop = new Properties();
		try {
			InputStream input = new FileInputStream(fileLocation);
			prop.load(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getBaseUrl() {
		readPropertiesFile();
		return prop.getProperty("baseUrl");
	}

	public static String getUsername() {
		readPropertiesFile();
		return prop.getProperty("username");
	}

	public static String getPassword() {
		readPropertiesFile();
		return prop.getProperty("password");
	}

	public static String getIncorectPassword() {
		readPropertiesFile();
		return prop.getProperty("incorectPassword");
	}

}
