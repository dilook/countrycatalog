package guru.qa.countrycatalog.controller.graphql;

import guru.qa.countrycatalog.domain.CountryGql;
import guru.qa.countrycatalog.domain.CountryInputGql;
import guru.qa.countrycatalog.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller()
public class CountryMutationController {

    private final CountryService countryService;

    @Autowired
    public CountryMutationController(CountryService countryService) {
        this.countryService = countryService;
    }

    @MutationMapping
    public CountryGql addCountry(@Argument CountryInputGql input) {
        return countryService.createGqlCountry(input);
    }

    @MutationMapping
    public CountryGql updateCountry(@Argument String code, @Argument CountryInputGql input) {
        return countryService.updateCountryGqlByCode(code, input);
    }

    @MutationMapping
    public void deleteCountry(@Argument String code) {
        countryService.deleteCountryByCode(code);
    }
}
