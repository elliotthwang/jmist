/**
 *
 */
package ca.eandb.jmist.framework.pdf;

import ca.eandb.jmist.framework.ProbabilityDensityFunction;
import ca.eandb.jmist.framework.Random;
import ca.eandb.jmist.framework.random.RandomUtil;
import ca.eandb.jmist.util.ArrayUtil;

/**
 * An abstract <code>ProbabilityDensityFunction</code> that provides default
 * implementations for some methods.
 * @author Brad Kimmel
 */
public abstract class AbstractProbabilityDensityFunction implements
		ProbabilityDensityFunction {

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.ProbabilityDensityFunction#sample(ca.eandb.jmist.framework.Random)
	 */
	public double sample(Random random) {
		return this.warp(RandomUtil.canonical(random));
	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.ProbabilityDensityFunction#evaluate(double[], double[])
	 */
	public double[] evaluate(double[] x, double[] results) {

		results = ArrayUtil.initialize(results, x.length);

		for (int i = 0; i < results.length; i++) {
			results[i] = this.evaluate(x[i]);
		}

		return results;

	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.ProbabilityDensityFunction#sample(ca.eandb.jmist.framework.Random, double[])
	 */
	public double[] sample(Random random, double[] results) {

		for (int i = 0; i < results.length; i++) {
			results[i] = this.sample(random);
		}

		return results;

	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.ProbabilityDensityFunction#warp(double[], double[])
	 */
	public double[] warp(double[] seeds, double[] results) {

		if (results == null) {
			results = new double[seeds.length];
		} else if (results.length != seeds.length) {
			throw new IllegalArgumentException("results.length != seeds.length");
		}

		for (int i = 0; i < results.length; i++) {
			results[i] = this.warp(seeds[i]);
		}

		return results;

	}

}
