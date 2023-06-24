package com.microservices.demo.elastic.query.service.business;

import com.microservices.demo.elastic.query.service.dataaccess.entity.UserPermission;

import java.util.List;
import java.util.Optional;

public interface QueryUserService {

    Optional<List<UserPermission>> findAllPermissionByUsername(String username);
}
