package org.snowjak.devitae.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.snowjak.devitae.data.conversion.ScopeSerializationSupport;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "Users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int id;

    @Version
    private int version;

    @Basic(optional = false)
    @Column(name = "username", length = 255, updatable = false, nullable = false, unique = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String username;

    @Basic
    @Column(name = "password", length = 255, nullable = false)
    @JsonIgnore
    private String password;

    @ManyToMany(targetEntity = Scope.class, fetch = FetchType.EAGER)
    @JoinTable(name = "User_Scopes", joinColumns = @JoinColumn(name = "userID"), inverseJoinColumns = @JoinColumn(name = "scopeID"))
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonSerialize(contentUsing = ScopeSerializationSupport.SerializerByName.class)
    @JsonDeserialize(contentUsing = ScopeSerializationSupport.DeserializerByName.class)
    private Collection<Scope> scopes = new ArrayList<>();

    @Basic(optional = false)
    @Column(name = "created", nullable = false)
    @CreatedDate
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant created;

    @Basic(optional = false)
    @Column(name = "lastModified", nullable = false)
    @LastModifiedDate
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant lastModified;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<Scope> getScopes() {
        return scopes;
    }

    public void setScopes(Collection<Scope> scopes) {
        this.scopes = scopes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getScopes();
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
