package org.snowjak.devitae.services;

import org.snowjak.devitae.data.entities.Scope;
import org.snowjak.devitae.data.repositories.ScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ScopeService {

    @Autowired
    private ScopeRepository scopeRepository;

    public Collection<Scope> getDefaultScopes() {
        return scopeRepository.findByIsDefaultTrue();
    }

    public Scope findByName(String name) {
        return scopeRepository.findByName(name);
    }
}
