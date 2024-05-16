package ro.axon.dot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import ro.axon.dot.config.RoleConfig;
import ro.axon.dot.model.response.RolesList;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(RoleConfig.class)
public class RolesService {
    private final RoleConfig roleConfig;
    public RolesList getRoles() {
        RolesList roles = new RolesList();
        roles.setRoles(roleConfig.getRoles());
        return roles;
    }
}
