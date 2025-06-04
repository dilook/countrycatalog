package guru.qa.countrycatalog.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {

    long removeByCode(String code);

    CountryEntity findByCode(String code);
//
//    default boolean removeByCode(String code) {
//        if (findByCode(code) != null) {
//            removeByCode(code);
//            return true;
//        }
//        return false;
//    }

}
