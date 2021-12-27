package com.timoteoponce.ttdb;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Utility class used for adapting the stored-activity data into response data structures.
 * 
 * @author timoteo
 *
 */
public class ActivityResponse {

	public static Map<String, Object> map(Activity t) {
		var result = new HashMap<String, Object>();		
		var timeframes = Map.of("daily", mapDaily(t.getLog()),"weekly",mapWeekly(t.getLog()), "monthly",mapMonthly(t.getLog()));
		result.put("timeframes",  timeframes);
		result.put("title",  t.getTitle());
		return result;
	}

	private static Map<String, Integer> mapMonthly(List<ActivityLog> log) {
		var current = LocalDate.now();
		var previous = current.minusMonths(1);
		return Map.of(
				"current", log.stream().filter(t -> t.hasSameMonth(current)).mapToInt(t -> t.getTime()).sum()
				, "previous",log.stream().filter(t -> t.hasSameMonth(previous)).mapToInt(t -> t.getTime()).sum()
		);
	}

	private static Map<String, Integer> mapWeekly(List<ActivityLog> log) {
		var now = LocalDate.now();
		var current = now.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
		var previous = now.minusWeeks(1).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
		return Map.of(
				"current", log.stream().filter(t -> t.hasSameWeek(current)).mapToInt(t -> t.getTime()).sum()
				, "previous",log.stream().filter(t -> t.hasSameWeek(previous)).mapToInt(t -> t.getTime()).sum()
		);
	}

	private static Map<String, Integer> mapDaily(List<ActivityLog> log) {
		var current = LocalDate.now();
		var previous = current.minusDays(1);
		return Map.of(
				"current", log.stream().filter(t -> t.hasSameDay(current)).mapToInt(t -> t.getTime()).sum()
				, "previous",log.stream().filter(t -> t.hasSameDay(previous)).mapToInt(t -> t.getTime()).sum()
		);
	}

}
