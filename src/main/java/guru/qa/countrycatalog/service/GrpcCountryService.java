package guru.qa.countrycatalog.service;

import com.google.protobuf.Empty;
import guru.qa.countrycatalog.domain.CountryGql;
import guru.qa.countrycatalog.domain.CountryInputGql;
import guru.qa.grpc.countrycatalog.AllCountriesResponse;
import guru.qa.grpc.countrycatalog.CodeRequest;
import guru.qa.grpc.countrycatalog.CountryRequest;
import guru.qa.grpc.countrycatalog.CountryRequestWithCode;
import guru.qa.grpc.countrycatalog.CountryResponse;
import guru.qa.grpc.countrycatalog.CountrycatalogServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
public class GrpcCountryService extends CountrycatalogServiceGrpc.CountrycatalogServiceImplBase {

    private final CountryService countryService;

    public GrpcCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    @Override
    public void country(CodeRequest request, StreamObserver<CountryResponse> responseObserver) {
        final CountryGql countryGql = countryService.countryGqlByCode(request.getCode());
        responseObserver.onNext(
                CountryResponse.newBuilder()
                        .setId(countryGql.id().toString())
                        .setCode(countryGql.code())
                        .setName(countryGql.name())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void allCountries(Empty request, StreamObserver<AllCountriesResponse> responseObserver) {
        final List<CountryGql> countryGqlList = countryService.allGqlCountries();
        final List<CountryResponse> countryResponseList = countryGqlList.stream().map(countryGql ->
                CountryResponse.newBuilder()
                        .setId(countryGql.id().toString())
                        .setCode(countryGql.code())
                        .setName(countryGql.name())
                        .build()
        ).toList();
        responseObserver.onNext(
                AllCountriesResponse.newBuilder()
                        .addAllAllCountries(countryResponseList)
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void addCountry(CountryRequest request, StreamObserver<CountryResponse> responseObserver) {
        final CountryGql country = countryService.createGqlCountry(new CountryInputGql(request.getName(), request.getCode()));
        responseObserver.onNext(
                CountryResponse.newBuilder()
                        .setId(country.id().toString())
                        .setCode(country.code())
                        .setName(country.name())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void updateCountry(CountryRequestWithCode request, StreamObserver<CountryResponse> responseObserver) {
        CountryGql country = countryService.updateCountryGqlByCode(request.getCurrentCode(), new CountryInputGql(request.getName(), request.getCode()));
        responseObserver.onNext(
                CountryResponse.newBuilder()
                        .setId(country.id().toString())
                        .setCode(country.code())
                        .setName(country.name())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void deleteCountry(CodeRequest request, StreamObserver<Empty> responseObserver) {
        countryService.deleteCountryByCode(request.getCode());
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
