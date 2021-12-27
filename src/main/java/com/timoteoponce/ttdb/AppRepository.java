package com.timoteoponce.ttdb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
	private Path dataPath = Paths.get("./data.json");

	private Collection<Activity> readStoredActivities() {
		if (Files.exists(dataPath)) {
			var mapper = mapper();
			try {
				return new ArrayList<>(Arrays.asList(mapper.readValue(dataPath.toFile(), Activity[].class)));
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
		var mapper = mapper();
		var writer = mapper.writer();
		try {
			writer.writeValue(dataPath.toFile(), items);
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
			Files.deleteIfExists(dataPath);
		} catch (IOException e) {
			log.warn(e.getMessage());
		}
	}

	public String toJson(Object value) {
		try {
			return mapper().writeValueAsString(value);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
