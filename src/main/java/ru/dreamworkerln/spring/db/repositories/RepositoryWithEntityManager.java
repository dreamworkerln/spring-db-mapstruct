package ru.dreamworkerln.spring.db.repositories;

import com.cosium.spring.data.jpa.entity.graph.repository.support.EntityGraphSimpleJpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import java.io.Serializable;



// Делаем нашу реализацию репо-интерфейса  CustomRepository базовым для Spring Data JPA,
// Spring будет генерить для него методы доступа(Find.One.By.Parent.Address и т.д.)
// См мануал по @NoRepositoryBean, @EnableJpaRepositories
// (providing an extended base interface for all repositories
// in combination with a custom repository base class)
// Do not forget to put @NoRepositoryBean on interface CustomRepository too or won't work

@NoRepositoryBean
@Transactional
public class RepositoryWithEntityManager<T, ID extends Serializable> extends EntityGraphSimpleJpaRepository<T, ID> /*SimpleJpaRepository<T, ID>*/
    implements CustomRepository<T, ID> {

    private final EntityManager entityManager;

    private final PersistenceUnitUtil persistenceUnitUtil;

    public RepositoryWithEntityManager(JpaEntityInformation entityInformation, EntityManager entityManager) {
        //noinspection unchecked
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
        this.persistenceUnitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
    }

    @Override
    public void refresh(T t) {
        entityManager.refresh(t);
    }


    @Override
    public void merge(T t) {
        entityManager.merge(t);
    }

    @Override
    public void detach(T t) {
        entityManager.detach(t);
    }

    public PersistenceUnitUtil getPersistenceUnitUtil() {
        return persistenceUnitUtil;
    }
}



