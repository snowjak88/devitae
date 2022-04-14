package org.snowjak.devitae.data.repositories;

import org.snowjak.devitae.data.entities.Scope;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface ScopeRepository extends CrudRepository<Scope, Integer> {

    Scope findByName(String name);

    Collection<Scope> findByIsDefaultTrue();
}
