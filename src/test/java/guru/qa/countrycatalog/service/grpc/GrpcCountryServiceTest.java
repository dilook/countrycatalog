package guru.qa.countrycatalog.service.grpc;

import com.google.protobuf.Empty;
import guru.qa.grpc.countrycatalog.AllCountriesResponse;
import guru.qa.grpc.countrycatalog.CodeRequest;
import guru.qa.grpc.countrycatalog.CountryCount;
import guru.qa.grpc.countrycatalog.CountryRequest;
import guru.qa.grpc.countrycatalog.CountryRequestWithCode;
import guru.qa.grpc.countrycatalog.CountryResponse;
import guru.qa.grpc.countrycatalog.CountrycatalogServiceGrpc;
import io.grpc.Channel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

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
        public Channel countrycatalogChannel(GrpcChannelFactory grpcChannelFactory) {
            return grpcChannelFactory.createChannel("countrycatalog");
        }

        @Bean
        public CountrycatalogServiceGrpc.CountrycatalogServiceBlockingStub countrycatalogServiceBlockingStub(Channel countrycatalogChannel) {
            return CountrycatalogServiceGrpc.newBlockingStub(countrycatalogChannel);
        }

        @Bean
        public CountrycatalogServiceGrpc.CountrycatalogServiceStub countrycatalogServiceStub(Channel countrycatalogChannel) {
            return CountrycatalogServiceGrpc.newStub(countrycatalogChannel);
        }
    }

    @Autowired
    private CountrycatalogServiceGrpc.CountrycatalogServiceBlockingStub countrycatalogServiceBlockingStub;

    @Autowired
    private CountrycatalogServiceGrpc.CountrycatalogServiceStub countrycatalogServiceStub;

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

    @Test
    @Sql("/cleanup.sql")
    public void addCountryStream_ShouldCreateMultipleCountriesAndReturnCount() throws Exception {
        final CountryRequest[] countries = {
                CountryRequest.newBuilder().setName("United States").setCode("US").build(),
                CountryRequest.newBuilder().setName("Canada").setCode("CA").build(),
                CountryRequest.newBuilder().setName("Mexico").setCode("MX").build()
        };
        final int expectedCount = countries.length;

        CountryCount countResponse = prepareAndSendRequest(countries);
        assertNotNull(countResponse, "Response should not be null");
        assertEquals(expectedCount, countResponse.getCount(), "Should create expected number of countries");

        for (CountryRequest country : countries) {
            CodeRequest codeRequest = CodeRequest.newBuilder().setCode(country.getCode()).build();
            CountryResponse countryResponse = countrycatalogServiceBlockingStub.country(codeRequest);

            assertNotNull(countryResponse, "Country response should not be null");
            assertEquals(country.getName(), countryResponse.getName(), "Country name should match");
            assertEquals(country.getCode(), countryResponse.getCode(), "Country code should match");
        }
    }

    private CountryCount prepareAndSendRequest(CountryRequest[] countries) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<CountryCount> response = new AtomicReference<>();
        final AtomicReference<Throwable> error = new AtomicReference<>();

        StreamObserver<CountryRequest> requestStream = getStreamObserver(response, error, latch);

        for (CountryRequest country : countries) {
            requestStream.onNext(country);
        }
        requestStream.onCompleted();

        if (!latch.await(5, TimeUnit.SECONDS) || error.get() != null) {
            throw new Exception("Error in gRPC call", error.get());
        }

        return response.get();
    }

    private StreamObserver<CountryRequest> getStreamObserver(AtomicReference<CountryCount> response,
                                                             AtomicReference<Throwable> error,
                                                             CountDownLatch latch) {
        return countrycatalogServiceStub.addCountryStream(
                new StreamObserver<>() {
                    @Override
                    public void onNext(CountryCount value) {
                        response.set(value);
                    }

                    @Override
                    public void onError(Throwable t) {
                        error.set(t);
                        latch.countDown();
                    }

                    @Override
                    public void onCompleted() {
                        latch.countDown();
                    }
                });
    }
}
