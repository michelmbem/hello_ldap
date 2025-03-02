package org.addy.hello_ldap.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group implements GrantedAuthority {
    private String name;

    @Override
    @JsonIgnore
    public String getAuthority() {
        return getName();
    }
}
