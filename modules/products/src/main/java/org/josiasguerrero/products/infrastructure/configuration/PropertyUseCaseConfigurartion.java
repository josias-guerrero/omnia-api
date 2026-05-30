package org.josiasguerrero.products.infrastructure.configuration;

import org.josiasguerrero.products.application.usecase.property.CreatePropertyUseCase;
import org.josiasguerrero.products.application.usecase.property.DeletePropertyUseCase;
import org.josiasguerrero.products.application.usecase.property.FindAllPropertiesUseCase;
import org.josiasguerrero.products.application.usecase.property.FindPropertyByIdUseCase;
import org.josiasguerrero.products.application.usecase.property.UpdatePropertyUseCase;
import org.josiasguerrero.products.domain.port.PropertyRepository;
import org.josiasguerrero.shared.aplication.validation.DtoValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropertyUseCaseConfigurartion {
  @Bean
  public CreatePropertyUseCase createPropertyUseCase(
      PropertyRepository propertyRepository, DtoValidator validator) {
    return new CreatePropertyUseCase(propertyRepository, validator);
  }

  @Bean
  public FindAllPropertiesUseCase findAllPropertiesUseCase(PropertyRepository propertyRepository) {
    return new FindAllPropertiesUseCase(propertyRepository);
  }

  @Bean
  public FindPropertyByIdUseCase findPropertyByIdUseCase(PropertyRepository propertyRepository) {
    return new FindPropertyByIdUseCase(propertyRepository);
  }

  @Bean
  public UpdatePropertyUseCase updatePropertyUseCase(
      PropertyRepository propertyRepository, DtoValidator validator) {
    return new UpdatePropertyUseCase(propertyRepository, validator);
  }

  @Bean
  public DeletePropertyUseCase deletePropertyUseCase(PropertyRepository propertyRepository) {
    return new DeletePropertyUseCase(propertyRepository);
  }
}
