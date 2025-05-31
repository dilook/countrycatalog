package guru.qa.countrycatalog.service;

import guru.qa.countrycatalog.domain.Country;

import java.util.List;

public interface CountryService {

    List<Country> allCountries();
    Country countryByCode(String code);
    Country createCountry(Country country);
    Country updateCountryByCode(String code, Country country);
    void deleteCountryByCode(String code);
}
