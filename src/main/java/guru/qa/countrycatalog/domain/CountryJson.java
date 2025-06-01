package guru.qa.countrycatalog.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.countrycatalog.data.CountryEntity;
import jakarta.annotation.Nonnull;

public record CountryJson(
        @JsonProperty("name")
        String name,
        @JsonProperty("code")
        String code) {

    public static @Nonnull CountryJson fromEntity(@Nonnull CountryEntity countryEntity) {
        return new CountryJson(countryEntity.getName(), countryEntity.getCode());
    }
}
