package guru.qa.countrycatalog.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {

    void removeByCode(String code);

    CountryEntity findByCode(String code);
}
