package guru.qa.countrycatalog.service;

import guru.qa.countrycatalog.data.CountryEntity;
import guru.qa.countrycatalog.data.CountryRepository;
import guru.qa.countrycatalog.domain.CountryJson;
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
    public List<CountryJson> allCountries() {
        return countryRepository.findAll()
                .stream()
                .map(countryEntity -> new CountryJson(countryEntity.getName(), countryEntity.getCode()))
                .toList();
    }

    @Override
    @Nullable
    public CountryJson countryByCode(@Nonnull String code) {
        final CountryEntity found = countryRepository.findByCode(code);
        if(found != null) {
            return CountryJson.fromEntity(found);
        } else {
            throw new CountryNotFoundException(code);
        }
    }

    @Transactional
    @Override
    public @Nonnull CountryJson createCountry(@Nonnull CountryJson countryJson) {
        return CountryJson.fromEntity(
                countryRepository.save(CountryEntity.fromCountry(countryJson))
        );
    }

    @Transactional
    @Override
    public @Nonnull CountryJson updateCountryByCode(String code, CountryJson countryJson) {
        final CountryEntity foundCountry = countryRepository.findByCode(code);
        if (foundCountry != null) {
            final CountryEntity countryEntity = CountryEntity.fromCountry(countryJson);
            countryEntity.setId(foundCountry.getId());
            return CountryJson.fromEntity(
                    countryRepository.save(countryEntity)
            );
        }
        throw new CountryNotFoundException(code);
    }

    @Transactional
    @Override
    public void deleteCountryByCode(@Nonnull String code) {
        countryRepository.removeCountryEntityByCode(code);
    }
}
