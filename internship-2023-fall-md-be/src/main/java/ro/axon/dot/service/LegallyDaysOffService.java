package ro.axon.dot.service;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.axon.dot.domain.entities.LegallyDaysOffEty;
import ro.axon.dot.mapper.LegallyDaysOffMapper;
import ro.axon.dot.model.response.LegallyDaysOffListItem;
@RequiredArgsConstructor
@Service
public class LegallyDaysOffService {

    private final LegallyDaysOffPersistenceManager legallyDaysOffPersistenceManager;

    public List<LegallyDaysOffListItem> getLegalDaysOff(List<String> periods, List<String> years) {
        List<LegallyDaysOffEty> legallyDaysOff = this.legallyDaysOffPersistenceManager.getAllLegallyDaysOffDb();
        if ((years == null || years.isEmpty()) && (periods == null || periods.isEmpty())) {
            return legallyDaysOff.stream().map(LegallyDaysOffMapper.INSTANCE::mapLegallyDaysOffEntityDto).collect(Collectors.toList());
        } else if (years != null && !years.isEmpty()) {
            return legallyDaysOff.stream()
                    .filter(legallyDaysOffEty -> {
                        LocalDate localDate = legallyDaysOffEty.getDate();
                        String dbYear = String.valueOf(localDate.getYear());
                        return years.contains(dbYear);
                    })
                    .map(LegallyDaysOffMapper.INSTANCE::mapLegallyDaysOffEntityDto)
                    .collect(Collectors.toList());
        } else {
            return legallyDaysOff.stream()
                    .filter(legallyDaysOffEty -> {
                        LocalDate localDate = legallyDaysOffEty.getDate();
                        String dateAsPeriod = periodConvertor(localDate);
                        return periods.contains(dateAsPeriod);
                    })
                    .map(LegallyDaysOffMapper.INSTANCE::mapLegallyDaysOffEntityDto)
                    .collect(Collectors.toList());
        }

    }
    private static String periodConvertor(LocalDate inputPeriod) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        try {
            var date = new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(inputPeriod));
            return dateFormat.format(date);
        } catch (ParseException e) {
            throw new IllegalStateException("Failed to convert period.");
        }

    }
}