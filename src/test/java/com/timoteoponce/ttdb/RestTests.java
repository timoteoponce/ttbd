package com.timoteoponce.ttdb;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

	@Test
	public void testGetActivitiesWhenEmpty() throws Exception {
		repository.reset();
		mvc.perform(get("/activities")).andExpect(status().isOk()).andExpect(content().json("[]"));
	}

	@Test
	public void testPostAndGetActivity() throws Exception {
		repository.reset();

		mvc.perform(post("/activities/jogging").contentType(MediaType.APPLICATION_JSON)
				.content("{\"date\": \"2021-12-27\", \"time\": 10}")).andExpect(status().isOk());

		mvc.perform(get("/activities")).andExpect(status().isOk()).andExpect(content().json(
				"[{\"timeframes\":{\"monthly\":{\"previous\":0,\"current\":10},\"weekly\":{\"previous\":0,\"current\":10},\"daily\":{\"previous\":0,\"current\":10}},\"title\":\"jogging\"}]"));
	}
	
	@Test
	public void testPostAndGetActivityWithPastDayData() throws Exception {
		repository.reset();

		mvc.perform(post("/activities/jogging").contentType(MediaType.APPLICATION_JSON)
				.content("{\"date\": \"2021-12-27\", \"time\": 10}")).andExpect(status().isOk());
		mvc.perform(put("/activities/jogging").contentType(MediaType.APPLICATION_JSON)
				.content("{\"date\": \"2021-12-26\", \"time\": 10}")).andExpect(status().isOk());

		mvc.perform(get("/activities")).andExpect(status().isOk()).andExpect(content().json(
				"[{\"timeframes\":{\"daily\":{\"previous\":10,\"current\":10},\"monthly\":{\"previous\":0,\"current\":20},\"weekly\":{\"previous\":10,\"current\":10}},\"title\":\"jogging\"}]"));
	}

	// GET /activities -- return all activities in the current/previous frame (day,
	// week, month)
	// POST /activities/:title -- add a new activity register for the day on the
	// given activity
	// {:date '2021-12-24' :time 33}

}
