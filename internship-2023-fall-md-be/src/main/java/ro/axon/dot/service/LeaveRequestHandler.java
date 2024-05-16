package ro.axon.dot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.axon.dot.domain.entities.LegallyDaysOffEty;
import ro.axon.dot.model.response.LeaveRequestItem;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestHandler {
    private final LegallyDaysOffPersistenceManager legallyDaysOffPersistenceManager;

    public long calculateNumberOffDaysOffInAPeriod(LocalDate startDate, LocalDate endDate) {
        List<LegallyDaysOffEty> legallyDaysOffList = legallyDaysOffPersistenceManager.getAllLegallyDaysOffDb();
        return startDate.datesUntil(endDate.plusDays(1))
                .filter(date ->
                        date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY &&
                                legallyDaysOffList.stream()
                                        .noneMatch(legallyDaysOffEty -> legallyDaysOffEty.getDate().isEqual(date)))
                .count();
    }

    public int calculateLeaveDaysInPeriod(LocalDate startDate, LocalDate endDate, LeaveRequestItem leaveRequest){
        if (!leaveRequest.getStartDate().isBefore(startDate) && !leaveRequest.getEndDate().isAfter(endDate)) {
            return leaveRequest.getNoOfDays();
        }
        if (leaveRequest.getEndDate().isAfter(endDate)) {
            return (int) calculateNumberOffDaysOffInAPeriod(leaveRequest.getStartDate(), endDate);
        }
        return (int) calculateNumberOffDaysOffInAPeriod(startDate, leaveRequest.getEndDate());
    }
}
