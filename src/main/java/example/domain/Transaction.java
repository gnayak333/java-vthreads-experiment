package example.domain;

public record Transaction(String id, int amount, String merchantCode, int balance, int creditLimit) {}
