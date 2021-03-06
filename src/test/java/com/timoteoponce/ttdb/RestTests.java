package com.timoteoponce.ttdb;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class RestTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private AppRepository repository;

	private final LocalDate today = LocalDate.now();

	/**
	 * Test that verifies the response when there's no data
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetActivitiesWhenEmpty() throws Exception {
		repository.reset();
		mvc.perform(get("/activities")).andExpect(status().isOk()).andExpect(content().json("[]"));
	}

	/**
	 * Test that verifies the creation and listing of an activity
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPostAndGetActivity() throws Exception {
		repository.reset();

		mvc.perform(post("/activities/jogging").contentType(MediaType.APPLICATION_JSON).content(activity(today, 10)))
				.andExpect(status().isOk());

		mvc.perform(get("/activities")).andExpect(status().isOk()).andExpect(content().json(
				"[{\"timeframes\":{\"monthly\":{\"previous\":0,\"current\":10},\"weekly\":{\"previous\":0,\"current\":10},\"daily\":{\"previous\":0,\"current\":10}},\"title\":\"jogging\"}]"));
	}

	private String activity(LocalDate date, int time) {
		return repository.toJson(Map.of("date", date, "time", time));
	}

	/**
	 * Test that verifies the creation, update and listing of an activity with data
	 * on different dates
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPostAndGetActivityWithPastDayData() throws Exception {
		repository.reset();

		mvc.perform(post("/activities/jogging").contentType(MediaType.APPLICATION_JSON).content(activity(today, 10)))
				.andExpect(status().isOk());
		mvc.perform(put("/activities/jogging").contentType(MediaType.APPLICATION_JSON)
				.content(activity(today.minusDays(1), 10))).andExpect(status().isOk());

		mvc.perform(get("/activities")).andExpect(status().isOk()).andExpect(content().json(
				"[{\"timeframes\":{\"daily\":{\"previous\":10,\"current\":10},\"monthly\":{\"previous\":0,\"current\":20},\"weekly\":{\"previous\":10,\"current\":10}},\"title\":\"jogging\"}]"));
	}
}
