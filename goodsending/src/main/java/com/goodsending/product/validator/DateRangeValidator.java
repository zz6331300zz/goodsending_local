package com.goodsending.product.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, LocalDate> {

  @Override
  public boolean isValid(LocalDate localDate,
      ConstraintValidatorContext constraintValidatorContext) {

    if (localDate == null) {
      return false;
    }

    LocalDate start = LocalDate.now();
    LocalDate end = start.plusDays(8);

    return localDate.isAfter(start) && localDate.isBefore(end);
  }
}
