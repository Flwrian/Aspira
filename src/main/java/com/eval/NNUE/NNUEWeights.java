package com.eval.NNUE;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

public final class NNUEWeights {

    public final int hidden;
    public final short[][] w1;
    public final int[] b1;

    public final short[] w2;
    public final int b2;

    private NNUEWeights(
            int hidden,
            short[][] w1,
            int[] b1,
            short[] w2,
            int b2
    ) {
        this.hidden = hidden;
        this.w1 = w1;
        this.b1 = b1;
        this.w2 = w2;
        this.b2 = b2;
    }

    public NNUEWeights(int hidden) {
        this.hidden = hidden;
        this.w1 = new short[FeatureMapper.FEATURES][hidden];
        this.b1 = new int[hidden];
        this.w2 = new short[hidden];
        this.b2 = 0;
    }

    public static NNUEWeights createDummy(int hidden) {
        int features = FeatureMapper.FEATURES;

        short[][] w1 = new short[features][hidden];
        int[] b1 = new int[hidden];
        short[] w2 = new short[hidden];

        // Génération déterministe
        Random r = new Random(123456);

        for (int f = 0; f < features; f++) {
            for (int i = 0; i < hidden; i++) {
                w1[f][i] = (short) (r.nextInt(7) - 3); // [-3..3]
            }
        }

        for (int i = 0; i < hidden; i++) {
            b1[i] = r.nextInt(11) - 5; // [-5..5]
            w2[i] = (short) (r.nextInt(7) - 3);
        }

        int b2 = r.nextInt(21) - 10;

        return new NNUEWeights(hidden, w1, b1, w2, b2);
    }


    public static NNUEWeights load(String path) {
        try (DataInputStream in =
                     new DataInputStream(
                         new BufferedInputStream(
                             new FileInputStream(path)))) {

            int hidden = in.readInt();
            int features = in.readInt();

            System.out.println("NNUE header: hidden=" + hidden + " features=" + features);

            if (features != FeatureMapper.FEATURES) {
                throw new IllegalStateException(
                        "Invalid feature count: " + features);
            }

            short[][] w1 = new short[features][hidden];
            int[] b1 = new int[hidden];

            // read w1
            for (int f = 0; f < features; f++) {
                for (int i = 0; i < hidden; i++) {
                    w1[f][i] = in.readShort();
                }
            }

            // read b1
            for (int i = 0; i < hidden; i++) {
                b1[i] = in.readInt();
            }

            // read output layer
            short[] w2 = new short[hidden];
            for (int i = 0; i < hidden; i++) {
                w2[i] = in.readShort();
            }

            int b2 = in.readInt();

            return new NNUEWeights(hidden, w1, b1, w2, b2);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load NNUE weights", e);
        }
    }
}
