package com.ef.logparse;

import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LogparseApplication implements CommandLineRunner {

	@Autowired
	LogParserRepository lp;
	private static final Logger log = LoggerFactory.getLogger(LogparseApplication.class);

	public static void main(String args[]) {
		SpringApplication.run(LogparseApplication.class, args);

	}

	@Override
	public void run(String... strings) throws Exception {
		if (strings.length > 0) {
			String duration = decodeArgs(strings).get("duration");
			String startDate = decodeArgs(strings).get("startDate");
			int threshold = Integer.parseInt(decodeArgs(strings).get("threshold"));
			System.out.println(startDate + " : " + duration + " : " + threshold);
			lp.getLogEntries(startDate, duration, threshold);

		} else {
			System.out.println("USAGE: java -jar logparser-1.0.0.jar --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100");
		}

	}

	public static HashMap<String, String> decodeArgs(String[] array) {
		return (HashMap<String, String>) Stream.of(array).map(elem -> elem.split("\\="))
				.filter(elem -> elem.length == 2).collect(Collectors.toMap(e -> e[0], e -> e[1])).entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey().split("\\-\\-")[1], e -> e.getValue()));
	}

}
