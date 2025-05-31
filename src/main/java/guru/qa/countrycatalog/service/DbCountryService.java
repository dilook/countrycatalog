package guru.qa.countrycatalog.service;

import guru.qa.countrycatalog.data.CountryEntity;
import guru.qa.countrycatalog.data.CountryRepository;
import guru.qa.countrycatalog.domain.Country;
import guru.qa.countrycatalog.exception.CountryNotFoundException;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DbCountryService implements CountryService {

    private final CountryRepository countryRepository;

    @Autowired
    public DbCountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    @Nonnull
    public List<Country> allCountries() {
        return countryRepository.findAll()
                .stream()
                .map(countryEntity -> new Country(countryEntity.getName(), countryEntity.getCode()))
                .toList();
    }

    @Override
    @Nullable
    public Country countryByCode(@Nonnull String code) {
        return countryRepository.findByCode(code);
    }

    @Transactional
    @Override
    public @Nonnull Country createCountry(@Nonnull Country country) {
        return Country.fromEntity(
                countryRepository.save(CountryEntity.fromCountry(country))
        );
    }

    @Transactional
    @Override
    public @Nonnull Country updateCountryByCode(String code, Country country) {
        final CountryEntity foundCountry = countryRepository.findCountryEntityByCode(code);
        if (foundCountry != null) {
            final CountryEntity countryEntity = CountryEntity.fromCountry(country);
            countryEntity.setId(foundCountry.getId());
            return Country.fromEntity(
                    countryRepository.save(countryEntity)
            );
        }
        throw new CountryNotFoundException("Country not found");
    }

    @Transactional
    @Override
    public void deleteCountryByCode(@Nonnull String code) {
        countryRepository.removeCountryEntityByCode(code);
    }
}
