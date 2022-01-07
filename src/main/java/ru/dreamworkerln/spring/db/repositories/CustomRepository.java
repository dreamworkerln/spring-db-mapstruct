package ru.dreamworkerln.spring.db.repositories;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceUnitUtil;
import java.io.Serializable;

@NoRepositoryBean
@Transactional
public interface CustomRepository<T, ID extends Serializable> extends EntityGraphJpaRepository<T, ID>/*JpaRepository<T, ID>*/, EntityGraphJpaSpecificationExecutor<T>/*JpaSpecificationExecutor<T>*/ {

    @Transactional
    void refresh(T t);

    @Transactional
    void merge(T t);

    @Transactional
    void detach(T t);
    
    PersistenceUnitUtil getPersistenceUnitUtil();
}