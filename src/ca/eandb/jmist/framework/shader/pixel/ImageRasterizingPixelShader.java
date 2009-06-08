/**
 *
 */
package ca.eandb.jmist.framework.shader.pixel;

import ca.eandb.jmist.math.Point2;
import ca.eandb.jmist.framework.ImageShader;
import ca.eandb.jmist.framework.PixelShader;
import ca.eandb.jmist.framework.color.Color;

/**
 * Represents a pixel shader that rasterizes an image represented by
 * an image shader.  The inheriting pixel shader's sole responsibility
 * will be anti-aliasing.
 * @author Brad Kimmel
 */
public abstract class ImageRasterizingPixelShader implements PixelShader {

	/**
	 * Initializes the image shader to use for this pixel shader.
	 * @param shader The image shader to use for this pixel shader.
	 */
	protected ImageRasterizingPixelShader(ImageShader shader) {
		this.shader = shader;
	}

	/**
	 * Shades the specified pixel using this shader's image shader.
	 * @param p The point on the image plane to shade.
	 * @return The shaded pixel.
	 */
	protected Color shadeAt(Point2 p) {
		return this.shader.shadeAt(p);
	}

	/** The image shader to use for shading points. */
	private final ImageShader shader;

}