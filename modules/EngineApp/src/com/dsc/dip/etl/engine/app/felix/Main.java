package com.dsc.dip.etl.engine.app.felix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import com.dsc.dip.etl.engine.app.felix.utils.ClassPathHacker;

public class Main {

	private static final String ENGINE_INI_FILE = "engine.ini";

	public static void main(String[] args) {
		ClassPathHacker.addFile("framework" + File.separatorChar + "felix.jar");

		// Config base properties
		Properties engineIni = new Properties();
		try {
			engineIni.load(new FileInputStream(ENGINE_INI_FILE));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<String, String> properties = new HashMap<String, String>();
		for (Entry<Object, Object> entry : engineIni.entrySet()) {
			properties.put(entry.getKey().toString(), entry.getValue()
					.toString());
		}
		
		Starter starter = new Starter(new org.apache.felix.framework.Felix(
				properties));
		System.out.println("Success load felix framework");

		starter.start();
		starter.join();

		System.exit(0);
	}

}
