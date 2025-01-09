package com.swe.grpc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.protobuf.Empty;
import com.swe.grpc.entity.Kunde;
import com.swe.grpc.services.KundeMapperService;
import com.swe.grpc.services.KundeReadService;
import com.swe.grpc.services.KundeReadServiceGrpcImpl;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

@ExtendWith(MockitoExtension.class)
class KundeReadServiceGrpcImplTest {

    @Mock
    private KundeReadService kundeReadService;

    @Mock
    private KundeMapperService kundeMapperService;

    @InjectMocks
    private KundeReadServiceGrpcImpl kundeReadServiceGrpcImpl;

    private StreamObserver<KundeProto.Kunde> responseObserver;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        responseObserver = mock(StreamObserver.class);
    }

    @Test
    @SuppressWarnings("unused")
    void testFindAll() {
        // Given
        Kunde kunde = new Kunde(UUID.randomUUID(), "Müller", "mueller@example.com", 1, true, null, null, null, null, null, null, null);
        when(kundeReadService.findAll()).thenReturn(List.of(kunde));
        KundeProto.Kunde protoKunde = KundeProto.Kunde.newBuilder().setId(kunde.id().toString()).build();
        when(kundeMapperService.toProto(kunde)).thenReturn(protoKunde);

        // When
        kundeReadServiceGrpcImpl.findAll(Empty.getDefaultInstance(), responseObserver);

        // Then
        verify(responseObserver, times(1)).onNext(protoKunde);
        verify(responseObserver, times(1)).onCompleted();
    }

    @Test
    @SuppressWarnings("unused")
    void testFindById_KundeFound() {
        // Given
        UUID kundeId = UUID.randomUUID();
        Kunde kunde = new Kunde(kundeId, "Müller", "mueller@example.com", 2, true, null, null, null, null, null, null, null);
        KundeProto.Kunde protoKunde = KundeProto.Kunde.newBuilder().setId(kunde.id().toString()).build();
        when(kundeReadService.findById(kundeId)).thenReturn(Optional.of(kunde));
        when(kundeMapperService.toProto(kunde)).thenReturn(protoKunde);

        // When
        KundeProto.KundeByIdRequest request = KundeProto.KundeByIdRequest.newBuilder().setId(kundeId.toString()).build();
        kundeReadServiceGrpcImpl.findById(request, responseObserver);

        // Then
        verify(responseObserver, times(1)).onNext(protoKunde);
        verify(responseObserver, times(1)).onCompleted();
    }

    @Test
    @SuppressWarnings("unused")
    void testFindById_KundeNotFound() {
        // Given
        UUID kundeId = UUID.randomUUID();
        when(kundeReadService.findById(kundeId)).thenReturn(Optional.empty());

        // When
        KundeProto.KundeByIdRequest request = KundeProto.KundeByIdRequest.newBuilder().setId(kundeId.toString()).build();
        kundeReadServiceGrpcImpl.findById(request, responseObserver);

        // Then
        verify(responseObserver, times(1)).onError(
                Status.NOT_FOUND
                        .withDescription("Kunde mit ID " + kundeId.toString() + " nicht gefunden.")
                        .asRuntimeException()
        );
    }
}
