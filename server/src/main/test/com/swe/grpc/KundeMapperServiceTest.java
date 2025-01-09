package com.swe.grpc;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.swe.grpc.KundeProto.InteresseType;
import com.swe.grpc.KundeProto.Umsatz;
import com.swe.grpc.entity.Adresse;
import com.swe.grpc.entity.FamilienstandType;
import com.swe.grpc.entity.GeschlechtType;
import com.swe.grpc.entity.Kunde;
import com.swe.grpc.services.KundeMapperService;

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
    
    @SuppressWarnings("deprecation")
private Kunde createTestKunde() throws MalformedURLException{
        return new Kunde(
                TEST_ID,
                TEST_NACHNAME,
                TEST_EMAIL,
                2,
                true,
                TEST_GEBURTSDATUM,
                new URL("http://www.mueller.de"),
                GeschlechtType.MAENNLICH,
                FamilienstandType.LEDIG,
                new Adresse("12345", "Musterstadt"),
                List.of(),
                List.of(com.swe.grpc.entity.InteresseType.SPORT)
        );
    }

    @Test
    @DisplayName("Test mapping Kunde to ProtoKunde")
    @SuppressWarnings("unused")
    void testToProto() throws MalformedURLException{
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
        softly.assertThat(protoKunde.getGeschlecht()).isEqualTo(KundeProto.GeschlechtType.MAENNLICH);
        softly.assertThat(protoKunde.getFamilienstand()).isEqualTo(KundeProto.FamilienstandType.LEDIG);
        softly.assertThat(protoKunde.getUmsaetzeCount()).isEqualTo(kunde.umsaetze().size());
        softly.assertThat(protoKunde.getInteressenCount()).isEqualTo(kunde.interessen().size());
    }

    @SuppressWarnings({"deprecation", "unused"})
@Test
    @DisplayName("Test mapping ProtoKunde to Kunde")
    void testFromProto() throws MalformedURLException{
        // Given
        KundeProto.Kunde protoKunde = KundeProto.Kunde.newBuilder()
                .setId(TEST_ID.toString())
                .setNachname(TEST_NACHNAME)
                .setEmail(TEST_EMAIL)
                .setGeburtsdatum(com.google.protobuf.Timestamp.newBuilder()
                        .setSeconds(TEST_GEBURTSDATUM.atStartOfDay().toEpochSecond(java.time.ZoneOffset.UTC))
                        .build())
                .setHomepage(new URL("http://www.mueller.de").toString())
                .setGeschlecht(KundeProto.GeschlechtType.MAENNLICH)
                .setFamilienstand(KundeProto.FamilienstandType.LEDIG)
                .addAllUmsaetze(Arrays.asList(
                        Umsatz.newBuilder().setBetrag(150.75).setWaehrung("EUR").build(),
                        Umsatz.newBuilder().setBetrag(320.40).setWaehrung("USD").build()))
                .addAllInteressen(Arrays.asList(InteresseType.SPORT, InteresseType.LESEN))
                .build();

        // When
        Kunde kunde = service.fromProto(protoKunde);

        // Then
        softly.assertThat(kunde).isNotNull();
        softly.assertThat(kunde.id()).isEqualTo(TEST_ID);
        softly.assertThat(kunde.nachname()).isEqualTo(TEST_NACHNAME);
        softly.assertThat(kunde.email()).isEqualTo(TEST_EMAIL);
        softly.assertThat(kunde.geburtsdatum()).isEqualTo(TEST_GEBURTSDATUM);
        softly.assertThat(kunde.homepage()).isEqualTo(new URL("http://www.mueller.de"));
        softly.assertThat(kunde.geschlecht()).isEqualTo(GeschlechtType.MAENNLICH);
        softly.assertThat(kunde.familienstand()).isEqualTo(FamilienstandType.LEDIG);
        softly.assertThat(kunde.umsaetze()).hasSize(1);
        softly.assertThat(kunde.interessen()).hasSize(1);
    }
}
