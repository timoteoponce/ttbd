package com.timoteoponce.ttdb;

import java.time.LocalDate;
import java.time.temporal.IsoFields;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ActivityLog {
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate timestamp = LocalDate.now();
	private int time = 0;
	
	public ActivityLog() {	
	}

	public ActivityLog(LocalDate timestamp, int time) {
		this.timestamp = timestamp;
		this.time = time;
	}

	public boolean hasSameDay(LocalDate other) {
		return timestamp.getDayOfYear() == other.getDayOfYear();
	}

	public boolean hasSameWeek(int weekOfYear) {
		return timestamp.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) == weekOfYear;
	}

	public boolean hasSameMonth(LocalDate other) {
		return timestamp.getMonthValue() == other.getMonthValue();
	}

	public ActivityLog appendTime(int hours) {
		this.time += hours;
		return this;
	}

	public LocalDate getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDate timestamp) {
		this.timestamp = timestamp;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

}
