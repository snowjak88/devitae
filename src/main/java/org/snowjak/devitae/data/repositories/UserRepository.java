package org.snowjak.devitae.data.repositories;

import org.snowjak.devitae.data.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

    public User findByUsername(String username);
}
