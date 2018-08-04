/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qrcodegen.modules.vcardgenpanel;

/**
 *
 * @author Stefan Ganzer
 */
public class IllegalInputException extends IllegalArgumentException {

	/**
	 * Creates a new instance of
	 * <code>IllegalInputException</code> without detail message.
	 */
	public IllegalInputException() {
	}

	/**
	 * Constructs an instance of
	 * <code>IllegalInputException</code> with the specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public IllegalInputException(String msg) {
		super(msg);
	}
	
	public IllegalInputException(String msg, Throwable cause){
		super(msg, cause);
	}
	
	public IllegalInputException(Throwable cause){
		super(cause);
	}
}
