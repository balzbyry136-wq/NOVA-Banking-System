package com.portfolio.bank.model;
public record BankTransaction(long id, String type, double amount, String description, String createdAt) {}
