package com.example.demo.common.cache;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSON;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONSerializer implements Serializer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private static final String STR_HEADER = "S:::";
    /**
     *
     */
    private static final String OBJ_HEADER = "O:::";

    @Override
    public String serialize(Object value) {
        if (value instanceof String) {
            return STR_HEADER + value;
        } else if (isNumeric(value)) {
            return value.toString();
        } else {
            return OBJ_HEADER + JSON.toJSONString(value);
        }
    }

    @Override
    public Object deSerialize(String value, Class<?> clazz) {
        if (StringUtils.isBlank(value)) {
            logger.debug("Skip for value is blank.");
            return value;
        }

        if (value.startsWith(STR_HEADER)) {

            return value.substring(STR_HEADER.length());
        } else if (value.startsWith(OBJ_HEADER)) {

            String v = value.substring(OBJ_HEADER.length());

            if (v.trim().startsWith("[")) {
                return JSON.parseArray(v, clazz);
            } else {
                return JSON.parseObject(v, clazz);
            }
        } else {
            return JSON.parseObject(value, clazz);
        }
    }

    private boolean isNumeric(Object obj)
    {
        return obj instanceof  Integer ||
                obj instanceof Long ||
                obj instanceof  Float ||
                obj instanceof  Double ||
                obj instanceof BigDecimal;
    }
}
