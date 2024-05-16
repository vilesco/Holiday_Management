package ro.axon.dot.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import ro.axon.dot.domain.entities.LegallyDaysOffEty;
import ro.axon.dot.domain.repositories.LegallyDaysOffRepository;

@Component
@RequiredArgsConstructor
public class LegallyDaysOffPersistenceManager {
    private final LegallyDaysOffRepository legallyDaysOffRepository;

    @Cacheable("legallyDayOff")
    public List<LegallyDaysOffEty> getAllLegallyDaysOffDb() {
        return legallyDaysOffRepository.findAll();
    }
}
