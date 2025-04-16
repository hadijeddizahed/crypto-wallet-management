package com.swisspost.cryptowalletmanagement.service.exceptions;

import lombok.Data;

@Data
public class DuplicateUserException extends BusinessException{
    private String message;

    public DuplicateUserException(String message) {
        super(message);
    }
}
