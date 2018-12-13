
package app;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TripRepositoryTests {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private TripRepository tripRepository;

    @Before
    public void deleteAllBeforeTests() throws Exception {
        tripRepository.deleteAll();
    }


    @Test
    public void shouldReturnTripRepositoryIndex() throws Exception {

        mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk()).andExpect(
                jsonPath("$._links.trips").exists());
    }



    @Test
    public void shouldCreateTripEntity() throws Exception {

        mockMvc.perform(post("/trips").content(
                "{\"name\": \"sample trip name\"}")).andExpect(
                status().isCreated()).andExpect(
                header().string("Location", containsString("trips/")));
    }



    @Test
    public void shouldRetrieveTripEntity() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/trips").content(
                "{\"name\": \"sample trip name\"}")).andExpect(
                status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
                jsonPath("$.name").value("sample trip name"));
    }



    @Test
    public void shouldQueryTripEntity() throws Exception {

        mockMvc.perform(post("/trips").content(
                "{ \"name\": \"sample trip name\"}")).andExpect(
                status().isCreated());

        mockMvc.perform(
                get("/trips/search/findByName?name={name}", "sample trip name")).andExpect(
                status().isOk()).andExpect(
                jsonPath("$._embedded.trips[0].name").value(
                        "sample trip name"));
    }



    @Test
    public void shouldUpdateTripEntity() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/trips").content(
                "{\"name\": \"sample trip name\"}")).andExpect(
                status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");

        mockMvc.perform(put(location).content(
                "{\"name\": \"sample trip changed name\"}")).andExpect(
                status().isNoContent());

        mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
                jsonPath("$.name").value("sample trip changed name"));
    }





    @Test
    public void shouldDeleteTripEntity() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/trips").content(
                "{ \"name\": \"sample trip name\"}")).andExpect(
                status().isCreated()).andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        mockMvc.perform(delete(location)).andExpect(status().isNoContent());

        mockMvc.perform(get(location)).andExpect(status().isNotFound());
    }
}