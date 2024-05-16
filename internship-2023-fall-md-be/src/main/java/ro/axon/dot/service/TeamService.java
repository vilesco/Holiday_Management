package ro.axon.dot.service;

import java.time.Clock;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.axon.dot.domain.enums.Status;
import ro.axon.dot.domain.repositories.TeamRepository;
import ro.axon.dot.mapper.TeamMapper;
import ro.axon.dot.model.response.TeamDetailsList;
import ro.axon.dot.model.request.TeamRequest;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final SecurityAccessTokenHandler securityAccessTokenHandler;
    private final TeamRepository teamRepository;
    private final Clock clock;

    public TeamDetailsList getTeamsDetails() {
        var teamDetailsList = new TeamDetailsList();
        teamDetailsList.setItems(teamRepository.findByStatus(Status.ACTIVE).stream().map(TeamMapper.INSTANCE::mapTeamEtyToTeamDto)
                .collect(Collectors.toList()));
        return teamDetailsList;
    }

    @Transactional
    public void createNewTeam(TeamRequest teamRequest){
        var newTeam = TeamMapper.INSTANCE.mapTeamRequestToTeamEty(teamRequest);
        newTeam.setCrtUsr(securityAccessTokenHandler.getEmployeeIdFromToken());
        newTeam.setMdfUsr(securityAccessTokenHandler.getEmployeeIdFromToken());
        newTeam.setCrtTms(clock.instant());
        newTeam.setMdfTms(clock.instant());
        newTeam.setStatus(Status.ACTIVE);

        teamRepository.save(newTeam);
    }
}
