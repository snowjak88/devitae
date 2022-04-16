package org.snowjak.devitae.data.repositories;

import org.snowjak.devitae.data.entities.Scope;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collection;

@RepositoryRestResource(
        collectionResourceDescription = @Description("All defined scopes/permissions."),
        itemResourceDescription = @Description("A single scope/permission."))
@PreAuthorize("isAuthenticated()")
public interface ScopeRepository extends CrudRepository<Scope, Integer> {

    @RestResource(exported = false)
    Scope findByName(String name);

    @RestResource(exported = false)
    Collection<Scope> findByIsDefaultTrue();

    @RestResource(exported = false)
    @Override
    <S extends Scope> S save(S entity);

    @RestResource(exported = false)
    @Override
    <S extends Scope> Iterable<S> saveAll(Iterable<S> entities);

    @RestResource(exported = false)
    @Override
    void deleteById(Integer integer);

    @RestResource(exported = false)
    @Override
    void delete(Scope entity);

    @RestResource(exported = false)
    @Override
    void deleteAllById(Iterable<? extends Integer> integers);

    @RestResource(exported = false)
    @Override
    void deleteAll(Iterable<? extends Scope> entities);

    @RestResource(exported = false)
    @Override
    void deleteAll();
}
