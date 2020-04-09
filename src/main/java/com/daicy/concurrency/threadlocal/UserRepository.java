package com.daicy.concurrency.threadlocal;

import java.util.UUID;


public class UserRepository {
    public String getUserNameForUserId(Integer userId) {
        return UUID.randomUUID().toString();
    }
}
