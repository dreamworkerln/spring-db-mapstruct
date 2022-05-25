package ru.dreamworkerln.spring.db.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.dreamworkerln.spring.db.entities.AbstractEntity;
import ru.dreamworkerln.spring.db.specifications.SpecBuilder;
import ru.dreamworkerln.spring.utils.common.dto.AbstractDto;
import ru.dreamworkerln.spring.utils.common.dto.AbstractSpecDto;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public abstract class AbstractConverter<E extends AbstractEntity, D extends AbstractDto, S extends AbstractSpecDto> {

    @Autowired
    protected Validator validator;
    @Autowired
    protected ObjectMapper objectMapper;

    protected AbstractMapper<E,D> entityMapper;
    protected SpecBuilder<E,S> specBuilder;

    protected Class<E> entityClass;
    protected Class<D> dtoClass;
    protected Class<S> specClass;

    @SuppressWarnings("unchecked")
    public AbstractConverter() {
        Class<?> clazz = this.getClass();
        ParameterizedType p = (ParameterizedType) clazz.getGenericSuperclass();
        Type[] genParams = p.getActualTypeArguments();
        //System.out.println(Arrays.toString(genParams));

        Assert.isTrue(genParams.length == 3, "AbstractConverter generic params count != 3");
        entityClass = (Class<E>) genParams[0];
        dtoClass = (Class<D>) genParams[1];
        specClass = (Class<S>) genParams[2];
    }


//    // Будешь @Autowired через конструктор - придется в конструкторах наследников юзать super.constructor(...)
//    @Autowired
//    public void setValidator(Validator validator) {
//        this.validator = validator;
//    }
//
//    @Autowired
//    public void setObjectMapper(ObjectMapper objectMapper) {
//        this.objectMapper = objectMapper;
//    }

    // Json => T
    public <T> T get(JsonNode params, Class<T> clazz) {

        T result;

        // parsing request
        try {
            result = objectMapper.treeToValue(params, clazz);

            // validate
            if (result == null) {
                throw new ValidationException("Found null - validation failed");
            }

            //if(Number.class.isAssignableFrom(clazz))
        }
        catch (JsonProcessingException e) {
            throw new ParseException(0, "Param parse error", e);
        }
        return result;
    }


    // Json => T
    public <T> T getByField(JsonNode params, String fieldName, Class<T> clazz) {

        T result;

        // parsing request
        try {
            params = params.get(fieldName);
            // validate
            if (params == null) {
                throw new ValidationException("Field not found - validation failed");
            }

            result = objectMapper.treeToValue(params, clazz);

            // validate
            if (result == null) {
                throw new ValidationException("Found null - validation failed");
            }

            //if(Number.class.isAssignableFrom(clazz))
        }
        catch (JsonProcessingException e) {
            throw new ParseException(0, "Param parse error", e);
        }
        return result;
    }


/*
    // Json => Long
    public Long getLong(JsonNode params) {

        Long result;

        // parsing request
        try {
            result = objectMapper.treeToValue(params, Long.class);

            // validate
            if (result == null || result < 0) {
                throw new ValidationException("Id validation failed");
            }
        }
        catch (JsonProcessingException e) {
            throw new ParseException(0, "Id parse error", e);
        }
        return result;
    }
*/


    // Json => List<T>
    public <T> List<T> getList(JsonNode params, Class<T> clazz) {


        List<T> result;
        try {

            if (params == null) {
                throw new ValidationException("params = null");
            }
            // https://stackoverflow.com/questions/6349421/how-to-use-jackson-to-deserialise-an-array-of-objects

            //noinspection rawtypes
            Class tArrayClass = Array.newInstance(clazz, 0).getClass();
            //noinspection unchecked
            result = /*(List<T>)*/Arrays.asList((T[])objectMapper.treeToValue(params, tArrayClass));

            result.forEach(l -> {
                if (l == null) {
                    throw new ValidationException("List<T> contains null elements");
                }
            });
        }
        catch (IOException e) {
            throw new ParseException(0, "idList param parse error", e);
        }

        return result;
    }

/*

    // Json => List<Long>
    public List<Long> getLongList(JsonNode params) {

        List<Long> result;
        try {

            if (params == null) {
                throw new ValidationException("IdList = null");
            }
            
            // https://stackoverflow.com/questions/6349421/how-to-use-jackson-to-deserialise-an-array-of-objects
            result = Arrays.asList(objectMapper.treeToValue(params, Long[].class));

            result.forEach(l -> {
                if (l == null) {
                    throw new ValidationException("IdList contains null elements");
                }
            });
        }
        catch (JsonProcessingException e) {
            throw new ParseException(0 ,"idList param parse error", e);
        }

        return result;
    }
*/

/*

    // Json => String
    public String getId(JsonNode params) {

        String result;

        // parsing request
        try {
            result = objectMapper.treeToValue(params, String.class);

            // validate
            if (StringUtils.isBlank(result)) {
                throw new ValidationException("String validation failed");
            }
        }
        catch (JsonProcessingException e) {
            throw new ParseException(0, "String parse error", e);
        }
        return result;
    }

*/

    // T => Json
    public JsonNode toJson(JsonNode params) {
        return objectMapper.valueToTree(params);
    }


    // T.getId() => Json
    public JsonNode toIdJson(AbstractEntity entity) {
        return objectMapper.valueToTree(entity.getId());
    }



    // ----------------------------------------------------------------------------




    // Json => Dto => Entity
    public E toEntity(JsonNode params)  {
        try {
            D dto = objectMapper.treeToValue(params, dtoClass);
            E result = entityMapper.toEntity(dto);
            validate(result);
            return result;
        }
        catch (ValidationException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ParseException(0, "toEntity parse error", e);
        }
    }


    // Dto => Entity
    public E toEntity(D dto)  {
        try {
            E result = entityMapper.toEntity(dto);
            validate(result);
            return result;
        }
        catch (ValidationException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ParseException(0, "toEntity parse error", e);
        }
    }

    public List<E> toEntityList(Iterable<D> dtoList)  {
        return entityMapper.toEntityList(dtoList);
    }


    // Entity => Dto => Json
    public JsonNode toDtoJson(E entity) {
        try {
            D dto = entityMapper.toDto(entity);
            return objectMapper.valueToTree(dto);
        }
        catch (Exception e) {
            throw new ParseException(0, "toDtoJson convert error", e);
        }
    }

    // Entity => Dto
    public D toDto(E entity) {
        try {
            return entityMapper.toDto(entity);
        }
        catch (Exception e) {
            throw new ParseException(0, "toDtoJson convert error", e);
        }
    }


    // EntityList => Dto => Json
    public JsonNode toDtoListJson(Iterable<E> entityList) {
        try {
            List<D> dtoList = entityMapper.toDtoList(entityList);
            return objectMapper.valueToTree(dtoList);
        }
        catch (Exception e) {
            throw new ParseException(0, "toDtoListJson convert error", e);
        }
    }

    // EntityList => Dto
    public List<D> toDtoList(Iterable<E> entityList) {
        return entityMapper.toDtoList(entityList);
    }

    // ----------------------------------------------------------------------


    // Entity => Dto
    public Optional<D> toDto(Optional<E> entityOp) {
        Optional<D> result = Optional.empty();

        if (entityOp.isPresent()) {
            result = Optional.of(toDto(entityOp.get()));
        }
        return result;
    }


/*    // (Spec)Json => Dto (Specifications have no Entities)
    public S toSpecDto(JsonNode params) {

        try {
            S result = objectMapper.treeToValue(params, specClass);
            validateSpecDto(result);
            return result;
        }
        catch (ValidationException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ParseException(0, "toSpecDto convert error", e);
        }
    }*/


    // (Spec)Json => Dto (Specifications have no Entities)

//    /**
//     * Validate specification
//     * @param spec Specification
//     */
//    public void validate(S spec) {
//
//        try {
//            validateSpecDto(spec);
//        }
//        catch (ValidationException e) {
//            throw e;
//        }
//        catch (Exception e) {
//            throw new ParseException(0, "toSpecDto convert error", e);
//        }
//    }

    /**
     * Validate specDto and build specification
     * @param specDto SpecDto
     * @return Specification
     */
    public Specification<E> buildSpec(S specDto) {

        validate(specDto);
        return specBuilder.build(specDto);
    }



    // =================================================================================================================


    // check Entity validity
    protected void validate(E entity) {
        Set<ConstraintViolation<E>> violations = validator.validate(entity);
        if (violations.size() != 0) {
            throw new ConstraintViolationException("Entity validation failed", violations);
        }
    }


    // check SpecDto validity
    private void validate(S specDto) {

        if (specDto != null) {
            Set<ConstraintViolation<S>> violations = validator.validate(specDto);
            if (violations.size() != 0) {
                throw new ConstraintViolationException("Specification validation failed", violations);
            }
        }
    }


}



//    // (Spec)Json => Dto (Specifications have no Entities)
//    public S toSpecDto(JsonNode params) {
//
//        try {
//            S result = objectMapper.treeToValue(params, specClass);
//            if(result != null) {
//                validateSpecDto(result);
//            }
//            return result;
//        }
//        catch (ValidationException e) {
//            throw e;
//        }
//        catch (Exception e) {
//            throw new ParseException(0, "toSpecDto convert error", e);
//        }
//    }
