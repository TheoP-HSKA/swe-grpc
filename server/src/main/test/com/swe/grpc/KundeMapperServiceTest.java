package com.swe.grpc.services;

import com.swe.grpc.entity.Kunde;
import com.swe.grpc.KundeProto;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
@Tag("service-mapper")
@DisplayName("KundeMapperService Tests")
@ExtendWith(SoftAssertionsExtension.class)
class KundeMapperServiceTest {

    private final KundeMapperService service = new KundeMapperService();

    @InjectSoftAssertions
    private SoftAssertions softly;

    private static final UUID TEST_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final String TEST_NACHNAME = "Müller";
    private static final String TEST_EMAIL = "mueller@example.com";
    private static final LocalDate TEST_GEBURTSDATUM = LocalDate.of(1990, 1, 1);
    private static final URL TEST_HOMEPAGE = new URL("http://www.mueller.de");
    
    private Kunde createTestKunde() {
        return new Kunde(
                TEST_ID,
                TEST_NACHNAME,
                TEST_EMAIL,
                "Kunde",
                true,
                TEST_GEBURTSDATUM,
                TEST_HOMEPAGE,
                Kunde.GeschlechtType.MALE,
                Kunde.FamilienstandType.SINGLE,
                new Kunde.Adresse("Musterstraße", "12", "12345", "Musterstadt"),
                List.of(new Kunde.Umsatz(100.0, LocalDate.now())),
                List.of(Kunde.InteresseType.SPORT)
        );
    }

    @Test
    @DisplayName("Test mapping Kunde to ProtoKunde")
    void testToProto() {
        // Given
        Kunde kunde = createTestKunde();

        // When
        KundeProto.Kunde protoKunde = service.toProto(kunde);

        // Then
        softly.assertThat(protoKunde).isNotNull();
        softly.assertThat(protoKunde.getId()).isEqualTo(kunde.id().toString());
        softly.assertThat(protoKunde.getNachname()).isEqualTo(kunde.nachname());
        softly.assertThat(protoKunde.getEmail()).isEqualTo(kunde.email());
        softly.assertThat(protoKunde.getGeburtsdatum().getSeconds())
                .isEqualTo(kunde.geburtsdatum().atStartOfDay().toEpochSecond(java.time.ZoneOffset.UTC));
        softly.assertThat(protoKunde.getHomepage()).isEqualTo(kunde.homepage().toString());
        softly.assertThat(protoKunde.getGeschlecht()).isEqualTo(KundeProto.GeschlechtType.MALE);
        softly.assertThat(protoKunde.getFamilienstand()).isEqualTo(KundeProto.FamilienstandType.SINGLE);
        softly.assertThat(protoKunde.getUmsaetzeCount()).isEqualTo(kunde.umsaetze().size());
        softly.assertThat(protoKunde.getInteressenCount()).isEqualTo(kunde.interessen().size());
    }

    @Test
    @DisplayName("Test mapping ProtoKunde to Kunde")
    void testFromProto() {
        // Given
        KundeProto.Kunde protoKunde = KundeProto.Kunde.newBuilder()
                .setId(TEST_ID.toString())
                .setNachname(TEST_NACHNAME)
                .setEmail(TEST_EMAIL)
                .setGeburtsdatum(com.google.protobuf.Timestamp.newBuilder()
                        .setSeconds(TEST_GEBURTSDATUM.atStartOfDay().toEpochSecond(java.time.ZoneOffset.UTC))
                        .build())
                .setHomepage(TEST_HOMEPAGE.toString())
                .setGeschlecht(KundeProto.GeschlechtType.MALE)
                .setFamilienstand(KundeProto.FamilienstandType.SINGLE)
                .addUmsaetze(KundeProto.Umsatz.newBuilder().setBetrag(100.0).setDatum(
                        com.google.protobuf.Timestamp.newBuilder()
                                .setSeconds(LocalDate.now().atStartOfDay(java.time.ZoneOffset.UTC).toEpochSecond())
                                .build()
                ).build())
                .addInteressen(KundeProto.InteresseType.SPORT)
                .build();

        // When
        Kunde kunde = service.fromProto(protoKunde);

        // Then
        softly.assertThat(kunde).isNotNull();
        softly.assertThat(kunde.id()).isEqualTo(TEST_ID);
        softly.assertThat(kunde.nachname()).isEqualTo(TEST_NACHNAME);
        softly.assertThat(kunde.email()).isEqualTo(TEST_EMAIL);
        softly.assertThat(kunde.geburtsdatum()).isEqualTo(TEST_GEBURTSDATUM);
        softly.assertThat(kunde.homepage()).isEqualTo(TEST_HOMEPAGE);
        softly.assertThat(kunde.geschlecht()).isEqualTo(Kunde.GeschlechtType.MALE);
        softly.assertThat(kunde.familienstand()).isEqualTo(Kunde.FamilienstandType.SINGLE);
        softly.assertThat(kunde.umsaetze()).hasSize(1);
        softly.assertThat(kunde.interessen()).hasSize(1);
    }
}
