package com.gamer.api.config;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;
import java.util.Locale;

@Component
public class StringToIntegerConverter implements Formatter<Integer> {
    
    @Override
    public Integer parse(String source, Locale locale) {
        if (source == null || source.isEmpty() || source.equals("undefined") || source.equals("null")) {
            return null;
        }
        
        try {
            return Integer.parseInt(source.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String print(Integer object, Locale locale) {
        return object == null ? "" : object.toString();
    }
}
