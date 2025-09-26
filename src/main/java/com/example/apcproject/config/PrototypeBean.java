package com.example.apcproject.config;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Scope("prototype")
public class PrototypeBean {
    private final String instanceId = UUID.randomUUID().toString();
    public String getInstanceId() { return instanceId; }
}


