package com.timoteoponce.ttdb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
public class AppRepository {

	private static final Logger log = LoggerFactory.getLogger(AppRepository.class);

	private Collection<Activity> readStoredActivities() {
		var path = Paths.get("./data.json");
		if (Files.exists(path)) {
			var mapper = mapper();
			try {
				return new ArrayList<>(Arrays.asList(mapper.readValue(path.toFile(), Activity[].class)));
			} catch (Exception e) {
				log.warn(e.getMessage());
			}
		}
		return new ArrayList<>();
	}

	private ObjectMapper mapper() {
		var mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		return mapper;
	}

	private void writeStoredActivities(Collection<Activity> items) {
		var path = Paths.get("./data.json");
		var mapper = mapper();
		var writer = mapper.writer();
		try {
			writer.writeValue(path.toFile(), items);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
	}

	public Collection<Activity> find() {
		return readStoredActivities();
	}

	public Optional<Activity> get(String title) {
		return readStoredActivities().stream().filter(t -> t.getTitle().equalsIgnoreCase(title)).findAny();
	}

	public Activity store(Activity activity) {
		var items = readStoredActivities().stream().filter(t -> !t.getTitle().equals(activity.getTitle()))
				.collect(Collectors.toList());
		items.add(activity);
		writeStoredActivities(items);
		return activity;
	}

	public void reset() {
		try {
			Files.deleteIfExists(Paths.get("./data.json"));
		} catch (IOException e) {
			log.warn(e.getMessage());
		}
	}

}
