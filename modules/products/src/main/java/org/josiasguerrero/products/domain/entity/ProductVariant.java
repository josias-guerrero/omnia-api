package org.josiasguerrero.products.domain.entity;

import java.time.LocalDateTime;
import java.util.Set;

import org.josiasguerrero.products.domain.valueobject.Barcode;
import org.josiasguerrero.products.domain.valueobject.ProductVariantId;
import org.josiasguerrero.products.domain.valueobject.PropertyId;
import org.josiasguerrero.products.domain.valueobject.Sku;
import org.josiasguerrero.products.domain.valueobject.Stock;
import org.josiasguerrero.products.domain.valueobject.VariantAttribute;
import org.josiasguerrero.shared.domain.valueobject.Money;

public class ProductVariant {
  private final ProductVariantId id;

  private Sku sku;
  private Barcode barcode;

  private Stock stock;
  private Money cost;
  private Money price;
  private Set<VariantAttribute> properties;

  private final LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public ProductVariant(ProductVariantId id, Sku sku, Barcode barcode, Stock stock, Money cost, Money price,
      Set<VariantAttribute> properties, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id;
    this.sku = sku;
    this.barcode = barcode;
    this.stock = stock;
    this.cost = cost;
    this.price = price;
    this.properties = properties;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public ProductVariant(ProductVariantId id, Sku sku, Barcode barcode, Stock stock, Money cost, Money price,
      Set<VariantAttribute> properties) {

    validatePricing(cost, price);
    this.id = id;
    this.sku = sku;
    this.barcode = barcode;
    this.stock = stock;
    this.cost = cost;
    this.price = price;
    this.properties = properties;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  public ProductVariantId getId() {
    return id;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public Sku getSku() {
    return sku;
  }

  public Barcode getBarcode() {
    return barcode;
  }

  public void setBarcode(Barcode barcode) {
    this.barcode = barcode;
  }

  public Stock getStock() {
    return stock;
  }

  public void increaseStock(int amount) {
    this.stock = this.stock.increase(amount);
    this.updatedAt = LocalDateTime.now();
  }

  public void decreaseStock(int amount) {
    this.stock = this.stock.decrease(amount);
    this.updatedAt = LocalDateTime.now();
  }

  public void adjustStock(int amount) {
    this.stock = new Stock(amount);
    this.updatedAt = LocalDateTime.now();
  }

  public Money getCost() {
    return cost;
  }

  public void setCost(Money cost) {
    this.cost = cost;
  }

  public Money getPrice() {
    return price;
  }

  public void setPrice(Money price) {
    this.price = price;
  }

  public Set<VariantAttribute> getProperties() {
    return properties;
  }

  public void setProperties(Set<VariantAttribute> properties) {
    this.properties = properties;
  }

  public void clearProperties() {
    this.properties.clear();
  }

  public void addProperty(VariantAttribute variantAttribute) {
    this.properties.add(variantAttribute);
    this.updatedAt = LocalDateTime.now();
  }

  public void removeProperty(PropertyId propertyId) {
    this.properties.removeIf(attr -> attr.propertyId().equals(propertyId));
    this.updatedAt = LocalDateTime.now();
  }

  public void changeSku(Sku sku) {
    this.sku = sku;
    this.updatedAt = LocalDateTime.now();
  }

  public void updatePricing(Money newCost, Money newPrice) {
    validatePricing(newCost, newPrice);
    this.cost = newCost;
    this.price = newPrice;
    this.updatedAt = LocalDateTime.now();
  }

  public boolean hasStock() {
    return this.stock.isAvailable();
  }

  public boolean hasSufficientStock(int requiredAmount) {
    return this.stock.isSufficient(requiredAmount);
  }

  private void validatePricing(Money cost, Money price) {
    if (cost == null || price == null) {
      throw new IllegalArgumentException("Cost and price cannot be null");
    }
    if (price.isGreaterThan(cost)) {
      return;
    }

    throw new IllegalArgumentException("Price must be greater than cost");
  }
}
