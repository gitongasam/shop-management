package com.devsam.shopmanagement.entity;

public class PhoneNumberUtil {

    public static String standardizeTo254(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }

        // 1. Remove non-digit characters
        String cleanedNumber = phoneNumber.replaceAll("[^0-9]", "");

        // 2. Handle 07... or 01... format
        if (cleanedNumber.startsWith("0") && cleanedNumber.length() == 10) {
            return "254" + cleanedNumber.substring(1);
        }

        // 3. Handle +254... format
        if (cleanedNumber.startsWith("254") && cleanedNumber.length() == 12) {
            return cleanedNumber;
        }

        // 4. Handle 7... or 1... (assuming 9-digit local number)
        if (cleanedNumber.length() == 9) {
            return "254" + cleanedNumber;
        }

        // If it already seems to be 254 and 12 digits, return as is
        if (cleanedNumber.startsWith("254") && cleanedNumber.length() == 12) {
            return cleanedNumber;
        }

        // Fallback for unexpected formats - might need refinement
        return cleanedNumber;
    }
}
