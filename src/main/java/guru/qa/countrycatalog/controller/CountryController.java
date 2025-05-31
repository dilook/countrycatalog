package guru.qa.countrycatalog.controller;

import guru.qa.countrycatalog.domain.Country;
import guru.qa.countrycatalog.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("api/country")
public class CountryController {

    private final CountryService countryService;

    @Autowired
    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("/all")
    public List<Country> all() {
        return countryService.allCountries();
    }

    @GetMapping
    public Country getByCode(@RequestParam String code) {
        return countryService.countryByCode(code);
    }

    @PostMapping("/create")
    public Country create(@RequestBody Country country) {
        return countryService.createCountry(country);
    }

    @PatchMapping("/update/{code}")
    public Country update(@PathVariable String code, @RequestBody Country country) {
        return countryService.updateCountryByCode(code, country);
    }

    @DeleteMapping("/delete/{code}")
    public void delete(@PathVariable String code) {
        countryService.deleteCountryByCode(code);
    }
}
