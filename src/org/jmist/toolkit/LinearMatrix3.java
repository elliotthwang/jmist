/**
 *
 */
package org.jmist.toolkit;

import java.io.Serializable;

/**
 * A 4x4 matrix of the form
 * <table>
 * 		<tr><td>a</td><td>b</td><td>c</td><td>0</td></tr>
 * 		<tr><td>d</td><td>e</td><td>f</td><td>0</td></tr>
 * 		<tr><td>g</td><td>h</td><td>i</td><td>0</td></tr>
 * 		<tr><td>0</td><td>0</td><td>0</td><td>1</td></tr>
 * </table>
 * for applying three dimensional linear transformations.
 * This class is immutable.
 * @author brad
 */
public final class LinearMatrix3 implements Serializable {

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
	 * @see #inverse()
	 */
	public LinearMatrix3 divide(LinearMatrix3 other) {
		return this.times(other.inverse());
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
	 * @see #getElement(int, int)
	 */
	public double at(int row, int col) {
		return a[row][col];
	}

	/**
	 * Transforms the specified <code>Vector3</code> according to the
	 * transformation representing by this <code>LinearMatrix3</code>.
	 * @param v The <code>Vector3</code> to transform.
	 * @return The transformed <code>Vector3</code>.
	 */
	public Vector3 times(Vector3 v) {
		return new Vector3(
				a[0][0] * v.x() + a[0][1] * v.y() + a[0][2] * v.z(),
				a[1][0] * v.x() + a[1][1] * v.y() + a[1][2] * v.z(),
				a[2][0] * v.x() + a[2][1] * v.y() + a[2][2] * v.z()
		);
	}

	/**
	 * Returns a scaling matrix.
	 * @param c The factor by which to scale.
	 * @return A scaling matrix.
	 */
	public static LinearMatrix3 scaleMatrix(double c) {
		return new LinearMatrix3(
				c, 0.0, 0.0,
				0.0, c, 0.0,
				0.0, 0.0, c
		);
	}

	/**
	 * Returns a matrix for scaling by independent factors along
	 * each axis.
	 * @param cx The factor by which to scale along the x-axis.
	 * @param cy The factor by which to scale along the y-axis.
	 * @param cz The factor by which to scale along the z-axis.
	 * @return The stretch matrix.
	 */
	public static LinearMatrix3 stretchMatrix(double cx, double cy, double cz) {
		return new LinearMatrix3(
				cx, 0.0, 0.0,
				0.0, cy, 0.0,
				0.0, 0.0, cz
		);
	}

	/**
	 * Returns a matrix for stretching along the x-axis.
	 * @param cx The factor by which to scale along the x-axis.
	 * @return The stretch matrix.
	 */
	public static LinearMatrix3 stretchXMatrix(double cx) {
		return new LinearMatrix3(
				cx , 0.0, 0.0,
				0.0, 1.0, 0.0,
				0.0, 0.0, 1.0
		);
	}

	/**
	 * Returns a matrix for stretching along the y-axis.
	 * @param cy The factor by which to scale along the y-axis.
	 * @return The stretch matrix.
	 */
	public static LinearMatrix3 stretchYMatrix(double cy) {
		return new LinearMatrix3(
				1.0, 0.0, 0.0,
				0.0, cy , 0.0,
				0.0, 0.0, 1.0
		);
	}

	/**
	 * Returns a matrix for stretching along the z-axis.
	 * @param cz The factor by which to scale along the z-axis.
	 * @return The stretch matrix.
	 */
	public static LinearMatrix3 stretchZMatrix(double cz) {
		return new LinearMatrix3(
				1.0, 0.0, 0.0,
				0.0, 1.0, 0.0,
				0.0, 0.0, cz
		);
	}

	/**
	 * Returns a matrix for stretching along an arbitrary axis.
	 * @param axis The axis to stretch along.
	 * @param c The factor to stretch by.
	 * @return The stretch matrix.
	 */
	public static LinearMatrix3 stretchMatrix(Vector3 axis, double c) {
		// TODO implement this method.
		throw new UnsupportedOperationException("not yet implemented");
	}

	/**
	 * Returns a matrix for rotating about the x-axis.
	 * @param theta The angle to rotate by.
	 * @return The rotation matrix.
	 */
	public static LinearMatrix3 rotateXMatrix(double theta) {
		return new LinearMatrix3(
				1.0, 0.0, 0.0,
				0.0, Math.cos(theta), -Math.sin(theta),
				0.0, Math.sin(theta), Math.cos(theta)
		);
	}

	/**
	 * Returns a matrix for rotating about the y-axis.
	 * @param theta The angle to rotate by.
	 * @return The rotation matrix.
	 */
	public static LinearMatrix3 rotateYMatrix(double theta) {
		return new LinearMatrix3(
				Math.cos(theta), 0.0, Math.sin(theta),
				0.0, 1.0, 0.0,
				-Math.sin(theta), 0.0, Math.cos(theta)
		);
	}

	/**
	 * Returns a matrix for rotating about the z-axis.
	 * @param theta The angle to rotate by.
	 * @return The rotation matrix.
	 */
	public static LinearMatrix3 rotateZMatrix(double theta) {
		return new LinearMatrix3(
				Math.cos(theta), -Math.sin(theta), 0.0,
				Math.sin(theta), Math.cos(theta), 0.0,
				0.0, 0.0, 1.0
		);
	}

	/**
	 * Returns a matrix for rotating about an arbitrary axis.
	 * @param axis The axis to rotate around.
	 * @param theta The angle to rotate by.
	 * @return The rotation matrix.
	 */
	public static LinearMatrix3 rotateMatrix(Vector3 axis, double theta) {
		// TODO implement this method.
		throw new UnsupportedOperationException("not yet implemented");
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

	/**
	 * Serialization version ID.
	 */
	private static final long serialVersionUID = -2238797743118681949L;

}
