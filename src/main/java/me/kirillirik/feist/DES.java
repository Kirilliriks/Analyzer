package me.kirillirik.feist;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

// Repository used https://github.com/deadlytea/DES/
public final class DES {

    public static int BLOCK_LENGTH = 64;

    private final long[] keys = new long[17];

    public String crypt(String key, String inputBinary, boolean encrypt) {
        buildKeys(DESUtils.hash(key)); // Build the key schedule

        final var binaryText = new StringBuilder(inputBinary);
        final int remainder = binaryText.length() % BLOCK_LENGTH;
        if (remainder != 0) { // Add padding if necessary
            for (int i = 0; i < (BLOCK_LENGTH - remainder); i++) {
                binaryText.insert(0, "0");
            }
        }

        final var binaryBlocks = new String[binaryText.length() / BLOCK_LENGTH];
        for (int i = 0; i < binaryBlocks.length; i++) { // Separate binary plaintext into blocks
            binaryBlocks[i] = binaryText.substring(i * BLOCK_LENGTH, (i + 1) * BLOCK_LENGTH);
        }

        final var cipherBlocks = new String[binaryText.length() / BLOCK_LENGTH];
        for (int i = 0; i < cipherBlocks.length; i++) { // Encrypt the blocks
            final String bin = binaryBlocks[i];
            cipherBlocks[i] = cryptBlock(bin, encrypt);
        }

        Arrays.fill(keys, 0); // Clear keys

        return StringUtils.join(cipherBlocks);
    }

    public String cryptBlock(String plaintextBlock, boolean encrypt) {
        final int length = plaintextBlock.length();
        if (length != BLOCK_LENGTH) {
            throw new RuntimeException("Input block length is not " + BLOCK_LENGTH + " bits! It's is " + length);
        }

        final var out = new StringBuilder();
        for (final int j : DESMatrix.IP) {
            out.append(plaintextBlock.charAt(j - 1)); //Initial permutation
        }

        String mL = out.substring(0, BLOCK_LENGTH / 2);
        String mR = out.substring(BLOCK_LENGTH / 2);
        for (int i = 0; i < 16; i++) {
            final int index = (encrypt ? i + 1: 16 - i);

            final var curKey = new StringBuilder(Long.toBinaryString(keys[index])); // 48-bit current key
            while (curKey.length() < 48) {
                curKey.insert(0, "0");
            }

            final String fResult = DESUtils.feistelFunction(mR, curKey.toString()); // Get 32-bit result from f with m1 and ki
            final String xorResult = DESUtils.XOR(fResult, mL);
            final var m2String = new StringBuilder(xorResult);
            while (m2String.length() < BLOCK_LENGTH / 2) {
                m2String.insert(0, "0");
            }

            mL = mR;
            mR = m2String.toString();
        }

        final String in = mR + mL;
        final var output = new StringBuilder();
        for (final int j : DESMatrix.IPi) {
            output.append(in.charAt(j - 1));
        }

        return output.toString();
    }

    public void buildKeys(long key) {
        final var binKey = new StringBuilder(Long.toBinaryString(key)); // Convert long value to 64bit binary string
        while (binKey.length() < BLOCK_LENGTH) { // Add leading zeros if not at key length for ease of computations
            binKey.insert(0, "0");
        }

        final var binKey_PC1 = new StringBuilder(); // For the 56-bit permuted key
        for (final int k : DESMatrix.PC1) { // Apply Permuted Choice 1 (64 -> 56 bit)
            binKey_PC1.append(binKey.charAt(k - 1));
        }

        // Split permuted string in half | 56/2 = 28
        final String sL = binKey_PC1.substring(0, 28);
        final String sR = binKey_PC1.substring(28);

        // Parse binary strings into integers for shifting
        int iL = Integer.parseInt(sL, 2);
        int iR = Integer.parseInt(sR, 2);

        for (int i = 1; i < keys.length; i++) { // Build the keys (Start at index 1)
            // Perform left shifts according to key shift array
            iL = Integer.rotateLeft(iL, DESMatrix.KEY_SHIFTS[i]);
            iR = Integer.rotateLeft(iR, DESMatrix.KEY_SHIFTS[i]);

            final long merged = ((long) iL << 28) + iR; // Merge the two halves
            final var sMerged = new StringBuilder(Long.toBinaryString(merged)); // 56-bit merged
            while (sMerged.length() < 56) { // Fix length if leading zeros absent
                sMerged.insert(0, "0");
            }

            final var binKey_PC2 = new StringBuilder(); // For the 56-bit permuted key
            for (final int k : DESMatrix.PC2) { // Apply Permuted Choice 2 (56 -> 48 bit)
                binKey_PC2.append(sMerged.charAt(k - 1));
            }

            keys[i] = Long.parseLong(binKey_PC2.toString(), 2); // Set the 48-bit key
        }
    }

    public enum MODE {
        ECB,
        CBC,
        CFB,
        OFB
    }
}
