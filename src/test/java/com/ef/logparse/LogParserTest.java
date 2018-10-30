package com.ef.logparse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LogparseApplication.class, initializers = ConfigFileApplicationContextInitializer.class)
public class LogParserTest {
	@Autowired
	LogParserRepository lp;

	@Test
	public void logParserTest() {
		assertEquals(769, lp.getLogEntries("2017-01-01.00:00:00", "daily", 50));
	}

}
