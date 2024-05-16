package ro.axon.dot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ro.axon.dot.domain.entities.LeaveRequestEty;
import ro.axon.dot.model.response.LeaveRequestItem;
import ro.axon.dot.model.response.RequestDetailsListItem;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface LeaveRequestMapper {

      LeaveRequestMapper INSTANCE = Mappers.getMapper(LeaveRequestMapper.class);


      @Mapping(target = "employeeDetails.firstName",source = "employeeEty.firstName")
      @Mapping(target = "employeeDetails.lastName",source = "employeeEty.lastName")
      @Mapping(target = "employeeDetails.employeeId",source = "employeeEty.id")

      RequestDetailsListItem mapRequestEtyToRequestDto(LeaveRequestEty requestEty);

      LeaveRequestItem mapRequestEtyToLeaveRequestItem(LeaveRequestEty requestEty);

}
