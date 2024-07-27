package com.goodsending.product.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DateRangeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {
  String message() default "경매 시작일은 내일부터 7일 동안 선택할 수 있습니다.";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
