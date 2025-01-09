package com.swe.grpc.client;

import java.time.Instant;
import java.util.Arrays;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import com.swe.grpc.KundeProto.*;
import com.swe.grpc.KundeProto.Adresse;
import com.swe.grpc.KundeProto.CreateKundeRequest;
import com.swe.grpc.KundeProto.FamilienstandType;
import com.swe.grpc.KundeProto.GeschlechtType;
import com.swe.grpc.KundeProto.InteresseType;
import com.swe.grpc.KundeProto.Kunde;
import com.swe.grpc.KundeProto.KundeByIdRequest;
import com.swe.grpc.KundeProto.KundeResponse;
import com.swe.grpc.KundeProto.Umsatz;
import com.swe.grpc.KundeReadServiceGrpc;
import com.swe.grpc.KundeWriteServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class KundeClient {
    public static void main(String[] args) {
        // Setup gRPC channel
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        // Stubs for services
        KundeReadServiceGrpc.KundeReadServiceBlockingStub readStub = KundeReadServiceGrpc.newBlockingStub(channel);
        KundeWriteServiceGrpc.KundeWriteServiceBlockingStub writeStub = KundeWriteServiceGrpc.newBlockingStub(channel);

        // 1. Fetch all customers
        System.out.println("Fetching all customers:");
        readStub.findAll(Empty.newBuilder().build())
                .forEachRemaining(kunde -> System.out.println(kunde));

        // 2. Fetch a customer by ID
        System.out.println("\nFetching customer by ID:");
        Kunde kunde = readStub
                .findById(KundeByIdRequest.newBuilder().setId("00000000-0000-0000-0000-000000000001").build());
        System.out.println(kunde);

        // 3. Create a new customer
        System.out.println("\nCreating a new customer:");
        CreateKundeRequest createRequest = CreateKundeRequest.newBuilder()
                .setNachname("Schmidt")
                .setEmail("anna.schmidt@example.com")
                .setKategorie(2)
                .setHasNewsletter(true)
                .setGeburtsdatum(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build())
                .setHomepage("https://www.annaschmidt.com")
                .setGeschlecht(GeschlechtType.WEIBLICH)
                .setFamilienstand(FamilienstandType.LEDIG)
                .setAdresse(Adresse.newBuilder()
                        .setPlz("76133")
                        .setOrt("Karlsruhe")
                        .build())
                .addAllUmsaetze(Arrays.asList(
                        Umsatz.newBuilder().setBetrag(150.75).setWaehrung("EUR").build(),
                        Umsatz.newBuilder().setBetrag(320.40).setWaehrung("USD").build()))
                .addAllInteressen(Arrays.asList(InteresseType.SPORT, InteresseType.LESEN))
                .build();

        KundeResponse createResponse = writeStub.createKunde(createRequest);
        System.out.println(createResponse.getMessage());

        // 4. Delete a customer
        System.out.println("\nDeleting a customer:");
        KundeResponse deleteResponse = writeStub
                .deleteKunde(KundeByIdRequest.newBuilder().setId("00000000-0000-0000-0000-000000000001").build());
        System.out.println(deleteResponse.getMessage());

        // Shutdown the channel
        channel.shutdown();
    }
}
