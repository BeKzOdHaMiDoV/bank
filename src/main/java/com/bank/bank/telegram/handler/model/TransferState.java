package com.bank.bank.telegram.handler.model;

public enum TransferState {
    NONE,
    WAIT_AMOUNT,
    WAIT_RECEIVER,
    WAIT_CONFIRM
}
