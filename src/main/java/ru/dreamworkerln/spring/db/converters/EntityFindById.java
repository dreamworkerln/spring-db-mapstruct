package ru.dreamworkerln.spring.db.converters;

import java.util.Optional;

public interface EntityFindById<E> {
    Optional<E> find(Long id);
}
