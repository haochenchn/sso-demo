package com.aaron.cas.service;

import java.util.Set;

public interface RoleService {

    String findRolesByUserId(String uid);

    Set<String> findAllRoles();
}
