/*
 *
 *  * Copyright 2015-2016 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.yqboots.project.security.autoconfigure;

import com.yqboots.project.security.access.support.DelegatingObjectIdentityRetrievalStrategy;
import com.yqboots.project.security.access.support.ObjectIdentityRetrieval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;
import java.util.List;

/**
 * Configuration for ACL.
 *
 * @author Eric H B Zhan
 * @since 1.1.0
 */
public class AclSecurityConfiguration {
    public static final String ACL_CACHE_NAME = "acls";

    @Autowired
    private DataSource dataSource;

    @Autowired
    private RoleHierarchy roleHierarchy;

    @Autowired(required = false)
    private List<ObjectIdentityRetrieval> objectIdentityRetrievals;

    @Bean
    public PermissionEvaluator permissionEvaluator() {
        AclPermissionEvaluator bean = new AclPermissionEvaluator(aclService());
        bean.setObjectIdentityRetrievalStrategy(aclObjectIdentityRetrievalStrategy());
        bean.setObjectIdentityGenerator(aclObjectIdentityGenerator());
        bean.setSidRetrievalStrategy(aclSidRetrievalStrategy());
        return bean;
    }

    @Bean
    public AclService aclService() {
        return new JdbcAclService(dataSource, aclLookupStrategy());
    }

    @Bean
    public LookupStrategy aclLookupStrategy() {
        return new BasicLookupStrategy(dataSource, aclCache(), aclAuthorizationStrategy(),
                aclPermissionGrantingStrategy());
    }

    @Bean
    public AclCache aclCache() {
        return new SpringCacheBasedAclCache(new ConcurrentMapCache(ACL_CACHE_NAME), aclPermissionGrantingStrategy(),
                aclAuthorizationStrategy());
    }

    @Bean
    public PermissionGrantingStrategy aclPermissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("SUPERVISOR"));
    }

    @Bean
    public ObjectIdentityRetrievalStrategy aclObjectIdentityRetrievalStrategy() {
        return new DelegatingObjectIdentityRetrievalStrategy(objectIdentityRetrievals);
    }

    @Bean
    public ObjectIdentityGenerator aclObjectIdentityGenerator() {
        return new DelegatingObjectIdentityRetrievalStrategy(objectIdentityRetrievals);
    }

    @Bean
    public SidRetrievalStrategy aclSidRetrievalStrategy() {
        return new SidRetrievalStrategyImpl(roleHierarchy);
    }
}
