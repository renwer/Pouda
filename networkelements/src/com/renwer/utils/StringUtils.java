package com.renwer.utils;

public class StringUtils {
    public static String getParameterFromString(String inputString, String parameterName){
        int beginIndexOfParameter = inputString.indexOf(parameterName.toUpperCase());
        int beginIndexOfParameterValue = inputString.indexOf(':', beginIndexOfParameter);

        String parameterValue = inputString.substring(beginIndexOfParameterValue + 1);
        if(parameterValue.contains(" ")) {
            parameterValue = parameterValue.substring(0, parameterValue.indexOf(" "));
        }

        return parameterValue;
    }
}
