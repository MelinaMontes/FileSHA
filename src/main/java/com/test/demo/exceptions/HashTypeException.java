package com.test.demo.exceptions;
/*
* lanzamos esto cuando se intenta cargar un hash diferente a 512 o 256
*
* */
public class HashTypeException extends RuntimeException {

    public HashTypeException(String message) {
        super(message);
    }
}
