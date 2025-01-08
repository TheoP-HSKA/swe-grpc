package com.swe.grpc.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.UUID;
import java.util.stream.Collectors;

import com.swe.grpc.entity.*;

import io.grpc.stub.StreamObserver;

import com.swe.grpc.KundeProto;
import com.swe.grpc.KundeProto.CreateKundeRequest;

@Service
public class KundeMapperService {

        public KundeProto.Kunde toProto(Kunde kunde) {
                return KundeProto.Kunde.newBuilder()
                                .setId(kunde.id().toString()) // UUID to String
                                .setNachname(kunde.nachname())
                                .setEmail(kunde.email())
                                .setKategorie(kunde.kategorie())
                                .setHasNewsletter(kunde.hasNewsletter())
                                .setGeburtsdatum(
                                                com.google.protobuf.Timestamp.newBuilder()
                                                                .setSeconds(kunde.geburtsdatum()
                                                                                .atStartOfDay(ZoneOffset.UTC)
                                                                                .toEpochSecond())
                                                                .build()) // Convert LocalDate to protobuf Timestamp
                                .setHomepage(kunde.homepage().toString()) // URL to String
                                .setGeschlecht(KundeProto.GeschlechtType.valueOf(kunde.geschlecht().name())) // Enum
                                                                                                             // conversion
                                .setFamilienstand(KundeProto.FamilienstandType.valueOf(kunde.familienstand().name())) // Enum
                                                                                                                      // conversion
                                .setAdresse(
                                                KundeProto.Adresse.newBuilder()
                                                                .setPlz(kunde.adresse().getPlz())
                                                                .setOrt(kunde.adresse().getOrt())
                                                                .build()) // Map Adresse object
                                .addAllUmsaetze(
                                                kunde.umsaetze().stream()
                                                                .map(umsatz -> KundeProto.Umsatz.newBuilder()
                                                                                .setBetrag(umsatz.getBetrag()
                                                                                                .doubleValue())
                                                                                .setWaehrung(umsatz.getWaehrung()
                                                                                                .getCurrencyCode())
                                                                                .build())
                                                                .collect(Collectors.toList())) // Map Umsatz list
                                .addAllInteressen(
                                                kunde.interessen().stream()
                                                                .map(interesse -> KundeProto.InteresseType
                                                                                .valueOf(interesse.name()))
                                                                .collect(Collectors.toList())) // Map InteresseType list
                                .build();
        }

        public Kunde fromProto(KundeProto.Kunde protoKunde) {
                return new Kunde(
                                UUID.fromString(protoKunde.getId()), // Convert String to UUID
                                protoKunde.getNachname(),
                                protoKunde.getEmail(),
                                protoKunde.getKategorie(),
                                protoKunde.getHasNewsletter(),
                                LocalDate.ofEpochDay(protoKunde.getGeburtsdatum().getSeconds() / (24 * 60 * 60)), // Protobuf
                                                                                                                  // Timestamp
                                                                                                                  // to
                                                                                                                  // LocalDate
                                toURL(protoKunde.getHomepage()), // Convert String to URL
                                GeschlechtType.valueOf(protoKunde.getGeschlecht().name()),
                                FamilienstandType.valueOf(protoKunde.getFamilienstand().name()),
                                new Adresse(protoKunde.getAdresse().getPlz(), protoKunde.getAdresse().getOrt()), // Map
                                                                                                                 // Adresse
                                                                                                                 // object
                                protoKunde.getUmsaetzeList().stream()
                                                .map(umsatzProto -> new Umsatz(
                                                                BigDecimal.valueOf(umsatzProto.getBetrag()),
                                                                Currency.getInstance(umsatzProto.getWaehrung()))) // Map
                                                                                                                  // Umsatz
                                                                                                                  // with
                                                                                                                  // Currency
                                                .collect(Collectors.toList()), // Map Umsatz list
                                protoKunde.getInteressenList().stream()
                                                .map(interesseProto -> InteresseType.valueOf(interesseProto.name()))
                                                .collect(Collectors.toList())); // Map InteresseType list
        }

        public Kunde fromCreateRequest(CreateKundeRequest request) {
                KundeProto.Kunde protoKunde = createProto(request);
                return fromProto(protoKunde);
        }

        private KundeProto.Kunde createProto(CreateKundeRequest request) {
                // Map CreateKundeRequest to KundeProto.Kunde
                return KundeProto.Kunde.newBuilder()
                                .setId(UUID.randomUUID().toString())
                                .setNachname(request.getNachname())
                                .setEmail(request.getEmail())
                                .setKategorie(request.getKategorie())
                                .setHasNewsletter(request.getHasNewsletter())
                                .setGeburtsdatum(request.getGeburtsdatum())
                                .setHomepage(request.getHomepage())
                                .setGeschlecht(request.getGeschlecht())
                                .setFamilienstand(request.getFamilienstand())
                                .setAdresse(request.getAdresse())
                                .addAllUmsaetze(request.getUmsaetzeList())
                                .addAllInteressen(request.getInteressenList())
                                .build();
        }

        @SuppressWarnings("deprecation")
        private static URL toURL(String urlString) {
                try {
                        return new URL(urlString);
                } catch (Exception e) {
                        throw new IllegalArgumentException("Invalid URL: " + urlString);
                }
        }
}
