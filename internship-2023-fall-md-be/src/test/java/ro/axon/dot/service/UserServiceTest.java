package ro.axon.dot.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.axon.dot.domain.entities.EmployeeEty;
import ro.axon.dot.domain.entities.TeamEty;
import ro.axon.dot.model.response.UserDetailsResponse;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private EmployeeHandler employeeHandler;

    @Mock
    private SecurityAccessTokenHandler tokenHandler;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetLoggedUserDetails() {
        EmployeeEty employee = new EmployeeEty();
        employee.setId("employeeId");
        employee.setUsername("testUser");
        employee.setRole("USER");
        //Other properties are not used ,so no need to set/verify them
        TeamEty team = new TeamEty();
        team.setId(1L);
        team.setName("Team A");
        employee.setTeam(team);

        when(tokenHandler.getEmployeeIdFromToken()).thenReturn("employeeId");
        when(employeeHandler.getEmployee(eq("employeeId"))).thenReturn(employee);

        UserDetailsResponse userDetails = userService.getLoggedUserDetails();

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("testUser");
        assertThat(userDetails.getEmployeeId()).isEqualTo("employeeId");
        assertThat(userDetails.getRoles().get(0)).isEqualTo("USER");

        UserDetailsResponse.TeamDetails teamDetails = userDetails.getTeamDetails();
        assertThat(teamDetails).isNotNull();
        assertThat(teamDetails.getTeamId()).isEqualTo(1L);
        assertThat(teamDetails.getName()).isEqualTo("Team A");

        verify(tokenHandler).getEmployeeIdFromToken();
        verify(employeeHandler).getEmployee(eq("employeeId"));
    }

}
