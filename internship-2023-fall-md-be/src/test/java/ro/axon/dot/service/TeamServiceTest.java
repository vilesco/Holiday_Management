package ro.axon.dot.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.axon.dot.domain.entities.TeamEty;
import ro.axon.dot.domain.enums.Status;
import ro.axon.dot.domain.repositories.TeamRepository;
import ro.axon.dot.model.request.TeamRequest;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

  @Mock
  private TeamRepository teamRepository;

  @InjectMocks
  private TeamService teamService;

  @Mock
  private SecurityAccessTokenHandler securityAccessTokenHandler;

  @Mock
  private Clock clock;

  @Test
  void getAllTeams() {
    var team1 = new TeamEty();
    team1.setId(1L);
    team1.setName("Test");
    team1.setCrtUsr("test");
    team1.setCrtTms(Instant.parse("2023-06-17T21:00:00Z"));
    team1.setMdfUsr("test");
    team1.setMdfTms(Instant.parse("2023-06-17T21:00:00Z"));
    team1.setStatus(Status.ACTIVE);

    when(teamRepository.findByStatus(Status.ACTIVE)).thenReturn(List.of(team1));

    var activeTeams = teamService.getTeamsDetails();
    assertThat(activeTeams).isNotNull();
    assertThat(activeTeams.getItems()).hasSize(1);

    var teamDetailsListItem = activeTeams.getItems().get(0);
    assertThat(teamDetailsListItem.getId()).isEqualTo(1L);
    assertThat(teamDetailsListItem.getName()).isEqualTo("Test");
    assertThat(teamDetailsListItem.getCrtUsr()).isEqualTo("test");
    assertThat(teamDetailsListItem.getCrtTms()).isEqualTo(Instant.parse("2023-06-17T21:00:00Z"));
    assertThat(teamDetailsListItem.getMdfUsr()).isEqualTo("test");
    assertThat(teamDetailsListItem.getMdfTms()).isEqualTo(Instant.parse("2023-06-17T21:00:00Z"));
    assertThat(teamDetailsListItem.getStatus()).isEqualTo(Status.ACTIVE);

    verify(teamRepository).findByStatus(any());

  }

  @Test
  void testCreateEmployeeDuplicateUsername() {
    TeamRequest teamRequest = new TeamRequest();
    teamRequest.setName("newTeam");

    when(securityAccessTokenHandler.getEmployeeIdFromToken()).thenReturn("employeeId");
    when(clock.instant()).thenReturn(Instant.now());

    teamService.createNewTeam(teamRequest);

    verify(teamRepository).save(any(TeamEty.class));
  }

}