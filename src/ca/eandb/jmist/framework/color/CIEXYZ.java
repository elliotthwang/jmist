/**
 *
 */
package ca.eandb.jmist.framework.color;

import ca.eandb.jmist.math.MathUtil;
import ca.eandb.jmist.math.Tuple3;

/**
 * @author Brad
 *
 */
public class CIEXYZ extends Tuple3 {

	/** Serialization version ID. */
	private static final long serialVersionUID = -3369371416389668782L;

	public static final CIEXYZ ZERO = new CIEXYZ(0.0, 0.0, 0.0);
	public static final CIEXYZ A = new CIEXYZ(1.09850, 1.00000, 0.35585);
	public static final CIEXYZ B = new CIEXYZ(0.99072, 1.00000, 0.85223);
	public static final CIEXYZ C = new CIEXYZ(0.98074, 1.00000, 1.18232);
	public static final CIEXYZ D50 = new CIEXYZ(0.96422, 1.00000, 0.82521);
	public static final CIEXYZ D55 = new CIEXYZ(0.95682, 1.00000, 0.92149);
	public static final CIEXYZ D65 = new CIEXYZ(0.95047, 1.00000, 1.08883);
	public static final CIEXYZ D75 = new CIEXYZ(0.94972, 1.00000, 1.22638);
	public static final CIEXYZ E = new CIEXYZ(1.00000, 1.00000, 1.00000);
	public static final CIEXYZ F2 = new CIEXYZ(0.99186, 1.00000, 0.67393);
	public static final CIEXYZ F7 = new CIEXYZ(0.95041, 1.00000, 1.08747);
	public static final CIEXYZ F11 = new CIEXYZ(1.00962, 1.00000, 0.64350);

	/**
	 * @param X
	 * @param Y
	 * @param Z
	 */
	public CIEXYZ(double X, double Y, double Z) {
		super(X, Y, Z);
	}

	public final double X() {
		return x;
	}

	public final double Y() {
		return y;
	}

	public final double Z() {
		return z;
	}

	public final CIEXYZ plus(CIEXYZ other) {
		return new CIEXYZ(x + other.x, y + other.y, z + other.z);
	}

	public final CIEXYZ minus(CIEXYZ other) {
		return new CIEXYZ(x - other.x, y - other.y, z - other.z);
	}

	public final CIEXYZ divide(CIEXYZ other) {
		return new CIEXYZ(x / other.x, y / other.y, z / other.z);
	}

	public final CIEXYZ divide(double c) {
		return new CIEXYZ(x / c, y / c, z / c);
	}

	public final CIEXYZ times(CIEXYZ other) {
		return new CIEXYZ(x * other.x, y * other.y, z * other.z);
	}

	public final CIEXYZ times(double c) {
		return new CIEXYZ(x * c, y * c, z * c);
	}

	public final CIEXYZ clamp(double max) {
		return clamp(0.0, max);
	}

	public final CIEXYZ clamp(double min, double max) {
		return new CIEXYZ(
				MathUtil.threshold(x, min, max),
				MathUtil.threshold(y, min, max),
				MathUtil.threshold(y, min, max));
	}

	public final CIEXYZ adapt(CIEXYZ fromRef, CIEXYZ toRef) {
		return CIELab.fromXYZ(this, fromRef).toXYZ(toRef);
	}

	public final RGB toRGB() {
		return ColorUtil.convertXYZ2RGB(this);
	}

	public static final CIEXYZ fromRGB(double r, double g, double b) {
		return ColorUtil.convertRGB2XYZ(r, g, b);
	}

	public static final CIEXYZ fromRGB(RGB rgb) {
		return ColorUtil.convertRGB2XYZ(rgb);
	}

}
