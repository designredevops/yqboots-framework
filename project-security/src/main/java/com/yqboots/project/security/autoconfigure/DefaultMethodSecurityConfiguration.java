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

import org.springframework.security.access.expression.DenyAllPermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * Extends {@link GlobalMethodSecurityConfiguration} to provide global method security.
 *
 * @author Eric H B Zhan
 * @since 1.1.0
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class DefaultMethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {
    /**
     * {@inheritDoc}
     */
    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler bean = (DefaultMethodSecurityExpressionHandler) super.createExpressionHandler();
        // TODO: Customized PermissionEvaluator
        bean.setPermissionEvaluator(new DenyAllPermissionEvaluator());

        return bean;
    }
}
