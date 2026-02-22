package com.research.portal.application.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " mit ID " + id + " nicht gefunden");
    }

    public ResourceNotFoundException(String resourceName, String field, String value) {
        super(resourceName + " mit " + field + " '" + value + "' nicht gefunden");
    }
}
