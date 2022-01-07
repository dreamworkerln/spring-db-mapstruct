package ru.dreamworkerln.spring.db.configurations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


// Annotation used by Mapstruct to select appropriate constructor (read theirs manual)
// https://mapstruct.org/documentation/stable/reference/html/#mapping-with-constructors
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface Default {
}
