package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.model.Role;
import id.cs.ui.advprog.inthecost.model.UserRoles;

import java.util.List;

public interface UserRolesService {
    UserRoles assignRole(User user, Role role);
    void removeRole(User user, Role role);
    List<UserRoles> getRolesByUser(User user);
    boolean hasRole(User user, String roleName);
}
