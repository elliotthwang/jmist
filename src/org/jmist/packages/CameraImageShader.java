/**
 *
 */
package org.jmist.packages;

import org.jmist.framework.IImageShader;
import org.jmist.framework.ILens;
import org.jmist.framework.IRayShader;
import org.jmist.toolkit.Pixel;
import org.jmist.toolkit.Point2;
import org.jmist.toolkit.Ray3;

/**
 * An image shader that uses an ILens to shade rays corresponding to points
 * on the image plane.
 * @author bkimmel
 */
public final class CameraImageShader implements IImageShader {

	/**
	 * Initializes the lens and ray shader to use to shade points on
	 * the image plane.
	 * @param lens The lens to use to generate rays corresponding to
	 * 		points on the image plane.
	 * @param rayShader The shader to use to shade rays.
	 */
	public CameraImageShader(ILens lens, IRayShader rayShader) {
		this.lens = lens;
		this.rayShader = rayShader;
	}

	/* (non-Javadoc)
	 * @see org.jmist.framework.IImageShader#shadeAt(org.jmist.toolkit.Point2, org.jmist.toolkit.Pixel)
	 */
	public void shadeAt(Point2 p, Pixel pixel) {
		
		Ray3 ray = this.lens.rayAt(p);
		
		if (ray != null) {
			this.rayShader.shadeRay(ray, pixel);
		} else {
			pixel.setAll(0.0);
		}
		
	}

	/* (non-Javadoc)
	 * @see org.jmist.framework.IPixelFactory#createPixel()
	 */
	public Pixel createPixel() {
		return this.rayShader.createPixel();
	}

	/**
	 * The lens to use to obtain rays corresponding to points on the
	 * image plane.
	 */
	private final ILens lens;

	/** The shader to use to shade rays. */
	private final IRayShader rayShader;

}
