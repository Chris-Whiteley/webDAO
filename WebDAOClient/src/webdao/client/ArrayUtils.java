/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webdao.client;

import java.lang.reflect.Array;

/**
 * Class to provide static Array handling utility methods
 *
 * @author y125070
 */
public class ArrayUtils {

    /**
     * Array concatenation
     * @param <T> type of the array
     * @param A 
     * @param B
     * @return Array consisting of A concatenated with B
     */
    public static <T> T[] concatenate(T[] A, T[] B) {
        int aLen = A.length;
        int bLen = B.length;

        @SuppressWarnings("unchecked")
        T[] C = (T[]) Array.newInstance(A.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);

        return C;
    }

    public static <T> T[] concatenate(T A, T[] B) {
        Object[] arrA = new Object[1];
        arrA[0] = A;
        return (T[]) concatenate(arrA, B);        
    }

}
