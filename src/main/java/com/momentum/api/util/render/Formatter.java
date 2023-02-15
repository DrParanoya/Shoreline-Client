package com.momentum.api.util.render;

/**
 * @author bon55
 * @since 01/22/2023
 */
public class Formatter {

    /**
     * Formats an enum
     *
     * @param in The enum to format
     * @return The formatted enum
     */
    public static String formatEnum(Enum<?> in) {

        // enum name to string
        String enumName = in.name();

        // no capitalization
        if (!enumName.contains("_")) {

            // first character of the enum
            char firstChar = enumName.charAt(0);

            // rest of the string
            String suffixChars = enumName.split(String.valueOf(firstChar), 2)[1];

            // combine uppercase first char and remaining string
            return String.valueOf(firstChar).toUpperCase() + suffixChars.toLowerCase();
        }

        // split by spaces
        String[] names = enumName.split("_");

        // to return
        StringBuilder nameToReturn = new StringBuilder();

        // combine spaces
        for (String name : names) {

            // first character of the enum
            char firstChar = name.charAt(0);

            // rest of the string
            String suffixChars = name.split(String.valueOf(firstChar), 2)[1];

            // combine the phrases
            nameToReturn.append(String.valueOf(firstChar).toUpperCase())
                    .append(suffixChars.toLowerCase());
        }

        // return the combined string
        return nameToReturn.toString();
    }

    /**
     * Capitalises a given string
     *
     * @param in The string to capitalise
     * @return The string with the first letter capitalised
     */
    public static String capitalise(String in) {

        // capitalise
        if (in.length() != 0) {
            return Character.toTitleCase(in.charAt(0)) + in.substring(1);
        }

        // empty string
        return "";
    }
}
