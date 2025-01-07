package com.swe.grpc.services;

import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.stream.Collectors;

import com.swe.grpc.entity.Adresse;
import com.swe.grpc.entity.Kunde;

import com.swe.grpc.entity.FamilienstandType;
import com.swe.grpc.entity.GeschlechtType;
import com.swe.grpc.entity.InteresseType;

import com.swe.grpc.KundeProto;

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
                                .setSeconds(kunde.geburtsdatum().atStartOfDay(ZoneOffset.UTC).toEpochSecond())
                                .build()) // Convert LocalDate to protobuf Timestamp
                .setHomepage(kunde.homepage().toString()) // URL to String
                .setGeschlecht(KundeProto.GeschlechtType.valueOf(kunde.geschlecht().name())) // Enum conversion
                .setFamilienstand(KundeProto.FamilienstandType.valueOf(kunde.familienstand().name())) // Enum conversion
                .setAdresse(
                        KundeProto.Adresse.newBuilder()
                                .setPlz(kunde.adresse().getPlz())
                                .setOrt(kunde.adresse().getOrt())
                                .build()) // Map Adresse object
                .addAllUmsaetze(
                        kunde.umsaetze().stream()
                                .map(umsatz -> KundeProto.Umsatz.newBuilder()
                                        .setBetrag(umsatz.getBetrag())
                                        .setDatum(
                                                com.google.protobuf.Timestamp.newBuilder()
                                                        .setSeconds(umsatz.datum().toEpochSecond(ZoneOffset.UTC))
                                                        .build())
                                        .build())
                                .collect(Collectors.toList())) // Map Umsatz list
                .addAllInteressen(
                        kunde.interessen().stream()
                                .map(interesse -> KundeProto.InteresseType.valueOf(interesse.name()))
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
                LocalDate.ofEpochDay(protoKunde.getGeburtsdatum().getSeconds() / (24 * 60 * 60)), // Convert protobuf
                                                                                                  // Timestamp to
                                                                                                  // LocalDate
                new URL(protoKunde.getHomepage()), // Convert String to URL
                GeschlechtType.valueOf(protoKunde.getGeschlecht().name()), // Enum conversion
                FamilienstandType.valueOf(protoKunde.getFamilienstand().name()), // Enum conversion
                new Adresse(
                        protoKunde.getAdresse().getPlz(),
                        protoKunde.getAdresse().getOrt()), // Map Adresse object
                protoKunde.getUmsaetzeList().stream()
                        .map(umsatzProto -> new Umsatz(
                                umsatzProto.getBetrag(),
                                Instant.ofEpochSecond(umsatzProto.getDatum().getSeconds()).atZone(ZoneOffset.UTC)
                                        .toLocalDate()))
                        .collect(Collectors.toList()), // Map Umsatz list
                protoKunde.getInteressenList().stream()
                        .map(interesseProto -> InteresseType.valueOf(interesseProto.name()))
                        .collect(Collectors.toList()) // Map InteresseType list
        );
    }
}
