/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qrcodegen.tools;

/**
 *
 * @author Stefan Ganzer
 */
public class ExtendedIndexOutOfBoundsException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;
	private final int lowerBound;
	private final int upperBound;
	private final int index;

	/**
	 * Creates a new instance of
	 * <code>ExtendedIndexOutOfBoundsException</code>.
	 *
	 * @param lowerBound
	 * @param upperBound
	 * @param index
	 */
	public ExtendedIndexOutOfBoundsException(int lowerBound, int upperBound, int index) {
		super("Lower bound: " + lowerBound
				+ ", Upper bound: " + upperBound
				+ ", Index: " + index);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.index = index;
	}

	/**
	 *
	 * @param lowerBound
	 * @param upperBound
	 * @param index
	 * @param cause
	 */
	public ExtendedIndexOutOfBoundsException(int lowerBound, int upperBound, int index, Throwable cause) {
		super("Lower bound: " + lowerBound
				+ ", Upper bound: " + upperBound
				+ ", Index: " + index, cause);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.index = index;
	}

	public int getLowerBound() {
		return lowerBound;
	}

	public int getUpperBound() {
		return upperBound;
	}

	public int getIndex() {
		return index;
	}
}
