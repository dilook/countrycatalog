package guru.qa.countrycatalog.data;

import guru.qa.countrycatalog.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {

    Country findByCode(String code);

    void removeCountryEntityByCode(String code);

    CountryEntity findCountryEntityByCode(String code);
}
