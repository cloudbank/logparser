package com.ef.logparse;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.mysql.jdbc.PreparedStatement;

@Repository
@Transactional
public class LogParserRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;
	private static final Logger log = LoggerFactory.getLogger(LogParserRepository.class);
	ExecutorService es = Executors.newSingleThreadExecutor();

	public int getLogEntries(String startDate, String duration, int threshold) {
		
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

		/*
		 * es.execute(new Runnable() {
		 * 
		 * @Override public void run() {
		 */
		log.info("Begin insert into blocked table.");
		insertBatch(getIps(al), threshold, sDate, duration);
		log.info("Query inserted into blocked table.");
		/*
		 * }
		 * 
		 * });
		 */
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

		return al.size();

	}
	

	private ArrayList<String> getIps(ArrayList<LinkedCaseInsensitiveMap> al) {
		List<String> sl = new ArrayList<>();
		for (LinkedCaseInsensitiveMap m : al) {
		    sl.add(m.entrySet().stream().findFirst().get().toString().split("=")[1]);
		}
		return (ArrayList<String>) sl;
	}

	public void insertBatch(final ArrayList<String> al, int threshold, LocalDateTime logtime, String duration) {

		String sql = "INSERT IGNORE INTO blocked " + "(logtime, ip, blockComment) VALUES (?, ?, ?)";
		String comment = "more than " + threshold + " " + duration + " requests";
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(java.sql.PreparedStatement ps, int i) throws SQLException {

				ps.setDate(1, java.sql.Date.valueOf(logtime.toLocalDate()));
				ps.setString(2, al.get(i));

				ps.setString(3, comment);
			}

			@Override
			public int getBatchSize() {
				return al.size();
			}

		});
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
