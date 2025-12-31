package org.josiasguerrero.shared.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public record Money(BigDecimal amount, Currency currency) {

  public Money {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Amount cannot be negative");
    }
    if (currency == null) {
      throw new IllegalArgumentException("Currency cannot be null");
    }

    amount = amount.setScale(2, RoundingMode.HALF_UP);
  }

  public Money(BigDecimal amount) {
    this(amount, Currency.getInstance("USD"));
  }

  public Money(double amount) {
    this(BigDecimal.valueOf(amount), Currency.getInstance("USD"));
  }

  public Money add(Money other) {
    validateSameCurrency(other);
    return new Money(this.amount.add(other.amount), this.currency);
  }

  public Money substract(Money other) {
    validateSameCurrency(other);
    return new Money(this.amount.subtract(other.amount), this.currency);
  }

  public Money multiply(BigDecimal multiplier) {
    return new Money(this.amount.multiply(multiplier), this.currency);
  }

  public boolean isGreaterThan(Money other) {
    validateSameCurrency(other);
    return this.amount.compareTo(other.amount) > 0;
  }

  public boolean isPositive() {
    return this.amount.compareTo(BigDecimal.ZERO) > 0;
  }

  public static Money zero() {
    return new Money(BigDecimal.ZERO);
  }

  public static Money of(double amount) {
    return new Money(amount);
  }

  private void validateSameCurrency(Money other) {

    if (!this.currency.equals(other.currency)) {
      throw new IllegalArgumentException("Cannot operate with different currencies: " +
          this.currency + " and " + other.currency);

    }
  }
}
