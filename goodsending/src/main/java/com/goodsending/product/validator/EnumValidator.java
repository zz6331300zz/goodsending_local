package com.goodsending.product.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, Enum> {

  @Override
  public boolean isValid(Enum value, ConstraintValidatorContext constraintValidatorContext) {
    return value != null;
  }
}
