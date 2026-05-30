package org.josiasguerrero.products.infrastructure.configuration;

import org.josiasguerrero.products.domain.port.PropertyDomainService;
import org.josiasguerrero.products.domain.port.PropertyRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropertyDomainServiceConfiguration {
  @Bean
  public PropertyDomainService propertyDomainService(PropertyRepository repository) {
    return new PropertyDomainService(repository);
  }
}
