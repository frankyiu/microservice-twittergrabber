package com.microservices.demo.elastic.query.service.business.impl;


import com.microservices.demo.elastic.query.service.business.QueryUserService;
import com.microservices.demo.elastic.query.service.dataaccess.entity.UserPermission;
import com.microservices.demo.elastic.query.service.dataaccess.repository.UserPermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TwitterQueryUserService implements QueryUserService {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterQueryUserService.class);
    private final UserPermissionRepository userPermissionRepository;

    public TwitterQueryUserService(UserPermissionRepository userPermissionRepository) {
        this.userPermissionRepository = userPermissionRepository;
    }

    @Override
    public Optional<List<UserPermission>> findAllPermissionByUsername(String username) {
        LOG.info("Finding user permission by username {}", username);
        return userPermissionRepository.findPermissionByUsername(username);
    }
}
