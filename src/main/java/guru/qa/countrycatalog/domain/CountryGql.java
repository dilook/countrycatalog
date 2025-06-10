package guru.qa.countrycatalog.domain;

import guru.qa.countrycatalog.data.CountryEntity;
import jakarta.annotation.Nonnull;

import java.util.UUID;

public record CountryGql(UUID id, String name, String code) {

    public static @Nonnull CountryGql fromEntity(@Nonnull CountryEntity countryEntity) {
        return new CountryGql(countryEntity.getId(), countryEntity.getName(), countryEntity.getCode());
    }

}
