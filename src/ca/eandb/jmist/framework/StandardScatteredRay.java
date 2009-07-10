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

package ca.eandb.jmist.framework;

import ca.eandb.jmist.framework.color.Color;
import ca.eandb.jmist.math.Ray3;

/**
 * @author brad
 *
 */
public final class StandardScatteredRay extends ScatteredRay {

	private final Ray3 scatteredRay;
	private final Color color;
	private final Type type;
	private final boolean transmitted;

	public StandardScatteredRay(Ray3 scatteredRay, Color color, Type type, boolean transmitted) {

		assert(scatteredRay != null);

		this.scatteredRay = scatteredRay;
		this.color = color;
		this.transmitted = transmitted;
		this.type = type;

	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.ScatteredRay#getRay()
	 */
	public final Ray3 getRay() {
		return scatteredRay;
	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.ScatteredRay#getColor()
	 */
	public final Color getColor() {
		return color;
	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.ScatteredRay#getType()
	 */
	public final Type getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.ScatteredRay#isTransmitted()
	 */
	public final boolean isTransmitted() {
		return transmitted;
	}

}