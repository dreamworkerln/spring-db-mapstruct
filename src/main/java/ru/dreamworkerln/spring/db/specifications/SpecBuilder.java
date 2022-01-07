package ru.dreamworkerln.spring.db.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.dreamworkerln.spring.db.entities.AbstractEntity;
import ru.dreamworkerln.spring.utils.common.dto.AbstractSpecDto;

public interface SpecBuilder<E extends AbstractEntity, S extends AbstractSpecDto> {
    Specification<E> build(S specDto);
}
