package info.benallen;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Ben Allen on 20/01/2016.
 */
public class RabinKarpFloats {
    private float[] pat;      // the pattern  // needed only for Las Vegas
    private float patHash;    // pattern hash value
    private int M;           // pattern length
    private long Q;          // a large prime, small enough to avoid long overflow
    private int R;           // radix
    private long RM;         // R^(M-1) % Q

    /**
     * Preprocesses the pattern string.
     *
     * @param pattern the pattern string
     * @param R the alphabet size
     */
    public RabinKarpFloats(char[] pattern, int R) {
        throw new UnsupportedOperationException("Operation not supported yet");
    }

    /**
     * Preprocesses the pattern string.
     *
     * @param pat the pattern string
     */
    public RabinKarpFloats(float[] pat) {
        this.pat = pat;      // save pattern (needed only for Las Vegas)
        R = 256;
        M = pat.length;
        Q = longRandomPrime();

        // precompute R^(M-1) % Q for use in removing leading digit
        RM = 1;
        for (int i = 1; i <= M-1; i++)
            RM = (R * RM) % Q;
        patHash = hash(pat, M);
    }

    // Compute hash for key[0..M-1].
    private float hash(float[] key, int M) {
        float h = 0;
        for (int j = 0; j < M; j++)
            h = (R * h + key[j]) % Q;
        return h;
    }

    // Las Vegas version: does pat[] match txt[i..i-M+1] ?
    private boolean check(float[] txt, int i) {
        for (int j = 0; j < M; j++)
            if (pat[j] != txt[i + j])
                return false;
        return true;
    }

    // Monte Carlo version: always return true
    private boolean check(int i) {
        return true;
    }

    /**
     * Returns the index of the first occurrrence of the pattern string
     * in the text string.
     *
     * @param  txt the text string
     * @return the index of the first occurrence of the pattern string
     *         in the text string; N if no such match
     */
    public int search(float[] txt) {
        int N = txt.length;
        if (N < M) return N;
        float txtHash = hash(txt, M);

        // check for match at offset 0
        if ((patHash == txtHash) && check(txt, 0))
            return 0;

        // check for hash match; if hash match, check for exact match
        for (int i = M; i < N; i++) {
            // Remove leading digit, add trailing digit, check for match.
            txtHash = (txtHash + Q - RM*txt[i-M] % Q) % Q;
            txtHash = (txtHash*R + txt[i]) % Q;

            // match
            int offset = i - M + 1;
            if ((patHash == txtHash) && check(txt, offset))
                return offset;
        }

        // no match
        return N;
    }


    // a random 31-bit prime
    private static long longRandomPrime() {
        BigInteger prime = BigInteger.probablePrime(31, new Random());
        return prime.longValue();
    }

    /**
     * Takes a pattern string and an input string as command-line arguments;
     * searches for the pattern string in the text string; and prints
     * the first occurrence of the pattern string in the text string.
     */
    public static int main(float[] pat, float[] all) {

        RabinKarpFloats searcher = new RabinKarpFloats(pat);
        int offset = searcher.search(all);

        // print results

        // from brute force search method 1
        return offset;
    }
}