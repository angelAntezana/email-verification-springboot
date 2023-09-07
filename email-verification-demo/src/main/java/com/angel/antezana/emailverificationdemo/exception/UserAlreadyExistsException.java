package com.angel.antezana.emailverificationdemo.exception;

public class UserAlreadyExistsException extends RuntimeException{
    
    public UserAlreadyExistsException(String message){
        super(message);
    }
}
