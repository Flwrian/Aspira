package com.eval.NNUE;

public final class FeatureMapper {

    public static final int FEATURES = 768;

    private FeatureMapper() {}

    public static int featureIndex(int color01, int pieceType0to5, int square0to63) {
        return color01 * 384 + pieceType0to5 * 64 + square0to63;
    }
}