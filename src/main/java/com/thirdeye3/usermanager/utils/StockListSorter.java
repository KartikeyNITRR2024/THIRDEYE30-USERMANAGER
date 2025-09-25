package com.thirdeye3.usermanager.utils;

import java.util.Set;
import com.thirdeye3.usermanager.exceptions.ThresholdGroupNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class StockListSorter {
    public static String shorter(String input) {
        int n = 5;

        if (input.length() % n != 0) {
            throw new ThresholdGroupNotFoundException("Invalid stock list");
        }

        int count = input.length() / n;
        Set<String> uniqueNumbers = new HashSet<>();
        List<String> numbersList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String number = input.substring(i * n, (i + 1) * n);

            if (!uniqueNumbers.add(number)) {
                throw new ThresholdGroupNotFoundException("Invalid stock list");
            }

            numbersList.add(number);
        }
        numbersList.sort(Comparator.comparingInt(Integer::parseInt));
        return String.join("", numbersList);
    }
}
