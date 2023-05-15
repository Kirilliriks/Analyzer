package me.kirillirik.feist;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

public final class DESUtils {

    public static String binToUTF(String bin) {
        final var ciphertextBytes = new byte[bin.length() / 8];

        for (int j = 0; j < ciphertextBytes.length; j++) {
            final String temp = bin.substring(0, 8);
            final byte b = (byte) Integer.parseInt(temp, 2);
            ciphertextBytes[j] = b;
            bin = bin.substring(8);
        }

        return new String(ciphertextBytes, StandardCharsets.UTF_8).trim();
    }

    public static String utfToBin(String utf) {
        final byte[] bytes = utf.getBytes(StandardCharsets.UTF_8);

        final var bin = new StringBuilder();
        for (int aByte : bytes) {
            int value = aByte;

            for (int j = 0; j < 8; j++) {
                bin.append((value & 128) == 0 ? 0 : 1);
                value <<= 1;
            }
        }
        return bin.toString();
    }

    public static String XOR(String left, String right) {
        final var result = new StringBuilder();
        for (int i = 0; i < left.length(); i++) {
            result.append(left.charAt(i) ^ right.charAt(i));
        }
        return result.toString();
    }

    /**
     * Feistel function in DES algorithm specified in FIPS Pub 46
     *
     * @param mi  : String - 32-bit message binary string
     * @param key : String - 48-bit key binary string
     * @return 32-bit output string
     */
    public static String feistelFunction(String mi, String key) {
        final var gMi = new StringBuilder();
        for (final int value : DESMatrix.G) { // Expansion function g (named E in fips pub 46)
            gMi.append(mi.charAt(value - 1));
        }

        final String xorResult = XOR(gMi.toString(), key);
        var bin = new StringBuilder(xorResult);
        while (bin.length() < 48) { // Making sure the string is 48 bits
            bin.insert(0, "0");
        }

        final var sin = new String[8];
        for (int i = 0; i < 8; i++) { // Split into eight 6-bit strings
            sin[i] = bin.substring(0, 6);
            bin = new StringBuilder(bin.substring(6));
        }

        final var sout = new String[8];
        for (int i = 0; i < 8; i++) { // Do S-Box calculations
            final int[][] curS = DESMatrix.S[i];
            final String cur = sin[i];

            // Get binary values
            final int row = Integer.parseInt(cur.charAt(0) + String.valueOf(cur.charAt(5)), 2);
            final int col = Integer.parseInt(cur.substring(1, 5), 2);

            sout[i] = Integer.toBinaryString(curS[row][col]); // Do S-Box table lookup

            while (sout[i].length() < 4) { // Make sure the string is 4 bits
                sout[i] = "0" + sout[i];
            }
        }

        final var merged = StringUtils.join(sout);
        final var mergedPermuted = new StringBuilder();
        for (int j : DESMatrix.P) {  // Apply Permutation P
            mergedPermuted.append(merged.charAt(j - 1));
        }

        return mergedPermuted.toString();
    }

    /**
     * Hash Function from user <b>sfussenegger</b> on stackoverflow
     * <a href="http://stackoverflow.com/questions/1660501/what-is-a-good-64bit-hash-function-in-java-for-textual-strings">...</a>
     *
     * @param string : String to hash
     * @return 64-bit long hash value
     */
    public static long hash(String string) {
        long h = 1125899906842597L; // prime;

        for (int i = 0; i < string.length(); i++) {
            h = 31 * h + string.charAt(i);
        }
        return h;
    }
}
