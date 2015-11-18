/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webdao;

/**
 *
 * @author Chris Whiteley
 */
public class LockException extends RuntimeException {

    public LockException(String msg) {
        super(msg);
    }
}
