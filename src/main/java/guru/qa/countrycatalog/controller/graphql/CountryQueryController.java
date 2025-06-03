package guru.qa.countrycatalog.controller.graphql;

import guru.qa.countrycatalog.domain.CountryGql;
import guru.qa.countrycatalog.domain.CountryJson;
import guru.qa.countrycatalog.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller()
public class CountryQueryController {

    private final CountryService countryService;

    @Autowired
    public CountryQueryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @QueryMapping
    public List<CountryGql> all() {
        return countryService.allGqlCountries();
    }

    @QueryMapping
    public CountryGql country(@Argument String code) {
        return countryService.countryGqlByCode(code);
    }

}
