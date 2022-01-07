package ru.dreamworkerln.spring.db.services;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraph;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dreamworkerln.spring.db.repositories.CustomRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public abstract class BaseRepoAccessService<T> {

    //protected EntityGraph defaultEntityGraph = EntityGraphs.empty();

    private final CustomRepository<T, Long> baseRepository;

    protected BaseRepoAccessService(CustomRepository<T, Long> baseRepository) {
        this.baseRepository = baseRepository;
    }

    public Optional<T> findById(Long id) {
        return baseRepository.findById(id);
    }

    public Optional<T> findById(Long id, EntityGraph entityGraph) {
        return baseRepository.findById(id, entityGraph);
    }

    public T findByIdOrError(Long id) {
        return baseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Entity by id: " + id + " not found"));
    }

    public T findByIdOrError(Long id, EntityGraph entityGraph) {
        return baseRepository.findById(id, entityGraph)
            .orElseThrow(() -> new IllegalArgumentException("Entity by id: " + id + " not found"));
    }

    public Optional<T> findOne(Specification<T> spec) {
        return baseRepository.findOne(spec);
    }

    public List<T> findAllById(List<Long> listId) {
        return baseRepository.findAllById(listId);
    }

    public List<T> findAll(Specification<T> spec) {
        return baseRepository.findAll(spec);
    }

    public Page<T> findAll(Specification<T> spec, PageRequest pageable) {
        return baseRepository.findAll(spec, pageable);
    }

    public List<T> findAll() {
        return baseRepository.findAll();
    }

    public T save(T t) {
        return baseRepository.save(t);
    }

    public List<T> saveAll(Iterable<T> list) {
        return baseRepository.saveAll(list);
    }

    public void delete(T t) {
        baseRepository.delete(t);
    }

}
