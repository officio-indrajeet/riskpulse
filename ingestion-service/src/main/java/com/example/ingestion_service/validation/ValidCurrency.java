package com.example.ingestion_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CurrencyValidator.class)
public @interface ValidCurrency {
    String message() default "must be a valid ISO 4217 currency code (e.g. USD, EUR, GBP)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
