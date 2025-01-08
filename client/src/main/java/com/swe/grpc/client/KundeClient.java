package com.swe.grpc.client;
/* package com.swe.grpc.client;


import com.swe.grpc.KundeProto;
import com.swe.grpc.KundeReadServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class SweGrpcClientApplication {

    public static KundeProto.Kunde findById(int id) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        KundeReadServiceGrpc.KundeReadServiceBlockingStub stub = KundeReadServiceGrpc.newBlockingStub(channel);

        try {
            KundeProto.KundeByIdRequest request = KundeProto.KundeByIdRequest.newBuilder().setId(id).build();
            return stub.findById(request);
        } finally {
            channel.shutdown();
        }
    }


    public static void main(String[] args) {
        System.out.println("Hello from gRPC client!");

        final var kunde = findById(1);
        System.out.println("Kunde: " + kunde);
    }

}
 */

import com.swe.grpc.KundeProto.*;
import com.swe.grpc.KundeReadServiceGrpc;
import com.swe.grpc.KundeWriteServiceGrpc;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.time.Instant;
import java.util.Arrays;

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
