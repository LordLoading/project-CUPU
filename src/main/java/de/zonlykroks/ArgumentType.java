package de.zonlykroks;

import java.util.function.Predicate;

public class ArgumentType {
    private final String typeName;
    private final Predicate<String> validator;

    public ArgumentType(String typeName, Predicate<String> validator) {
        this.typeName = typeName;
        this.validator = validator;
    }

    public boolean validate(String arg) {
        return validator.test(arg);
    }

    public String getTypeName() {
        return typeName;
    }
}
