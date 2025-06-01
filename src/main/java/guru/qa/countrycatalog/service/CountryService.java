package guru.qa.countrycatalog.service;

import guru.qa.countrycatalog.domain.CountryJson;

import java.util.List;

public interface CountryService {

    List<CountryJson> allCountries();
    CountryJson countryByCode(String code);
    CountryJson createCountry(CountryJson countryJson);
    CountryJson updateCountryByCode(String code, CountryJson countryJson);
    void deleteCountryByCode(String code);
}
