package org.snowjak.devitae.data.repositories;

import org.snowjak.devitae.data.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

@RepositoryRestResource(
        collectionResourceDescription = @Description("Repository for all registered users."),
        itemResourceDescription = @Description("Repository for a single registered user.")
)
@PreAuthorize("isAuthenticated()")
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {

    @PreAuthorize("permitAll")
    @RestResource(exported = false)
    public User findByUsername(String username);

    @RestResource(exported = false)
    @Override
    <S extends User> S save(S entity);

    @RestResource(exported = false)
    @Override
    <S extends User> Iterable<S> saveAll(Iterable<S> entities);

    @RestResource(exported = false)
    @Override
    Optional<User> findById(Integer integer);

    @RestResource(exported = false)
    @Override
    boolean existsById(Integer integer);

    @RestResource(exported = false)
    @Override
    Page<User> findAll(Pageable pageable);

    @RestResource(exported = false)
    @Override
    Iterable<User> findAllById(Iterable<Integer> integers);

    @RestResource(exported = false)
    @Override
    void deleteById(Integer integer);

    @RestResource(exported = false)
    @Override
    void delete(User entity);

    @RestResource(exported = false)
    @Override
    void deleteAllById(Iterable<? extends Integer> integers);

    @RestResource(exported = false)
    @Override
    void deleteAll(Iterable<? extends User> entities);

    @RestResource(exported = false)
    @Override
    void deleteAll();
}
