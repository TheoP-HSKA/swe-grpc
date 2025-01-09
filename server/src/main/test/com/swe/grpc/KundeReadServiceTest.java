package com.swe.grpc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.swe.grpc.entity.Kunde;
import com.swe.grpc.repository.KundeRepository;
import com.swe.grpc.services.KundeReadService;

@ExtendWith(MockitoExtension.class)
class KundeReadServiceTest {

    @Mock
    private KundeRepository kundeRepository;

    @InjectMocks
    private KundeReadService kundeReadService;

    private Kunde testKunde;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        // Initialize the test data
        testKunde = new Kunde(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Müller", "mueller@example.com", 1, true, null, null, null, null, null, null, null);
    }

    @Test
    @SuppressWarnings("unused")
    void testFindAll() {
        // Given
        when(kundeRepository.findAll()).thenReturn(List.of(testKunde));

        // When
        List<Kunde> kunden = kundeReadService.findAll();

        // Then
        assertThat(kunden).isNotEmpty();
        assertThat(kunden).contains(testKunde);
        verify(kundeRepository, times(1)).findAll();
    }

    @Test
    @SuppressWarnings("unused")
    void testFindById_KundeFound() {
        // Given
        when(kundeRepository.findById(UUID.fromString("00000000-0000-0000-0000-000000000001"))).thenReturn(Optional.of(testKunde));

        // When
        Optional<Kunde> result = kundeReadService.findById(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testKunde);
        verify(kundeRepository, times(1)).findById(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }

    @Test
    @SuppressWarnings("unused")
    void testFindById_KundeNotFound() {
        // Given
        when(kundeRepository.findById(UUID.fromString("00000000-0000-0000-0000-000000000001"))).thenReturn(Optional.empty());

        // When
        Optional<Kunde> result = kundeReadService.findById(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        // Then
        assertThat(result).isEmpty();
        verify(kundeRepository, times(1)).findById(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }

    @Test
    @SuppressWarnings("unused")
    void testFindByNachname() {
        // Given
        String nachname = "Müller";
        when(kundeRepository.findByNachname(nachname)).thenReturn(List.of(testKunde));

        // When
        List<Kunde> kunden = kundeReadService.findByNachname(nachname);

        // Then
        assertThat(kunden).isNotEmpty();
        assertThat(kunden).contains(testKunde);
        verify(kundeRepository, times(1)).findByNachname(nachname);
    }
}
