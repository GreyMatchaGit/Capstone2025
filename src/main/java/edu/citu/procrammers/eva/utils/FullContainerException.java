package edu.citu.procrammers.eva.utils;

public class FullContainerException extends RuntimeException {

    public FullContainerException() {
        super("The container is full. Cannot add more elements.");
    }
}
