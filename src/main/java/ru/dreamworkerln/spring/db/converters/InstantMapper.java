package ru.dreamworkerln.spring.db.converters;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.util.Objects;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface InstantMapper {

    default Long map(Instant i) {

        Long result = null;

        if (Objects.nonNull(i)){
            result = i.getEpochSecond();
        }
        return result;
    }


    default Instant map(Long l) {

        Instant result = null;

        if (Objects.nonNull(l)){
            result = Instant.ofEpochSecond(l);
        }
        return result;
    }
}