package org.snowjak.devitae.data.entities;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
@Table(name = "Scopes")
public class Scope implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    private int id;

    @Version
    private int version;

    @Basic(optional = false)
    @Column(name = "name", updatable = false, insertable = false, length = 255, unique = true)
    private String name;

    @Basic(optional = false)
    @Column(name = "isDefault", updatable = true, insertable = true)
    private boolean isDefault = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    @Override
    public String getAuthority() {
        return name;
    }
}
