package com.ef.logparse.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogEntry {

	private Integer id;
	private String request;
	private String agent;
	private String ip;
	private String code;
	private LocalDateTime logtime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String userAgent) {
		this.agent = userAgent;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String statusCode) {
		this.code = statusCode;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	private LocalDateTime convertDateToLocalTime2(String date) {
		return LocalDateTime.of(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
				LocalDateTime.now().toLocalTime());
	}

	public LogEntry() {
	}

	public LocalDateTime getLogtime() {
		return logtime;
	}

	public void setLogtime(LocalDateTime l) {
		this.logtime = l;
	}

	@Override // delimited by pipe (|)
	public String toString() {
		return ip + "|" + logtime + "|" + code;
	}

}
