package com.swe.grpc.services;

import com.swe.grpc.entity.Kunde;
import com.swe.grpc.repository.KundeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class KundeReadServiceTest {

    @Mock
    private KundeRepository kundeRepository;

    @InjectMocks
    private KundeReadService kundeReadService;

    private Kunde testKunde;

    @BeforeEach
    void setUp() {
        // Initialize the test data
        testKunde = new Kunde(1, "Müller", "mueller@example.com", "Kunde", true, null, null, null, null, null, null, null);
    }

    @Test
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
    void testFindById_KundeFound() {
        // Given
        when(kundeRepository.findById(1)).thenReturn(Optional.of(testKunde));

        // When
        Optional<Kunde> result = kundeReadService.findById(1);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testKunde);
        verify(kundeRepository, times(1)).findById(1);
    }

    @Test
    void testFindById_KundeNotFound() {
        // Given
        when(kundeRepository.findById(1)).thenReturn(Optional.empty());

        // When
        Optional<Kunde> result = kundeReadService.findById(1);

        // Then
        assertThat(result).isEmpty();
        verify(kundeRepository, times(1)).findById(1);
    }

    @Test
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
