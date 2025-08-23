package org.example.deadknight.exceptions;

public class ComponentNotFoundException extends RuntimeException {
    public ComponentNotFoundException(String message) {
        super("такого класса не существует: " + message);
    }
}
