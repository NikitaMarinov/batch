package com.spring.batch.validators;

import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.spring.batch.domain.HLPI;

public class HLPIValidator implements Validator<HLPI> {

    @Override
    public void validate(HLPI hlpi) throws ValidationException {
        if (validateString(hlpi.getHLPIName()) && validateString(hlpi.getSeriesReference()) && validateString(hlpi.getQuarter()) &&
                validateString(hlpi.getHLPI()) && validateString(hlpi.getNZHEC()) && validateString(hlpi.getNZHECName()) &&
                validateString(hlpi.getNZHECShort()) && validateString(hlpi.getLevel())) {

            throw new ValidationException("Строки не могут содержать сиволы кроме: букв, провелов и знаков припинания.");
        }

        if (validateInt(hlpi.getIndex())) {
            throw new ValidationException("Целое число должо быть в пределе [0 ; 2000].");
        }

        if (validateDouble(hlpi.getQuarterlyChange()) && validateDouble(hlpi.getAnnualChange())) {
            throw new ValidationException("Число с плавающей точкой должны быть в пределе [-20.0 ; 20.0].");
        }
    }

    private static boolean validateString(String input) {
        String regex = "^[a-zA-Z0-9.,!?\\s]+$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        return !matcher.matches();
    }

    private static boolean validateInt(Integer input) {
        return input <= 0 || input >= 2000;
    }

    private static boolean validateDouble(Double input) {
        return input <= -20.0 || input >= 20.0;
    }
}
