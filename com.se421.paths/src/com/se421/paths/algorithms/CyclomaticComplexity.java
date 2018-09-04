package com.se421.paths.algorithms;

import com.ensoftcorp.atlas.core.db.graph.Graph;

/**
 * An analyzer for cyclomatic complexity
 * 
 * @author Ben Holland
 */
public class CyclomaticComplexity {

	/**
	 * Computes the cyclomatic complexity of a function as defined by
	 * https://en.wikipedia.org/wiki/Cyclomatic_complexity
	 * 
	 * @return
	 */
	public static int cyclomaticComplexity(Graph cfg) {
		long nodesCount = cfg.nodes().size();
		if(nodesCount == 0) {
			return 0;
		} else {
			long edgesCount = cfg.edges().size();
			return (int) (edgesCount - nodesCount + 2);
		}
	}

}
