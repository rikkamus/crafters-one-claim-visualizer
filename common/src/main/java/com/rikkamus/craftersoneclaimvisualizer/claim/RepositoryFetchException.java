package com.rikkamus.craftersoneclaimvisualizer.claim;

public class RepositoryFetchException extends RuntimeException {

    public RepositoryFetchException() {
        super();
    }

    public RepositoryFetchException(String message) {
        super(message);
    }

    public RepositoryFetchException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryFetchException(Throwable cause) {
        super(cause);
    }

}
