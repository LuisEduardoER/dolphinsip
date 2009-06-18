package com.dsc.dip.etl.engine.app.felix;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.felix.framework.Felix;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.startlevel.StartLevel;

import com.dsc.dip.etl.engine.app.felix.utils.Module;

public class Starter implements Runnable {

	protected Felix felix;

	protected Thread worker;

	protected Module[] modules;

	protected StartLevel startLevel;

	public Starter(Felix felix) {
		this.felix = felix;
		worker = new Thread(this);
		modules = new Module[] {
				new Module("org.apache.felix.bundlerepository", "1.4.0",
						"framework", 1),
				new Module("org.apache.felix.shell", "1.2.0", "framework", 1),
				new Module("log4j", "1.2.15", "core", 1),
				new Module("com.dsc.dip.etl.compiler", "0.0.1", "core", 2),
				new Module("com.dsc.dip.etl.engine", "0.0.1", "core", 2) };
	}

	public void start() {
		if (worker != null) {
			worker.start();
		}
	}

	public void run() {
		if (felix != null) {
			try {
				felix.start();

				BundleContext context = felix.getBundleContext();
				
				// init start level service
				ServiceReference ref = context
						.getServiceReference(StartLevel.class.getName());
				if (ref != null) {
					startLevel = (StartLevel) context.getService(ref);
				}

				if (startLevel == null) {
					System.out.println("StartLevel service is unavailable.");
				}

				// Init modules
				startLevel.setStartLevel(4);
				for (Module module : modules) {
					startModule(module);
				}

				showFelixModulesInfo();

				felix.waitForStop(0);
			} catch (BundleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void join() {
		if (worker != null) {
			try {
				worker.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void startModule(Module module) {
		if (felix != null) {
			try {
				BundleContext context = felix.getBundleContext();
				Bundle bundle = context.installBundle(module.getName(),
						new FileInputStream(module.getPath()));
				if (bundle != null) {
					System.out.println("Success install '" + module.getName()
							+ "' module");
					if (startLevel != null) {
						startLevel.setBundleStartLevel(bundle, module
								.getLevel());
					}
					bundle.start();
					System.out.println("Success start '" + module.getName()
							+ "' module");
				} else {
					System.err.println("Couldn't install '" + module.getName()
							+ "' module");
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BundleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void showFelixModulesInfo() {
		BundleContext context = felix.getBundleContext();

		// Check for optional argument.
		Bundle[] bundles = context.getBundles();
		if (bundles != null) {
			// Display active start level.
			if (startLevel != null) {
				System.out.println("START LEVEL " + startLevel.getStartLevel());
			}
			// Print column headers.
			String msg = " Name";
			String level = (startLevel == null) ? "" : "  Level ";
			System.out.println("   ID " + "  State       " + level + msg);
			for (int i = 0; i < bundles.length; i++) {
				// Get the bundle name or location.
				String name = (String) bundles[i].getHeaders().get(
						Constants.BUNDLE_NAME);
				// If there is no name, then default to symbolic name.
				name = (name == null) ? bundles[i].getSymbolicName() : name;
				// If there is no symbolic name, resort to location.
				name = (name == null) ? bundles[i].getLocation() : name;

				// Overwrite the default value is the user specifically
				// requested to display one or the other.
				name = bundles[i].getSymbolicName();
				name = (name == null) ? "<no symbolic name>" : name;
				// Show bundle version if not showing location.
				String version = (String) bundles[i].getHeaders().get(
						Constants.BUNDLE_VERSION);
				name = name + " (" + version + ")";
				long l = bundles[i].getBundleId();
				String id = String.valueOf(l);
				if (startLevel == null) {
					level = "1";
				} else {
					level = String.valueOf(startLevel
							.getBundleStartLevel(bundles[i]));
				}
				while (level.length() < 5) {
					level = " " + level;
				}
				while (id.length() < 4) {
					id = " " + id;
				}
				System.out.println("[" + id + "] ["
						+ getStateString(bundles[i].getState()) + "] [" + level
						+ "] " + name);
			}
		} else {
			System.out.println("There are no installed bundles.");
		}
	}

	public String getStateString(int i) {
		if (i == Bundle.ACTIVE)
			return "Active     ";
		else if (i == Bundle.INSTALLED)
			return "Installed  ";
		else if (i == Bundle.RESOLVED)
			return "Resolved   ";
		else if (i == Bundle.STARTING)
			return "Starting   ";
		else if (i == Bundle.STOPPING)
			return "Stopping   ";
		return "Unknown    ";
	}

}
