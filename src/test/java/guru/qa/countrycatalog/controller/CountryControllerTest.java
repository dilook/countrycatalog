package guru.qa.countrycatalog.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql("/getAllCountries_ShouldReturnAllCountries.sql")
    public void getAllCountries_ShouldReturnAllCountries() throws Exception {
        mockMvc.perform(get("/api/country/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(10));
    }

    @Test
    @Sql("/germanyCountry.sql")
    public void getCountryByCode_WhenCountryExists_ShouldReturnCountry() throws Exception {
        String countryCode = "DE";
        String countryName = "Germany";

        // Act & Assert
        mockMvc.perform(get("/api/country")
                .param("code", countryCode))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(countryName)))
                .andExpect(jsonPath("$.code", is(countryCode)));
    }

    @Test
    public void getCountryByCode_WhenCountryNotFound_ShouldReturn404() throws Exception {
        String nonExistentCode = "NONEXISTENT";

        // Act & Assert
        mockMvc.perform(get("/api/country")
                .param("code", nonExistentCode))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createCountry_ShouldCreateAndReturnCountry() throws Exception {
        String countryCode = "US";
        String countryName = "United States";

        // Act & Assert
        mockMvc.perform(post("/api/country/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"" + countryName + "\",\"code\":\"" + countryCode + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(countryName)))
                .andExpect(jsonPath("$.code", is(countryCode)));
    }

    @Test
    @Sql("/germanyCountry.sql")
    public void updateCountry_WhenCountryExists_ShouldUpdateAndReturnCountry() throws Exception {
        String originalCode = "DE";
        String updatedName = "Germany Republic";

        // Act & Assert
        mockMvc.perform(patch("/api/country/update/{code}", originalCode)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"" + updatedName + "\",\"code\":\"" + originalCode + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(updatedName)))
                .andExpect(jsonPath("$.code", is(originalCode)));
    }

    @Test
    public void updateCountry_WhenCountryNotFound_ShouldReturn404() throws Exception {
        String nonExistentCode = "NONEXISTENT";

        // Act & Assert
        mockMvc.perform(patch("/api/country/update/{code}", nonExistentCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Country\",\"code\":\"TEST\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Sql("/germanyCountry.sql")
    public void deleteCountry_ShouldDeleteCountry() throws Exception {
        // Act
        mockMvc.perform(delete("/api/country/delete/{code}", "DE"))
                .andExpect(status().isOk());

        // Assert - Verify the country no longer exists
        mockMvc.perform(get("/api/country")
                .param("code", "DE"))
                .andExpect(status().isNotFound());
    }
}
