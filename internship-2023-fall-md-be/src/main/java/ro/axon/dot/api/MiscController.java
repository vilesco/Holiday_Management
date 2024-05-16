package ro.axon.dot.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ro.axon.dot.model.response.LegallyDaysOffList;
import ro.axon.dot.model.response.LegallyDaysOffListItem;
import ro.axon.dot.model.response.RolesList;
import ro.axon.dot.service.LegallyDaysOffService;
import ro.axon.dot.service.RolesService;

import java.util.List;

import static ro.axon.dot.constants.Constants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(MISC_URI)
public class MiscController {

    private final RolesService rolesService;
    private final LegallyDaysOffService legallyDaysOffService;

    @GetMapping(ROLES)
    public RolesList getRoles() {
        return rolesService.getRoles();
    }

    @GetMapping(LEGALLY_DAY_OFF)
    public LegallyDaysOffList getLegallyDaysOff(@RequestParam(name = "periods", required = false) List<String> periods,
                                                @RequestParam(name = "years", required = false) List<String> years) {


        List<LegallyDaysOffListItem> legalDaysOff = legallyDaysOffService.getLegalDaysOff(periods, years);

        LegallyDaysOffList legallyDaysOffList = new LegallyDaysOffList();
        legallyDaysOffList.setLegallyDaysOffListItemList(legalDaysOff);

        return legallyDaysOffList;

    }
}


