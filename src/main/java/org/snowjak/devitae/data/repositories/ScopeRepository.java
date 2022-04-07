package org.snowjak.devitae.data.repositories;

import org.snowjak.devitae.data.entities.Scope;
import org.springframework.data.repository.CrudRepository;

public interface ScopeRepository extends CrudRepository<Scope, Integer> {

    Scope findByName(String name);
}
