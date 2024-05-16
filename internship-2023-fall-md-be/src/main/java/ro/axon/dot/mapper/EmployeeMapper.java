package ro.axon.dot.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ro.axon.dot.domain.entities.EmployeeEty;
import ro.axon.dot.model.request.CreateEmployeeRequest;
import ro.axon.dot.model.response.EmployeeDetailsListItem;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    @Mapping(target = "teamDetails", source = "team")
    @Mapping(target = "totalVacationDays", ignore = true)
    EmployeeDetailsListItem mapEmployeeEtyToEmployeeDto(EmployeeEty employeeEty);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "team", ignore = true)
    @Mapping(target = "firstName", source = "firstname")
    @Mapping(target = "lastName", source = "lastname")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "contractEndDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "empYearlyDaysOffEties", ignore = true)
    @Mapping(target = "crtUsr", ignore = true)
    @Mapping(target = "crtTms", ignore = true)
    @Mapping(target = "mdfUsr", ignore = true)
    @Mapping(target = "mdfTms", ignore = true)
    @Mapping(target = "leaveRequestEties", ignore = true)
    EmployeeEty mapToEmployeeEty(CreateEmployeeRequest createEmployeeRequest);
}
