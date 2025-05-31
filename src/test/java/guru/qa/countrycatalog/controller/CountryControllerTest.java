package guru.qa.countrycatalog.controller;

import guru.qa.countrycatalog.domain.Country;
import guru.qa.countrycatalog.exception.CountryNotFoundException;
import guru.qa.countrycatalog.service.CountryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CountryController.class)
@Import(CountryControllerTest.TestConfig.class)
public class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CountryService countryService;

    @Configuration
    static class TestConfig {
        @Bean
        public CountryService countryService() {
            return Mockito.mock(CountryService.class);
        }
    }

    @Test
    public void updateCountry_WhenCountryNotFound_ShouldReturn404() throws Exception {
        // Arrange
        String nonExistentCode = "NONEXISTENT";
        when(countryService.updateCountryByCode(eq(nonExistentCode), any(Country.class)))
                .thenThrow(new CountryNotFoundException("Country not found"));

        // Act & Assert
        mockMvc.perform(patch("/api/country/update/{code}", nonExistentCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Country\",\"code\":\"TEST\"}"))
                .andExpect(status().isNotFound());
    }
}
