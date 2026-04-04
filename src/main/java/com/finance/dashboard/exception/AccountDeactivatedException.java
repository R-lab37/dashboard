package com.finance.dashboard.exception;

public class AccountDeactivatedException extends RuntimeException {
    public AccountDeactivatedException() {
        super("Account is deactivated. Please contact an administrator.");
    }
}