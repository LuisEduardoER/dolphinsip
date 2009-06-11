package by.bsu.fami.etl.example.webservice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

public class SimpleWebService {

	protected final static Set<String> companies = new HashSet<String>();

	protected final static Map<String, String> users = new HashMap<String, String>();

	protected final static Map<String, String> visits = new HashMap<String, String>();

	static {
		companies.add("Google Inc.");
		users.put("Google Inc.", "10 m");
		visits.put("Google Inc.", "10 t");
		companies.add("IBM");
		users.put("IBM", "5 m");
		visits.put("IBM", "5 t");
		companies.add("Apple Inc.");
		users.put("Apple Inc.", "1 m");
		visits.put("Apple Inc.", "1 t");
	}

	public String echo(String value) {
		return value;
	}

	public String test() {
		return "Test";
	}

	public int checkSum() {
		return 0;
	}

	public OMElement statistic() {
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMElement root = fac.createOMElement(new QName("root"));
		for (String company : companies) {
			OMElement companyElement = fac
					.createOMElement(new QName("company"));
			OMElement name = fac.createOMElement(new QName("name"));
			name.setText(company);
			companyElement.addChild(name);
			OMElement userCount = fac.createOMElement(new QName("users"));
			userCount.setText(users.get(company));
			companyElement.addChild(userCount);
			OMElement visitCount = fac.createOMElement(new QName("visits"));
			visitCount.setText(visits.get(company));
			companyElement.addChild(visitCount);
			root.addChild(companyElement);
		}
		return root;
	}

}
