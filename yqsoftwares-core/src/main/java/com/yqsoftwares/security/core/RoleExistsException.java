package com.yqsoftwares.security.core;

/**
 * Created by Administrator on 2016-01-17.
 */
public class RoleExistsException extends RuntimeException {
    public RoleExistsException(String message) {
        super(message);
    }
}