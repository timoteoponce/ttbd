package com.timoteoponce.ttdb;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * An Activity is the main data structure for the application, it uses the
 * 'title' as ident and provides a simple approach for keeping track and
 * filtering logs based on date and registered hours.
 * 
 * @author timoteo
 *
 */
public class Activity {

	private String title;
	private List<ActivityLog> log = new ArrayList<>();

	public Activity() {
	}

	public Activity(String title) {
		this.title = title;
	}

	public Activity append(LocalDate date, int hours) {
		var entry = log.stream().filter(t -> t.getTimestamp().equals(date)).findAny();
		if (entry.isPresent()) {
			entry.get().appendTime(hours);
		} else {
			log.add(new ActivityLog(date, hours));
		}
		return this;
	}

	public String getTitle() {
		return title;
	}

	public List<ActivityLog> getLog() {
		return log;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setLog(List<ActivityLog> log) {
		this.log = log;
	}

}
