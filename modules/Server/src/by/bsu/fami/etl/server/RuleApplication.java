package by.bsu.fami.etl.server;

import org.apache.wicket.guice.GuiceComponentInjector;
import org.apache.wicket.protocol.http.WebApplication;

import by.bsu.fami.etl.server.page.RuleLogPage;

public class RuleApplication extends WebApplication {

	public RuleApplication() {
	}

	protected void init() {
		addComponentInstantiationListener(new GuiceComponentInjector(this));
	}

	public Class<?> getHomePage() {
		return RuleLogPage.class;
	}

}
