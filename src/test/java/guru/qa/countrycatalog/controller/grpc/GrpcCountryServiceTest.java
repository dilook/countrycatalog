package guru.qa.countrycatalog.controller.grpc;

import com.google.protobuf.Empty;
import guru.qa.grpc.countrycatalog.AllCountriesResponse;
import guru.qa.grpc.countrycatalog.CodeRequest;
import guru.qa.grpc.countrycatalog.CountryRequest;
import guru.qa.grpc.countrycatalog.CountryRequestWithCode;
import guru.qa.grpc.countrycatalog.CountryResponse;
import guru.qa.grpc.countrycatalog.CountrycatalogServiceGrpc;
import io.grpc.Channel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Import(GrpcCountryServiceTest.TestConfig.class)
public class GrpcCountryServiceTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CountrycatalogServiceGrpc.CountrycatalogServiceBlockingStub countrycatalogServiceBlockingStub(
                GrpcChannelFactory grpcChannelFactory) {
            Channel channel = grpcChannelFactory.createChannel("countrycatalog");
            return CountrycatalogServiceGrpc.newBlockingStub(channel);
        }
    }

    @Autowired
    private CountrycatalogServiceGrpc.CountrycatalogServiceBlockingStub countrycatalogServiceBlockingStub;

    @Test
    @Sql(scripts = {"/cleanup.sql", "/getAllCountries_ShouldReturnAllCountries.sql"})
    public void allCountries_ShouldReturnAllCountries() {
        AllCountriesResponse response = countrycatalogServiceBlockingStub.allCountries(Empty.getDefaultInstance());

        assertNotNull(response);
        assertEquals(10, response.getAllCountriesCount());
    }

    @Test
    @Sql(scripts = {"/cleanup.sql", "/germanyCountry.sql"})
    public void country_WhenCountryExists_ShouldReturnCountry() {
        String countryCode = "DE";
        String countryName = "Germany";
        CodeRequest request = CodeRequest.newBuilder().setCode(countryCode).build();

        CountryResponse response = countrycatalogServiceBlockingStub.country(request);

        assertNotNull(response);
        assertEquals(countryName, response.getName());
        assertEquals(countryCode, response.getCode());
    }

    @Test
    public void country_WhenCountryNotFound_ShouldThrowException() {
        String nonExistentCode = "NONEXISTENT";
        CodeRequest request = CodeRequest.newBuilder().setCode(nonExistentCode).build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> countrycatalogServiceBlockingStub.country(request));
        assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.UNKNOWN);
    }

    @Test
    @Sql("/cleanup.sql")
    public void addCountry_ShouldCreateAndReturnCountry() {
        String countryCode = "US";
        String countryName = "United States";
        CountryRequest request = CountryRequest.newBuilder()
                .setName(countryName)
                .setCode(countryCode)
                .build();

        CountryResponse response = countrycatalogServiceBlockingStub.addCountry(request);

        assertNotNull(response);
        assertEquals(countryName, response.getName());
        assertEquals(countryCode, response.getCode());

        CodeRequest getRequest = CodeRequest.newBuilder().setCode(countryCode).build();
        CountryResponse getResponse = countrycatalogServiceBlockingStub.country(getRequest);
        assertEquals(countryName, getResponse.getName());
        assertEquals(countryCode, getResponse.getCode());
    }

    @Test
    @Sql(scripts = {"/cleanup.sql", "/germanyCountry.sql"})
    public void updateCountry_WhenCountryExists_ShouldUpdateAndReturnCountry() {
        String originalCode = "DE";
        String updatedName = "Germany Republic";
        CountryRequestWithCode request = CountryRequestWithCode.newBuilder()
                .setCurrentCode(originalCode)
                .setName(updatedName)
                .setCode(originalCode)
                .build();

        CountryResponse response = countrycatalogServiceBlockingStub.updateCountry(request);

        assertNotNull(response);
        assertEquals(updatedName, response.getName());
        assertEquals(originalCode, response.getCode());

        CodeRequest getRequest = CodeRequest.newBuilder().setCode(originalCode).build();
        CountryResponse getResponse = countrycatalogServiceBlockingStub.country(getRequest);
        assertEquals(updatedName, getResponse.getName());
        assertEquals(originalCode, getResponse.getCode());
    }

    @Test
    public void updateCountry_WhenCountryNotFound_ShouldThrowException() {
        String nonExistentCode = "NONEXISTENT";
        CountryRequestWithCode request = CountryRequestWithCode.newBuilder()
                .setCurrentCode(nonExistentCode)
                .setName("Test Country")
                .setCode(nonExistentCode)
                .build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> countrycatalogServiceBlockingStub.updateCountry(request));
        assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.UNKNOWN);
    }

    @Test
    @Sql(scripts = {"/cleanup.sql", "/germanyCountry.sql"})
    public void deleteCountry_ShouldDeleteCountry() {
        String countryCode = "DE";
        CodeRequest request = CodeRequest.newBuilder().setCode(countryCode).build();

        countrycatalogServiceBlockingStub.deleteCountry(request);

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> countrycatalogServiceBlockingStub.country(request));
        assertThat(exception.getStatus().getCode()).isEqualTo(Status.Code.UNKNOWN);
    }
}
