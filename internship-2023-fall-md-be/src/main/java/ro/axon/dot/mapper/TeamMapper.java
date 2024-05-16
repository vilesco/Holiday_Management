package ro.axon.dot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ro.axon.dot.domain.entities.TeamEty;
import ro.axon.dot.model.response.TeamDetailsListItem;
import ro.axon.dot.model.request.TeamRequest;

/**
 * Mapper used for converting TeamEty object to TeamDto object
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TeamMapper {

    TeamMapper INSTANCE = Mappers.getMapper(TeamMapper.class);

    TeamDetailsListItem mapTeamEtyToTeamDto(TeamEty teamEty);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "crtUsr", ignore = true)
    @Mapping(target = "crtTms", ignore = true)
    @Mapping(target = "mdfUsr", ignore = true)
    @Mapping(target = "mdfTms", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "employees", ignore = true)
    TeamEty mapTeamRequestToTeamEty(TeamRequest teamRequest);
}
