package com.thrivent.riskclass.service.impl;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.thrivent.riskclass.bean.RickClassConstant;
import com.thrivent.riskclass.bean.Table;
import com.thrivent.riskclass.service.RiskClassService;

@Service
public class RiskClassImpl implements RiskClassService {
	
	//https://howtodoinjava.com/spring-boot2/read-file-from-resources/
	@Autowired
	ResourceLoader resourceLoader;

	@Override
	public String getRickClass(int age, String height, Integer wheight, String tobaccoUser, int tobaccoLastUsed) {
		String rickClass = RickClassConstant.NO_QUOTE; 
		List<Table> tableList = null;
		try {
			if (age >= 17 && age <= 60) { // here checking condition for age between 17 to 60
				tableList = readCSV("Table1");
				rickClass = findRickClassFromTables(tableList, height, wheight);
				/*
				 * If risk class falls under Rated column in Table A1 or A2, return "No Quote"
				 * If height/weight are out of table, return "No Quote"
				 */
				if (tobaccoUser.equals("no") && !rickClass.equals(RickClassConstant.NO_QUOTE)) {
					return rickClass+RickClassConstant.NON_TOBACCO;
				}else if(rickClass.equals(RickClassConstant.NO_QUOTE)) {
					return rickClass;
				}
				return getRickClassUsingTableC(tobaccoLastUsed, rickClass);
			} else if (age >= 61 && age <= 75) { // here checking condition for age between 61 to 75
				tableList = readCSV("Table2");
				rickClass = findRickClassFromTables(tableList, height, wheight);
				/*
				 * If risk class falls under Rated column in Table A1 or A2, return "No Quote"
				 * If height/weight are out of table, return "No Quote"
				 */
				if (tobaccoUser.equals("no") && !rickClass.equals(RickClassConstant.NO_QUOTE)) {
					return rickClass+RickClassConstant.NON_TOBACCO;
				}else if(rickClass.equals(RickClassConstant.NO_QUOTE)) {
					return rickClass;
				}
				return getRickClassUsingTableC(tobaccoLastUsed, rickClass);
			} else {
				return rickClass;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rickClass;
	}

	public List<Table> readCSV(String tableName) throws Exception {
		CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');
		CSVParser parser = new CSVParser(new FileReader(getFileLocation(tableName)), format);
		List<Table> tableList = new ArrayList<Table>();
		for (CSVRecord record : parser) {
			Table Table = new Table();
			Table.setHeight(record.get(RickClassConstant.HEIGHT));
			Table.setPreferred(fillWheights(record.get(RickClassConstant.PREFERRED)));
			Table.setPreferredBest(fillWheights(record.get(RickClassConstant.PREFERRED_BEST)));
			Table.setRated(fillWheights(record.get(RickClassConstant.RATED)));
			Table.setStandard(fillWheights(record.get(RickClassConstant.STANDARD)));
			Table.setSuperPreferred(fillWheights(record.get(RickClassConstant.SUPER_PREFERRED)));
			tableList.add(Table);

		}
		parser.close();
		return tableList;
	}

	public static List<Integer> fillWheights(String values) {
		if (values != null && !values.isEmpty()) {
			List<Integer> ibsList = new ArrayList<Integer>();
			String[] ibsArray = values.split("-");
			int startNumber = Integer.valueOf(ibsArray[0]);
			int endNumber = Integer.valueOf(ibsArray[1]);
			for (int i = startNumber; i <= endNumber; i++) {
				ibsList.add(i);
			}
			return ibsList;
		}

		return null;

	}

	public String findRickClassFromTables(List<Table> tableList, String height, Integer wheight) {
		String rikClass = RickClassConstant.NO_QUOTE;

		if (!tableList.isEmpty()) {
			for (Table table : tableList) {
				if (table.getHeight().contentEquals(height)) {
					if (table.getPreferred() != null && table.getPreferred().contains(wheight)) {
						rikClass = RickClassConstant.PREFERRED;
						break;
					} else if (table.getPreferredBest() != null && table.getPreferredBest().contains(wheight)) {
						rikClass =RickClassConstant.PREFERRED_BEST;
						break;
					} else if (table.getStandard() != null && table.getStandard().contains(wheight)) {
						rikClass =RickClassConstant.STANDARD;
						break;
					} else if (table.getSuperPreferred() != null && table.getSuperPreferred().contains(wheight)) {
						rikClass = RickClassConstant.SUPER_PREFERRED;
						break;
					} else if (table.getRated() != null && table.getRated().contains(wheight)) {
						rikClass = RickClassConstant.RATED;
						break;
					}

				}

			}
		}
		return rikClass;

	}

	public String getRickClassUsingTableC(int tobaccoLastUsedYears, String tableARickClass) {
		String rickClass = RickClassConstant.NO_QUOTE;
		List<String> rickClassList = new ArrayList<String>();
		if (tobaccoLastUsedYears < 1) {
			rickClassList.add("Preferred Tobacco");
			rickClassList.add("Standard Tobacco");
			rickClassList.add("Rated Tobacco");
		} else if (tobaccoLastUsedYears == 1) {
			rickClassList.add("Standard Non Tobacco");
			rickClassList.add("Rated Non Tobacco");
		} else if (tobaccoLastUsedYears == 2) {
			rickClassList.add("Preferred Non Tobacco");
		} else if (tobaccoLastUsedYears == 3) {
			rickClassList.add("Super Preferred Non Tobacco");
		} else if (tobaccoLastUsedYears == 5) {
			rickClassList.add("Preferred Best Non Tobacco");
		}
		Map<String, Integer> tableBMap = getTableB();
		List<Integer> tableBSelectedNum = new ArrayList<>();
		for (String tableC : rickClassList) {
			tableBSelectedNum.add(tableBMap.get(tableC));
		}
		Collections.sort(tableBSelectedNum);
		rickClass = getKey(tableBMap, tableBSelectedNum.get(0));
		if (tableBMap.get(rickClass) < tableBMap.get(tableARickClass+RickClassConstant.NON_TOBACCO)) {
			return tableARickClass+RickClassConstant.NON_TOBACCO; 
		} else {
			return rickClass;
		}

	}

	public <K, V> K getKey(Map<K, V> map, V value) {
		for (Entry<K, V> entry : map.entrySet()) {
			if (entry.getValue().equals(value)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public Map<String, Integer> getTableB() {
		Map<String, Integer> tableBMap = new HashMap<>();
		tableBMap.put("Preferred Best Non Tobacco", 1);
		tableBMap.put("Super Preferred Non Tobacco", 2);
		tableBMap.put("Preferred Non Tobacco", 3);
		tableBMap.put("Standard Non Tobacco", 4);
		tableBMap.put("Rated Non Tobacco", 5);
		tableBMap.put("Preferred Tobacco", 6);
		tableBMap.put("Standard Tobacco", 7);
		tableBMap.put("Rated Tobacco", 8);
		return tableBMap;

	}

	public String getFileLocation(String fileName) throws Exception {
		File file=new File("../src/main/resources/static/"+fileName+".csv");
		return file.getAbsolutePath();
	}
/**
 * rickClassServiceUrl = "http://localhost:8080/rickClass/rest/age/125/height/3'8%22/wheight/74/tobaccoUser/no/tobaccoLastUsed/0"
 * @param rickClassServiceUrl
 * @return
 */
	private String getRickClass(String rickClassServiceUrl)
	{
	    RestTemplate restTemplate = new RestTemplate();
	    String rickClass = restTemplate.getForObject(rickClassServiceUrl, String.class);
		return rickClass;
	}

}
