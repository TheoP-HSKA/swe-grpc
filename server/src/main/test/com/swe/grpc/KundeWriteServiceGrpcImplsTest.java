package com.swe.grpc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.swe.grpc.KundeProto.CreateKundeRequest;
import com.swe.grpc.entity.Kunde;
import com.swe.grpc.services.KundeMapperService;
import com.swe.grpc.services.KundeWriteService;
import com.swe.grpc.services.KundeWriteServiceGrpcImpl;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

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
    @SuppressWarnings("unused")
    void setUp() {
        MockitoAnnotations.initMocks(this);
        grpcService = new KundeWriteServiceGrpcImpl(kundeWriteService, kundeMapperService);
    }

    @Nested
    @DisplayName("createKunde Tests")
    @SuppressWarnings("unused")
    class CreateKundeTests {

        @Test
        @DisplayName("Successfully create Kunde")
        @SuppressWarnings("unused")
        void createKundeSuccess() {
            // Given
            CreateKundeRequest request = CreateKundeRequest.newBuilder()
                    .setNachname("John Smith")
                    .build();
            Kunde kunde = new Kunde(UUID.randomUUID(), "John Smith", "mueller@example.com", 2, true, null, null, null, null, null, null, null);

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
        @SuppressWarnings("unused")
        void createKundeError() {
            // Given
            KundeProto.CreateKundeRequest request = KundeProto.CreateKundeRequest.newBuilder()
                    .setNachname("John Doe")
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
    @SuppressWarnings("unused")
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
