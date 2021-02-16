package com.thrivent.riskclass;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.thrivent.riskclass.service.RiskClassService;

@SpringBootTest
@RunWith(SpringRunner.class)
class RickClassTests {
	
	@Autowired
	RiskClassService riskClassService;

	 
	 
	@Test
	void scenarios1() {
		String rickVlass=riskClassService.getRickClass(70, "4'10\"", 125, "yes", 0);
		assertEquals("Preferred Tobacco", rickVlass);
	}
	
	@Test
	void scenarios2() {
		String rickVlass=riskClassService.getRickClass(90, "5'10\"", 200, "yes", 0);
		assertEquals("No Quote", rickVlass);
	}
	
	
	@Test
	void scenarios3() {
		String rickVlass=riskClassService.getRickClass(45, "9'10\"", 125, "no", 0);
		assertEquals("No Quote", rickVlass); 
	}
	@Test
	void scenarios4() {
		String rickVlass=riskClassService.getRickClass(55, "5'8\"", 185, "yes", 0);
		assertEquals("Preferred Tobacco", rickVlass);
	}
	@Test
	void scenarios5() {
		String rickVlass=riskClassService.getRickClass(20, "5'10\"", 125, "yes", 0);
		assertEquals("Preferred Tobacco", rickVlass);
	}
	@Test
	void scenarios6() {
		String rickVlass=riskClassService.getRickClass(55, "4'8\"", 125, "yes", 5);
		assertEquals("Standard Non Tobacco", rickVlass);
	}
	@Test
	void scenarios7() {
		String rickVlass=riskClassService.getRickClass(65, "4'12\"", 125, "yes", 3);
		assertEquals("No Quote", rickVlass);
	}

}
