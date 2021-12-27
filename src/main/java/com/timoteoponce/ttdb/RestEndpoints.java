package com.timoteoponce.ttdb;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Main end-point that handles incoming requests and proceeds to interact with
 * the different components that will end up storing/reading/updating data in
 * the repositories.
 * 
 * @author timoteo
 *
 */
@RestController
public class RestEndpoints {
	private static final Logger log = LoggerFactory.getLogger(RestEndpoints.class);

	private final AppRepository repository;

	@Autowired
	@Lazy
	public RestEndpoints(AppRepository repository) {
		this.repository = repository;
	}

	/**
	 * Return all stored activities with the following structure:
	 * 
	 * <pre>
	 * [
	{
		"timeframes": {
			"daily": {
				"current": 10,
				"previous": 0
			},
			"monthly": {
				"current": 10,
				"previous": 5
			},
			"weekly": {
				"current": 10,
				"previous": 0
			}
		},
		"title": "jogging"
	}
	]
	 * </pre>
	 * 
	 * @return non-null list of stored activities 
	 */
	@GetMapping("/activities")
	public List<Map<String, Object>> getActivities() {
		return repository.find().stream().map(t -> ActivityResponse.map(t)).collect(Collectors.toList());
	}

	/**
	 * Creates a new activity with an initial log entry
	 * @param title unique identifier for the activity
	 * @param request date and time values to log
	 * @return created activity
	 */
	@PostMapping("/activities/{title}")
	public ResponseEntity<Object> postActivity(@PathVariable("title") String title, @RequestBody PostActivity request) {
		if (repository.get(title).isPresent()) {
			log.warn("Activity already exists: " + title);
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		} else {
			var activity = repository.store(new Activity(title).append(request.date, request.time));
			log.info("Activity created: " + title);
			return ResponseEntity.ok(ActivityResponse.map(activity));
		}
	}

	/**
	 * Updates an existing activity appending an given log entry
	 * @param title unique identifier for the existing activity
	 * @param request date and time values to log
	 * @return updated activity
	 */
	@PutMapping("/activities/{title}")
	public ResponseEntity<Object> putActivity(@PathVariable("title") String title, @RequestBody PostActivity request) {
		Optional<Activity> activity = repository.get(title);
		if (activity.isPresent()) {
			repository.store(activity.get().append(request.date, request.time));
			log.info("Activity updated: " + title);
			return ResponseEntity.ok(ActivityResponse.map(activity.get()));
		} else {
			return ResponseEntity.notFound().build();
		}
	}

}
