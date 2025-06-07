package guru.qa.countrycatalog.controller.graphql;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class CountryGraphQLControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql("/getAllCountries_ShouldReturnAllCountries.sql")
    public void all_ShouldReturnAllCountries() throws Exception {
        String query = """
                {"query": "query { all { id name code } }"}
                """;

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.all", hasSize(10)));
    }

    @Test
    @Sql("/germanyCountry.sql")
    public void country_WhenCountryExists_ShouldReturnCountry() throws Exception {
        String countryCode = "DE";
        String countryName = "Germany";

        String query = """
                {"query": "query { country(code: \\"%s\\") { name code } }"}
                """.formatted(countryCode);

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.country.name", is(countryName)))
                .andExpect(jsonPath("$.data.country.code", is(countryCode)));
    }

    @Test
    public void country_WhenCountryNotFound_ShouldReturnNull() throws Exception {
        String nonExistentCode = "NONEXISTENT";

        String query = """
                {"query": "query { country(code: \\"%s\\") { name code } }"}
                """.formatted(nonExistentCode);

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.country", nullValue()));
    }

    @Test
    public void addCountry_ShouldCreateAndReturnCountry() throws Exception {
        String countryCode = "US";
        String countryName = "United States";

        String mutation = """
                {"query": "mutation { addCountry(input: {name: \\"%s\\", code: \\"%s\\"}) { name code } }"}
                """.formatted(countryName, countryCode);

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mutation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.addCountry.name", is(countryName)))
                .andExpect(jsonPath("$.data.addCountry.code", is(countryCode)));
    }

    @Test
    @Sql("/germanyCountry.sql")
    public void updateCountry_WhenCountryExists_ShouldUpdateAndReturnCountry() throws Exception {
        String originalCode = "DE";
        String updatedName = "Germany Republic";

        String mutation = """
                {"query": "mutation { updateCountry(code: \\"%s\\", input: {name: \\"%s\\", code: \\"%s\\"}) { name code } }"}
                """.formatted(originalCode, updatedName, originalCode);

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mutation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.updateCountry.name", is(updatedName)))
                .andExpect(jsonPath("$.data.updateCountry.code", is(originalCode)));
    }

    @Test
    public void updateCountry_WhenCountryNotFound_ShouldReturnNull() throws Exception {
        String nonExistentCode = "NONEXISTENT";
        String updatedName = "Test Country";

        String mutation = """
                {"query": "mutation { updateCountry(code: \\"%s\\", input: {name: \\"%s\\", code: \\"%s\\"}) { name code } }"}
                """.formatted(nonExistentCode, updatedName, nonExistentCode);

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mutation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.updateCountry", nullValue()));
    }

    @Test
    @Sql("/germanyCountry.sql")
    public void deleteCountry_ShouldDeleteCountry() throws Exception {
        String countryCode = "DE";

        // First, delete the country
        String deleteMutation = """
                {"query": "mutation { deleteCountry(code: \\"%s\\") { success } }"}
                """.formatted(countryCode);

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(deleteMutation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deleteCountry.success", is(true)));

        // Then verify the country no longer exists
        String query = """
                {"query": "query { country(code: \\"%s\\") { name code } }"}
                """.formatted(countryCode);

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.country", nullValue()));
    }
}
