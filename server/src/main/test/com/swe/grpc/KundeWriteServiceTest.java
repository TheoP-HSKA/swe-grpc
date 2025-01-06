package com.swe.grpc.services;

import com.swe.grpc.entity.Kunde;
import com.swe.grpc.repository.KundeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class KundeWriteServiceTest {

    @Mock
    private KundeRepository kundeRepository;

    @InjectMocks
    private KundeWriteService kundeWriteService;

    private Kunde testKunde;
    private UUID testKundeId;

    @BeforeEach
    void setUp() {
        // Initialize the test data
        testKundeId = UUID.randomUUID();
        testKunde = new Kunde(testKundeId, "Müller", "mueller@example.com", "Kunde", true, null, null, null, null, null, null, null);
    }

    @Test
    void testCreateKunde() {
        // Given
        when(kundeRepository.save(testKunde)).thenReturn(testKunde);

        // When
        Kunde createdKunde = kundeWriteService.createKunde(testKunde);

        // Then
        assertThat(createdKunde).isNotNull();
        assertThat(createdKunde.getId()).isEqualTo(testKundeId);
        assertThat(createdKunde.getNachname()).isEqualTo("Müller");
        verify(kundeRepository, times(1)).save(testKunde);
    }

    @Test
    void testDeleteKunde_Success() {
        // Given
        when(kundeRepository.existsById(testKundeId)).thenReturn(true);

        // When
        kundeWriteService.deleteKunde(testKundeId);

        // Then
        verify(kundeRepository, times(1)).deleteById(testKundeId);
    }

    @Test
    void testDeleteKunde_KundeNotFound() {
        // Given
        when(kundeRepository.existsById(testKundeId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> kundeWriteService.deleteKunde(testKundeId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Kunde with ID " + testKundeId + " does not exist.");

        verify(kundeRepository, times(0)).deleteById(testKundeId);
    }
}
