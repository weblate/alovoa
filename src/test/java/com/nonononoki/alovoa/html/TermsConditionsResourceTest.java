package com.nonononoki.alovoa.html;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TermsConditionsResourceTest {
	
	@Autowired
	private TermsConditionsResource termsConditionsResource;

	@Test
	public void test() throws Exception {
		termsConditionsResource.termsConditions();
	}
}
