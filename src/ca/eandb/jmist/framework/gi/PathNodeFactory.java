/*
 * Copyright (c) 2008 Bradley W. Kimmel
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package ca.eandb.jmist.framework.gi;

import ca.eandb.jmist.framework.Emitter;
import ca.eandb.jmist.framework.Intersection;
import ca.eandb.jmist.framework.Light;
import ca.eandb.jmist.framework.NearestIntersectionRecorder;
import ca.eandb.jmist.framework.Random;
import ca.eandb.jmist.framework.Scene;
import ca.eandb.jmist.framework.color.Color;
import ca.eandb.jmist.framework.color.ColorModel;
import ca.eandb.jmist.math.Point2;
import ca.eandb.jmist.math.Ray3;
import ca.eandb.jmist.math.Sphere;

/**
 * @author brad
 *
 */
public final class PathNodeFactory {

	private static final int DEFAULT_MAX_DEPTH = 10;

	private final Scene scene;

	private final Light light;

	private final Sphere sceneBoundingSphere;

	private final ColorModel colorModel;

	private final int maxDepth;

	/**
	 * @param scene
	 */
	private PathNodeFactory(Scene scene, ColorModel colorModel) {
		this(scene, colorModel, DEFAULT_MAX_DEPTH);
	}

	/**
	 * @param scene
	 */
	private PathNodeFactory(Scene scene, ColorModel colorModel, int maxDepth) {
		this.scene = scene;
		this.light = scene.getLight();
		this.sceneBoundingSphere = scene.boundingSphere();
		this.colorModel = colorModel;
		this.maxDepth = maxDepth;
	}

	public static PathNodeFactory create(Scene scene, ColorModel colorModel) {
		return new PathNodeFactory(scene, colorModel);
	}


	public LightNode sampleLight(Color sample, Random rnd) {
		Emitter emitter = light.sample(rnd);
		return new LightNode(emitter, sceneBoundingSphere, sample, this);
	}

	public EyeNode sampleEye(Point2 p, Color sample) {
		return new EyeNode(p, sample, this);
	}

	/* package */ ScatteringNode trace(Ray3 ray, Color power, PathNode parent) {
		if (parent.getDepth() < maxDepth) {
			Intersection x = NearestIntersectionRecorder.computeNearestIntersection(ray, scene.getRoot());
			if (x != null) {
				return new SurfaceNode(ray, x, power, parent);
			} else {
				return new BackgroundNode(ray.direction(), power, parent);
			}
		} else { // parent.getDepth() >= maxDepth
			return null;
		}
	}

	/* package */ Scene getScene() {
		return scene;
	}

	/* package */ ColorModel getColorModel() {
		return colorModel;
	}

}