/**
 *
 */
package framework.core;

/**
 * A 3x3 matrix for applying three dimensional linear transformations.
 * This class is immutable.
 * @author brad
 */
public final class LinearMatrix3 {

	/**
	 * Initializes the matrix from its elements.
	 * @param _00
	 * @param _01
	 * @param _02
	 * @param _10
	 * @param _11
	 * @param _12
	 * @param _20
	 * @param _21
	 * @param _22
	 */
	public LinearMatrix3(
			double _00, double _01, double _02,
			double _10, double _11, double _12,
			double _20, double _21, double _22
			) {
		this.a[0][0] = _00; this.a[0][1] = _01; this.a[0][2] = _02;
		this.a[1][0] = _10; this.a[1][1] = _11; this.a[1][2] = _12;
		this.a[2][0] = _20; this.a[2][1] = _21; this.a[2][2] = _22;
	}

	/**
	 * Adds this matrix to another matrix.
	 * @param other The matrix to add to this matrix.
	 * @return The sum of this matrix and other.
	 */
	public LinearMatrix3 plus(LinearMatrix3 other) {
		return new LinearMatrix3(
				a[0][0] + other.a[0][0], a[0][1] + other.a[0][1], a[0][2] + other.a[0][2],
				a[1][0] + other.a[1][0], a[1][1] + other.a[1][1], a[1][2] + other.a[1][2],
				a[2][0] + other.a[2][0], a[2][1] + other.a[2][1], a[2][2] + other.a[2][2]
		);
	}

	/**
	 * Subtracts another matrix from this one.
	 * @param other The matrix to subtract from this one.
	 * @return The difference between this matrix and other.
	 */
	public LinearMatrix3 minus(LinearMatrix3 other) {
		return new LinearMatrix3(
				a[0][0] - other.a[0][0], a[0][1] - other.a[0][1], a[0][2] - other.a[0][2],
				a[1][0] - other.a[1][0], a[1][1] - other.a[1][1], a[1][2] - other.a[1][2],
				a[2][0] - other.a[2][0], a[2][1] - other.a[2][1], a[2][2] - other.a[2][2]
		);
	}

	/**
	 * Computes the product of this matrix with another.
	 * @param other The matrix by which to multiply this matrix.
	 * @return The product of this matrix and other.
	 */
	public LinearMatrix3 times(LinearMatrix3 other) {
		return new LinearMatrix3(
				a[0][0] * other.a[0][0] + a[0][1] * other.a[1][0] + a[0][2] * other.a[2][0],
				a[0][0] * other.a[0][1] + a[0][1] * other.a[1][1] + a[0][2] * other.a[2][1],
				a[0][0] * other.a[0][2] + a[0][1] * other.a[1][2] + a[0][2] * other.a[2][2],
				a[1][0] * other.a[0][0] + a[1][1] * other.a[1][0] + a[1][2] * other.a[2][0],
				a[1][0] * other.a[0][1] + a[1][1] * other.a[1][1] + a[1][2] * other.a[2][1],
				a[1][0] * other.a[0][2] + a[1][1] * other.a[1][2] + a[1][2] * other.a[2][2],
				a[2][0] * other.a[0][0] + a[2][1] * other.a[1][0] + a[2][2] * other.a[2][0],
				a[2][0] * other.a[0][1] + a[2][1] * other.a[1][1] + a[2][2] * other.a[2][1],
				a[2][0] * other.a[0][2] + a[2][1] * other.a[1][2] + a[2][2] * other.a[2][2]
		);
	}

	/**
	 * Multiplies this matrix by the inverse of the specified matrix.
	 * @param other The matrix by which to divide this matrix.
	 * @return The product of this matrix and the inverse of other.
	 * @see inverse
	 */
	public LinearMatrix3 divide(LinearMatrix3 other) {
		return this.times(other.inverse());
	}

	/**
	 * Computes the negative of this matrix.
	 * @return The negation of this matrix.
	 */
	public LinearMatrix3 negative() {
		return new LinearMatrix3(
				-a[0][0], -a[0][1], -a[0][2],
				-a[1][0], -a[1][1], -a[1][2],
				-a[2][0], -a[2][1], -a[2][2]
		);
	}

	/**
	 * Computes the inverse of this matrix.
	 * The product of {@code this} and {@code this.inverse()} will be
	 * the identity matrix.
	 * @return The inverse of this matrix.
	 */
	public LinearMatrix3 inverse() {
		double det = determinant();

		return new LinearMatrix3(
				(a[1][1] * a[2][2] - a[1][2] * a[2][1]) / det, (a[0][1] * a[2][2] - a[0][2] * a[2][1]) / det, (a[0][1] * a[1][2] - a[0][2] * a[1][1]) / det,
				(a[1][2] * a[2][0] - a[1][0] * a[2][2]) / det, (a[0][0] * a[2][2] - a[0][2] * a[2][0]) / det, (a[0][2] * a[1][0] - a[0][0] * a[1][2]) / det,
				(a[1][0] * a[2][1] - a[1][1] * a[2][0]) / det, (a[0][1] * a[2][0] - a[0][0] * a[2][1]) / det, (a[0][0] * a[1][1] - a[0][1] * a[1][0]) / det
		);
	}

	/**
	 * Computes the transpose of this matrix.
	 * @return The transpose of this matrix.
	 */
	public LinearMatrix3 transposed() {
		return new LinearMatrix3(
				a[0][0], a[1][0], a[2][0],
				a[0][1], a[1][1], a[2][1],
				a[0][2], a[1][2], a[2][2]
		);
	}

	/**
	 * Finds the determinant of this matrix (i.e., the volume of the
	 * unit cube after the transformation represented by this matrix
	 * is applied).
	 * @return The determinant of this matrix.
	 */
	public double determinant() {
		return a[0][0] * (a[1][1] * a[2][2] - a[1][2] * a[2][1]) +
		       a[0][1] * (a[1][2] * a[2][0] - a[1][0] * a[2][2]) +
		       a[0][2] * (a[1][0] * a[2][1] - a[1][1] * a[2][0]);
	}

	/**
	 * Gets an element of the matrix.
	 * @param row The row containing the element to get (0 <= row < 3).
	 * @param col The column containing the element to get (0 <= col < 3).
	 * @return The value of the element at the specified position.
	 * @see getElement
	 */
	public double at(int row, int col) {
		return a[row][col];
	}

	/**
	 * Gets an element of the matrix.
	 * @param row The row containing the element to get (0 <= row < 3).
	 * @param col The column containing the element to get (0 <= col < 3).
	 * @return The value of the element at the specified position.
	 * @see at
	 */
	public double getElement(int row, int col) {
		return a[row][col];
	}

	/**
	 * The identity matrix ({@code this * IDENTITY == this}).
	 */
	public static final LinearMatrix3 IDENTITY = new LinearMatrix3(1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0);

	/**
	 * The zero matrix ({@code this + IDENTITY == this}).
	 */
	public static final LinearMatrix3 ZERO = new LinearMatrix3(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

	/** The elements of the matrix. */
	private final double[][] a = new double[3][3];

}
