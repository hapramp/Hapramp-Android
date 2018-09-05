package com.hapramp.utils;

public class ReputationCalc {
  public static double calculateReputation(long raw) {
    double intermediateReputation = Math.log10(raw);
    intermediateReputation = (intermediateReputation == Double.NEGATIVE_INFINITY) ? 0 : intermediateReputation;
    intermediateReputation = Math.max(intermediateReputation - 9, 0);
    intermediateReputation = (intermediateReputation * 9) + 25;
    return intermediateReputation;
  }
}
