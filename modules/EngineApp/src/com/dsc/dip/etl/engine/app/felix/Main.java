package com.dsc.dip.etl.engine.app.felix;

import java.io.File;
import java.util.Collections;

import com.dsc.dip.etl.engine.app.felix.utils.ClassPathHacker;


public class Main {

	public static void main(String[] args) {
		ClassPathHacker.addFile("framework" + File.separatorChar + "felix.jar");
		System.out.println("Success load felix framework");
		Starter starter = new Starter(new org.apache.felix.framework.Felix(
				Collections.EMPTY_MAP));
		starter.start();
		starter.join();

		System.exit(0);
	}

}
