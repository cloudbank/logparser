package com.ef.logparse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedCaseInsensitiveMap;

public class LogParser {
	private static final Logger log = LoggerFactory.getLogger(LogParser.class);
	ExecutorService es = Executors.newSingleThreadExecutor();

	public void getLogEntries(JdbcTemplate jdbcTemplate, String startDate, String duration, int threshold) {
		LocalDateTime sDate = convertToLocalDateTime(startDate);
		SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("getLogEntries");
		LocalDateTime endDate = computeEndDate(sDate, duration);
		System.out.println("params " + startDate + "::" + endDate);
		SqlParameterSource in = new MapSqlParameterSource();
		((MapSqlParameterSource) in).addValue("startDate", startDate);
		((MapSqlParameterSource) in).addValue("endDate", endDate);
		((MapSqlParameterSource) in).addValue("threshold", threshold);
		Map<String, Object> out = jdbcCall.execute(in);
		Map.Entry entry = (Entry) (out.entrySet().toArray())[0];

		ArrayList<LinkedCaseInsensitiveMap> al = (ArrayList) entry.getValue();
		es.execute(new Runnable() {

			@Override
			public void run() {
				log.info("Begin insert into blocked table.");
				insertEntries(al, jdbcTemplate, threshold, sDate, duration);
				log.info("Query inserted into blocked table.");
				
			}
			
		});
		
		

		System.out.println(
				"-----------------------------------------------------------------------------------------------------------------------------------");

		System.out.println("Returning " + al.size() + " entries for query: between " + startDate + " and " + endDate
				+ " for " + duration + " requests over " + threshold);

		System.out.println(
				"-----------------------------------------------------------------------------------------------------------------------------------");
		al.stream().forEach(map -> {
			map.entrySet().stream().forEach(it -> {
				System.out.print(it + "\t");
			});
			System.out.println();

		}

		);
		
	}
	@Transactional(rollbackFor=Exception.class)
	private void insertEntries(final ArrayList<LinkedCaseInsensitiveMap> al, JdbcTemplate jdbcTemplate, int threshold,
			LocalDateTime logtime, String duration) {

		for (LinkedCaseInsensitiveMap map : al) {

			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("insertBlockedEntries");
			SqlParameterSource in = new MapSqlParameterSource();
			((MapSqlParameterSource) in).addValue("logtime", logtime);
			((MapSqlParameterSource) in).addValue("ip", map.entrySet().stream().findFirst().get());
			String comment = "more than " + threshold + " " + duration + " requests";
			((MapSqlParameterSource) in).addValue("blockComment", comment);
			Map<String, Object> out = jdbcCall.execute(in);

		}

	}

	
	private LocalDateTime computeEndDate(LocalDateTime startDate, String duration) {
		if (duration.equals("hourly")) {
			return (startDate).plusMinutes(59).plusSeconds(59);
		} else if (duration.equals("daily")) {
			return (startDate).plusDays(1).minusSeconds(1);
		}
		return null;
	}

	private LocalDateTime convertToLocalDateTime(String date) {
		return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss"));

	}

}
