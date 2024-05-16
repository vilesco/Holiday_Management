package ro.axon.dot.api;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import javax.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ro.axon.dot.model.response.TeamDetailsList;
import ro.axon.dot.model.response.TeamDetailsListItem;
import ro.axon.dot.service.TeamService;

@WebMvcTest(TeamApi.class)
@ContextConfiguration(classes = {TeamApi.class})
@AutoConfigureMockMvc(addFilters = false)
class TeamApiTest {

  @Inject
  MockMvc mockMvc;
  @MockBean
  private TeamService teamService;
  @Inject
  private WebApplicationContext webApplicationContext;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  void getTeamList_success() throws Exception {
    var teamDetailsList = new TeamDetailsList();
    teamDetailsList.setItems(Arrays
        .asList(new TeamDetailsListItem(), new TeamDetailsListItem()));
    when(teamService.getTeamsDetails()).thenReturn(teamDetailsList);
    mockMvc.perform(get("/api/v1/teams")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", hasSize(2)));
    verify(teamService).getTeamsDetails();
  }
}