package com.dsc.dip.etl.engine.felix.shell;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.dsc.dip.etl.engine.felix.EngineService;
import com.dsc.dip.etl.engine.felix.shell.impl.ShellServiceImpl;

public class ShellActivator implements BundleActivator {

    protected ShellServiceImpl shell;

    protected BundleContext context;

    protected EngineService engine;

    private volatile ShellRunnable runnable;

    protected volatile Thread thread;

    protected ServiceReference shellRef;

    public void start(BundleContext context) throws Exception {
	shellRef = context.registerService(ShellService.class.getName(),
		shell = new ShellServiceImpl(), null).getReference();

	// Listen for registering/unregistering of impl command
	// services so that we can automatically add/remove them
	// from our list of available commands.
	ServiceListener sl = new ServiceListener() {
	    public void serviceChanged(ServiceEvent event) {
		if (event.getType() == ServiceEvent.REGISTERED) {
		    shell.addCommand(ShellActivator.this.context, event
			    .getServiceReference());
		} else if (event.getType() == ServiceEvent.UNREGISTERING) {
		    shell.removeCommand(event.getServiceReference());
		} else {
		}
	    }
	};

	try {
	    context.addServiceListener(sl, "(|(objectClass="
		    + Command.class.getName() + "))");
	} catch (InvalidSyntaxException ex) {
	    System.err.println("Activator: Cannot register service listener.");
	    System.err.println("Activator: " + ex);
	}

	sl = new ServiceListener() {
	    public void serviceChanged(ServiceEvent event) {
		synchronized (ShellActivator.this) {
		    // Ignore additional services if we already have one.
		    if ((event.getType() == ServiceEvent.REGISTERED)
			    && (shellRef != null)) {
			return;
		    }
		    // Initialize the service if we don't have one.
		    else if ((event.getType() == ServiceEvent.REGISTERED)
			    && (shellRef == null)) {
			initializeService();
		    }
		    // Unget the service if it is unregistering.
		    else if ((event.getType() == ServiceEvent.UNREGISTERING)
			    && event.getServiceReference().equals(shellRef)) {
			ShellActivator.this.context.ungetService(shellRef);
			shellRef = null;
			shell = null;
			// Try to get another service.
			initializeService();
		    }
		}
	    }
	};

	try {
	    context.addServiceListener(sl, "(objectClass="
		    + ShellService.class.getName() + ")");
	} catch (InvalidSyntaxException ex) {
	    System.err.println("EngineShell: Cannot add service listener.");
	    System.err.println("EngineShell: " + ex);
	}

	// Now try to manually initialize the impl service
	// since one might already be available.
	initializeService();

	ServiceReference engineRefs = context
		.getServiceReference(EngineService.class.getName());
	if (engineRefs != null) {
	    engine = (EngineService) context.getService(engineRefs);
	}

	// Now manually try to find any commands that have already
	// been registered (i.e., we didn't see their service events).
	initializeCommands();

	// Register "bundlelevel" command service.
	// context.registerService(Command.class.getName(),
	// new BundleLevelCommandImpl(context), null);

	thread = new Thread(runnable = new ShellRunnable(shell, this),
		"Engine Shell");
	thread.setDaemon(true);
	thread.start();

    }

    public void stop(BundleContext context) throws Exception {
	if (shell != null) {
	    shell.clearCommands();
	}
	if (runnable != null) {
	    runnable.stop();
	}
    }

    private synchronized void initializeService() {
	if (shell != null) {
	    return;
	}
	shellRef = context.getServiceReference(ShellService.class.getName());
	if (shellRef == null) {
	    return;
	}
	shell = (ShellServiceImpl) context.getService(shellRef);
    }

    private void initializeCommands() {
	if (shell == null || context == null) {
	    return;
	}
	synchronized (shell) {
	    try {
		ServiceReference[] refs = context.getServiceReferences(
			Command.class.getName(), null);
		if (refs != null) {
		    for (int i = 0; i < refs.length; i++) {
			shell.addCommand(context, refs[i]);
		    }
		}
	    } catch (Exception ex) {
		System.err.println("Activator: " + ex);
	    }
	}
    }

}
