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
    <S extends User> S save(S user);

    @RestResource(exported = false)
    @Override
    <S extends User> Iterable<S> saveAll(Iterable<S> entities);

    @PreAuthorize("isAuthenticated() && ( hasAuthority('SCOPE_user_viewDetails') || authentication.principal.claims['id'] == #userId )")
    @RestResource(description = @Description("Find a user by their ID."))
    @Override
    Optional<User> findById(Integer userId);

    @RestResource(exported = false)
    @Override
    boolean existsById(Integer userId);

    @RestResource(exported = false)
    @Override
    Page<User> findAll(Pageable pageable);

    @RestResource(exported = false)
    @Override
    Iterable<User> findAllById(Iterable<Integer> userIds);

    @PreAuthorize("isAuthenticated() && hasAuthority('SCOPE_user_delete') && authentication.principal.claims['id'] != #userId")
    @RestResource(description = @Description("Delete a user by their ID."))
    @Override
    void deleteById(Integer userId);

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
