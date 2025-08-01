package com.rikkamus.craftersoneclaimvisualizer.claim;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;

import java.net.*;
import java.net.http.*;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class ClaimRepository implements AutoCloseable {

    private static boolean isStatusOk(int responseCode) {
        return responseCode >= 200 && responseCode < 300;
    }

    private final URI endpointUri;
    private final String userAgent;
    private final Duration timeout;
    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public ClaimRepository(URI endpointUri, String userAgent, Duration timeout) {
        this.endpointUri = endpointUri;
        this.userAgent = userAgent;
        this.timeout = timeout;
        this.client = HttpClient.newBuilder()
                                .connectTimeout(timeout)
                                .followRedirects(HttpClient.Redirect.NORMAL)
                                .build();
        this.objectMapper = new ObjectMapper();
    }

    public CompletableFuture<Collection<Claim>> findAllClaims() {
        HttpRequest request = HttpRequest.newBuilder(this.endpointUri)
                                         .setHeader(HttpHeaders.ACCEPT, "application/json")
                                         .setHeader(HttpHeaders.USER_AGENT, this.userAgent)
                                         .timeout(this.timeout)
                                         .build();

        return this.client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).exceptionally(e -> {
            if (e instanceof HttpTimeoutException || (e instanceof CompletionException && e.getCause() instanceof HttpTimeoutException)) {
                throw new RepositoryFetchException("Request timed out.", e);
            } else {
                throw new RuntimeException(e);
            }
        }).thenApply(response -> {
            int status = response.statusCode();
            if (!ClaimRepository.isStatusOk(status)) {
                throw new RepositoryFetchException(String.format("Received error response from server (response code: %d).", status));
            }

            try {
                return this.objectMapper.readValue(response.body(), this.objectMapper.getTypeFactory().constructCollectionType(List.class, Claim.class));
            } catch (Exception e) {
                throw new RepositoryFetchException("Failed to parse response body.", e);
            }
        });
    }

    @Override
    public void close() {
        this.client.close();
    }

}
