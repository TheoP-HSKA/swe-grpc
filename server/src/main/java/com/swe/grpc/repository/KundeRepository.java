package com.swe.grpc.repository;

import com.swe.grpc.entity.Adresse;
import com.swe.grpc.entity.FamilienstandType;
import com.swe.grpc.entity.GeschlechtType;
import com.swe.grpc.entity.InteresseType;
import com.swe.grpc.entity.Kunde;
import com.swe.grpc.entity.Umsatz;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

@Repository
public class KundeRepository {

    private final List<Kunde> kunden = new ArrayList<>(List.of(
            new Kunde(
                    UUID.fromString("00000000-0000-0000-0000-000000000001"),
                    "Mustermann",
                    "max.mustermann@web.de",
                    3,
                    false,
                    LocalDate.of(1990, 1, 1),
                    toURL("https://www.mustermann.de"),
                    GeschlechtType.MAENNLICH,
                    FamilienstandType.LEDIG,
                    new Adresse("76131", "Karlsruhe"),
                    List.of(new Umsatz(BigDecimal.valueOf(200), Currency.getInstance("EUR"))),
                    List.of(InteresseType.SPORT)),
            new Kunde(
                    UUID.fromString("00000000-0000-0000-0000-000000000002"),
                    "Musterfrau",
                    "erika.musterfrau@web.de",
                    2,
                    false,
                    LocalDate.of(1991, 2, 2),
                    toURL("https://www.musterfrau.de"),
                    GeschlechtType.WEIBLICH,
                    FamilienstandType.VERHEIRATET,
                    new Adresse("76131", "Karlsruhe"),
                    List.of(new Umsatz(BigDecimal.valueOf(300), Currency.getInstance("EUR"))),
                    List.of(InteresseType.REISEN))));

    // Method to fetch all customers
    public List<Kunde> findAll() {
        return Collections.unmodifiableList(kunden);
    }

    // Method to fetch a customer by ID
    public Optional<Kunde> findById(final UUID id) {
        return kunden.stream()
                .filter(kunde -> kunde.id().equals(id))
                .findFirst();
    }

    // Method to fetch customers by last name
    public List<Kunde> findByNachname(final String nachname) {
        return kunden.stream()
                .filter(kunde -> kunde.nachname().equalsIgnoreCase(nachname))
                .toList();
    }

    // Method to create a new customer
    public Kunde save(Kunde kunde) {
        kunden.add(kunde);
        return kunde;
    }

    // Method to delete a customer by ID
    public boolean deleteById(UUID id) {
        return kunden.removeIf(kunde -> kunde.id().equals(id));
    }

    // Helper method to convert String to URL
    @SuppressWarnings("deprecation")
    private static URL toURL(String urlString) {
        try {
            return new URL(urlString);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL: " + urlString);
        }
    }

    public boolean existsById(UUID id) {
        return kunden.stream().anyMatch(kunde -> kunde.id().equals(id));
    }
}
