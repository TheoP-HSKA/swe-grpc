package com.swe.grpc.services;

import com.google.protobuf.Empty;
import com.swe.grpc.KundeProto;
import com.swe.grpc.KundeReadServiceGrpc;
import com.swe.grpc.entity.Kunde;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

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
    void setUp() {
        responseObserver = mock(StreamObserver.class);
    }

    @Test
    void testFindAll() {
        // Given
        Kunde kunde = new Kunde(UUID.randomUUID(), "Müller", "mueller@example.com", "Kunde", true, null, null, null, null, null, null, null);
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
    void testFindById_KundeFound() {
        // Given
        UUID kundeId = UUID.randomUUID();
        Kunde kunde = new Kunde(kundeId, "Müller", "mueller@example.com", "Kunde", true, null, null, null, null, null, null, null);
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
