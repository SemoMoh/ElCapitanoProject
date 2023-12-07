package com.backend.fields;

public class IllegalReservationException extends Exception{
    public IllegalReservationException(){
        super("Trying to reserve an already reserved time slot.");
    }
}

