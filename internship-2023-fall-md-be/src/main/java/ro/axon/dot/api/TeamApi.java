package ro.axon.dot.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.axon.dot.model.response.TeamDetailsList;
import ro.axon.dot.model.request.TeamRequest;
import ro.axon.dot.service.TeamService;

import javax.validation.Valid;

import static ro.axon.dot.constants.Constants.BASE_URL;
import static ro.axon.dot.constants.Constants.TEAMS;

@RestController
@RequiredArgsConstructor
@RequestMapping(TEAMS)
public class TeamApi {

    private final TeamService teamService;

    @GetMapping
    public ResponseEntity<TeamDetailsList> getTeamDetailsList() {
        return ResponseEntity.ok(teamService.getTeamsDetails());
    }

    @PostMapping
    public ResponseEntity<Void> createTeamDetailsList(@RequestBody @Valid TeamRequest teamRequest) {
       teamService.createNewTeam(teamRequest);

       return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
