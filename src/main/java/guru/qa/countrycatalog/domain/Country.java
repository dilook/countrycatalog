package guru.qa.countrycatalog.domain;

import guru.qa.countrycatalog.data.CountryEntity;

import java.util.UUID;

public record Country(String name, String code) {

    public static Country fromEntity(CountryEntity countryEntity) {
        return new Country(countryEntity.getName(), countryEntity.getCode());
    }
}
