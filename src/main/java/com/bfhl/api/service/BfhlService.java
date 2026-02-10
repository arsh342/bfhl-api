package com.bfhl.api.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Pure computational service for mathematical operations.
 * All methods are stateless and thread-safe.
 */
@Service
public class BfhlService {

    /**
     * Generates the Fibonacci series up to {@code n} terms.
     *
     * @param n number of terms (must be >= 0)
     * @return Fibonacci series as a list of integers
     * @throws IllegalArgumentException if n is negative
     */
    public List<Integer> fibonacci(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Fibonacci input must be a non-negative integer, got: " + n);
        }
        if (n == 0) {
            return Collections.emptyList();
        }

        List<Integer> series = new ArrayList<>(n);
        series.add(0);
        if (n == 1) return series;

        series.add(1);
        for (int i = 2; i < n; i++) {
            series.add(series.get(i - 1) + series.get(i - 2));
        }
        return series;
    }

    /**
     * Filters and returns only the prime numbers from the input list.
     *
     * @param numbers list of integers to filter
     * @return list containing only prime numbers, preserving order
     * @throws IllegalArgumentException if the list is null or empty
     */
    public List<Integer> filterPrimes(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            throw new IllegalArgumentException("Prime input must be a non-empty array of integers");
        }
        return numbers.stream()
                .filter(this::isPrime)
                .collect(Collectors.toList());
    }

    /**
     * Computes the Least Common Multiple of all integers in the list.
     *
     * @param numbers list of integers
     * @return LCM value
     * @throws IllegalArgumentException if list is null, empty, or contains non-positive values
     */
    public long computeLcm(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            throw new IllegalArgumentException("LCM input must be a non-empty array of integers");
        }
        return numbers.stream()
                .mapToLong(Integer::longValue)
                .peek(v -> {
                    if (v <= 0) throw new IllegalArgumentException("LCM values must be positive integers, got: " + v);
                })
                .reduce(1L, this::lcm);
    }

    /**
     * Computes the Highest Common Factor (GCD) of all integers in the list.
     *
     * @param numbers list of integers
     * @return HCF value
     * @throws IllegalArgumentException if list is null, empty, or contains non-positive values
     */
    public long computeHcf(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            throw new IllegalArgumentException("HCF input must be a non-empty array of integers");
        }
        return numbers.stream()
                .mapToLong(Integer::longValue)
                .peek(v -> {
                    if (v <= 0) throw new IllegalArgumentException("HCF values must be positive integers, got: " + v);
                })
                .reduce(0L, this::gcd);
    }

    // ─── Private helpers ───

    private boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2 || n == 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; (long) i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }

    private long gcd(long a, long b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    private long lcm(long a, long b) {
        if (a == 0 || b == 0) return 0;
        return Math.abs(a / gcd(a, b) * b);
    }
}
