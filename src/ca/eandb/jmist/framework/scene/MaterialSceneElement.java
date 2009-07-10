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

package ca.eandb.jmist.framework.scene;

import ca.eandb.jmist.framework.Emitter;
import ca.eandb.jmist.framework.Illuminable;
import ca.eandb.jmist.framework.Light;
import ca.eandb.jmist.framework.LightSample;
import ca.eandb.jmist.framework.Material;
import ca.eandb.jmist.framework.Modifier;
import ca.eandb.jmist.framework.Random;
import ca.eandb.jmist.framework.SceneElement;
import ca.eandb.jmist.framework.ShadingContext;
import ca.eandb.jmist.framework.SurfacePoint;
import ca.eandb.jmist.framework.color.Color;
import ca.eandb.jmist.framework.color.WavelengthPacket;
import ca.eandb.jmist.framework.light.AbstractLight;
import ca.eandb.jmist.framework.light.PointLightSample;
import ca.eandb.jmist.framework.light.ScaledEmitter;
import ca.eandb.jmist.framework.light.SurfaceEmitter;
import ca.eandb.jmist.framework.shader.MinimalShadingContext;
import ca.eandb.jmist.math.Point3;
import ca.eandb.jmist.math.Vector3;

/**
 * @author brad
 *
 */
public final class MaterialSceneElement extends ModifierSceneElement {

	private final Material material;

	public MaterialSceneElement(Material material, SceneElement inner) {
		super(new MaterialModifier(material), inner);
		this.material = material;
	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.scene.SceneElementDecorator#createLight()
	 */
	@Override
	public Light createLight() {
		if (!material.isEmissive()) {
			return null;
		}

		final double surfaceArea = getSurfaceArea();

		return new AbstractLight() {

			@Override
			public void illuminate(SurfacePoint x, WavelengthPacket lambda, Random rng, Illuminable target) {

				ShadingContext context = new MinimalShadingContext(rng);
				generateImportanceSampledSurfacePoint(x, context);
				context.getModifier().modify(context);

				Point3 p = context.getPosition();
				Material mat = material;//context.getMaterial();
				Vector3 v = x.getPosition().unitVectorFrom(p);
				Vector3 n = context.getShadingNormal();
				double d2 = x.getPosition().squaredDistanceTo(p);
				double atten = Math.max(n.dot(v), 0.0) * surfaceArea / (4.0 * Math.PI * d2);
				Color ri = mat.emission(context, v, lambda).times(atten);

				LightSample sample = new PointLightSample(x, p, ri);

				target.addLightSample(sample);

			}

			@Override
			public Emitter sample(Random rng) {
				ShadingContext context = new MinimalShadingContext(rng);
				generateRandomSurfacePoint(context);
				context.getModifier().modify(context);

				return new ScaledEmitter(surfaceArea, new SurfaceEmitter(context));
			}

		};
	}

//	/* (non-Javadoc)
//	 * @see ca.eandb.jmist.framework.scene.SceneElementDecorator#isEmissive()
//	 */
//	@Override
//	public boolean isEmissive() {
//		return material.isEmissive();
//	}

	private static final class MaterialModifier implements Modifier {

		private final Material material;

		/**
		 * @param material
		 */
		public MaterialModifier(Material material) {
			this.material = material;
		}

		@Override
		public void modify(ShadingContext context) {
			context.setMaterial(material);
		}

	}

}