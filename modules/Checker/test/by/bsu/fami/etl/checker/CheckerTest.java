package by.bsu.fami.etl.checker;

import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CheckerTest {

	protected Checker checker;

	@Before
	public void before() {
		BasicConfigurator.configure();
		checker = new Checker("../../repository/bin");
	}

	@Test
	public void simple() {
		try {
			boolean res = checker.check("my.MyChecker");
			Assert.assertEquals(true, res);
		} catch (CheckerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
