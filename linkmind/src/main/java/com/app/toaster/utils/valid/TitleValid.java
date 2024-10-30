package com.app.toaster.utils.valid;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = TitleValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface TitleValid {
	String message() default "Invalid title";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	String pattern() default "^[\\S][가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9\\s]{0,20}$";
}
