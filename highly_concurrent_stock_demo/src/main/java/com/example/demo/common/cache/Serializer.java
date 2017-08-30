package com.example.demo.common.cache;


public interface Serializer {
    
    String serialize(final Object value);
    
    Object deSerialize(String value, Class<?> clazz);
}
