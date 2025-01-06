package com.swe.grpc.services;

import com.google.protobuf.Empty;
import com.swe.grpc.KundeProto;
import com.swe.grpc.entity.Kunde;
import com.swe.grpc.mappers.KundeMapperService;
import com.swe.grpc.services.KundeWriteServiceGrpcImpl;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
@DisplayName("KundeWriteServiceGrpcImpl Test")
class KundeWriteServiceGrpcImplTest {

    private KundeWriteServiceGrpcImpl grpcService;

    @Mock
    private KundeWriteService kundeWriteService;

    @Mock
    private KundeMapperService kundeMapperService;

    @Mock
    private StreamObserver<KundeProto.KundeResponse> responseObserver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        grpcService = new KundeWriteServiceGrpcImpl(kundeWriteService, kundeMapperService);
    }

    @Nested
    @DisplayName("createKunde Tests")
    class CreateKundeTests {

        @Test
        @DisplayName("Successfully create Kunde")
        void createKundeSuccess() {
            // Given
            KundeProto.CreateKundeRequest request = KundeProto.CreateKundeRequest.newBuilder()
                    .setName("John Doe")
                    .build();
            Kunde kunde = new Kunde(UUID.randomUUID(), "John Doe");

            when(kundeMapperService.fromCreateRequest(any())).thenReturn(kunde);

            // When
            grpcService.createKunde(request, responseObserver);

            // Then
            verify(kundeWriteService).createKunde(kunde);
            ArgumentCaptor<KundeProto.KundeResponse> captor = ArgumentCaptor.forClass(KundeProto.KundeResponse.class);
            verify(responseObserver).onNext(captor.capture());
            verify(responseObserver).onCompleted();

            assertThat(captor.getValue().getMessage())
                .contains("Kunde created successfully");
        }

        @Test
        @DisplayName("Error creating Kunde")
        void createKundeError() {
            // Given
            KundeProto.CreateKundeRequest request = KundeProto.CreateKundeRequest.newBuilder()
                    .setName("John Doe")
                    .build();

            when(kundeMapperService.fromCreateRequest(any()))
                    .thenThrow(new RuntimeException("Mapping error"));

            // When
            grpcService.createKunde(request, responseObserver);

            // Then
            verify(responseObserver).onError(any(StatusRuntimeException.class));
        }
    }

    @Nested
    @DisplayName("deleteKunde Tests")
    class DeleteKundeTests {

        @Test
        @DisplayName("Successfully delete Kunde")
        void deleteKundeSuccess() {
            // Given
            String kundeId = UUID.randomUUID().toString();
            KundeProto.KundeByIdRequest request = KundeProto.KundeByIdRequest.newBuilder()
                    .setId(kundeId)
                    .build();

            // When
            grpcService.deleteKunde(request, responseObserver);

            // Then
            verify(kundeWriteService).deleteKunde(UUID.fromString(kundeId));
            ArgumentCaptor<KundeProto.KundeResponse> captor = ArgumentCaptor.forClass(KundeProto.KundeResponse.class);
            verify(responseObserver).onNext(captor.capture());
            verify(responseObserver).onCompleted();

            assertThat(captor.getValue().getMessage())
                .contains("Kunde deleted successfully");
        }

        @Test
        @DisplayName("Kunde not found during deletion")
        void deleteKundeNotFound() {
            // Given
            String invalidId = "invalid-uuid";
            KundeProto.KundeByIdRequest request = KundeProto.KundeByIdRequest.newBuilder()
                    .setId(invalidId)
                    .build();

            // When
            grpcService.deleteKunde(request, responseObserver);

            // Then
            verify(responseObserver).onError(any(StatusRuntimeException.class));
        }

        @Test
        @DisplayName("Error deleting Kunde")
        void deleteKundeError() {
            // Given
            String kundeId = UUID.randomUUID().toString();
            KundeProto.KundeByIdRequest request = KundeProto.KundeByIdRequest.newBuilder()
                    .setId(kundeId)
                    .build();

            doThrow(new RuntimeException("Delete error")).when(kundeWriteService).deleteKunde(any());

            // When
            grpcService.deleteKunde(request, responseObserver);

            // Then
            verify(responseObserver).onError(any(StatusRuntimeException.class));
        }
    }
}
