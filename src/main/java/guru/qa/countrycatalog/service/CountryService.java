package guru.qa.countrycatalog.service;

import guru.qa.countrycatalog.domain.CountryGql;
import guru.qa.countrycatalog.domain.CountryInputGql;
import guru.qa.countrycatalog.domain.CountryJson;
import guru.qa.countrycatalog.domain.DeleteResponseGql;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CountryService {

    List<CountryJson> allCountries();

    @Nonnull
    List<CountryGql> allGqlCountries();

    CountryJson countryByCode(String code);

    @Nullable
    CountryGql countryGqlByCode(@Nonnull String code);

    CountryJson createCountry(CountryJson countryJson);

    @Transactional
    @Nonnull CountryGql createGqlCountry(@Nonnull CountryInputGql country);

    CountryJson updateCountryByCode(String code, CountryJson countryJson);

    @Transactional
    @Nonnull CountryGql updateCountryGqlByCode(String code, CountryInputGql country);

    void deleteCountryByCode(String code);

    @Transactional
    DeleteResponseGql deleteCountryGqlByCode(@Nonnull String code);
}
