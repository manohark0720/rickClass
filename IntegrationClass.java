package com.thrivent.riskclass.service.impl;

import org.springframework.web.client.RestTemplate;

import com.thrivent.riskclass.bean.RickClassConstant;

public class IntegrationClass {
	
	/*
	 * if (tobaccoUser.equals("no") &&
	 * !rickClass.equals(RickClassConstant.NO_QUOTE)) { return
	 * rickClass+RickClassConstant.NON_TOBACCO; }else
	 * if(rickClass.equals(RickClassConstant.NO_QUOTE)) { return rickClass; }
	 */
	
	
	/*
	 * #2.x context path setting
	 * server.servlet.context-path=/rickClass
	 * 
	 * #1.X context path setting 
	 * #server.context-path=/rickClass
	 */
	/**
 * rickClassServiceUrl = "http://localhost:8080/rickClass/rest/age/125/height/3'8%22/wheight/74/tobaccoUser/no/tobaccoLastUsed/0"
 * @param rickClassServiceUrl
 * @return
 */
	private String getRickClass(String rickClassServiceUrl)
	{
	    RestTemplate restTemplate = new RestTemplate();
	    SubmitResckClassAPIRequest SubmitResckClassAPIRequest=new SubmitResckClassAPIRequest();
	    //set the values
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    HttpEntity<SubmitResckClassAPIRequest> requestEntity =   new HttpEntity<>(payload, headers);
	    String response = restTemplate.exchange("url", HttpMethod.POST, requestEntity,String.class);
	    String rickClass = restTemplate.getForObject(rickClassServiceUrl, String.class);
		return rickClass;
	}

}
