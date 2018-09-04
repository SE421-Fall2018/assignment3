/**
 * 
 */
package com.se421.paths.regression.test.cases;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.script.CommonQueries;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.se421.paths.algorithms.PathCounter.CountingResult;
import com.se421.paths.algorithms.counting.DFSPathCounter;
import com.se421.paths.algorithms.enumeration.DFSPathEnumerator;
import com.se421.paths.regression.Activator;
import com.se421.paths.regression.RegressionTest;

/**
 * Checks that all path counting algorithms agree on the path counting results for all functions in the Xinu operating system.
 * 
 * Execute this class with Run As -> JUnit Plug-in Test
 * 
 * @author Ben Holland
 */
public class XinuPathCountingTests {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RegressionTest.setUpBeforeClass(Activator.getDefault().getBundle(), "/projects/Xinu.zip", "Xinu");
	}

	@Before
	public void setUp() throws Exception {}

	@After
	public void tearDown() throws Exception {}

	/**
	 * This is really just a sanity check that the index exists and correct project was loaded
	 */
	@Test
	public void testExpectedFunctionExists() {
		if(Common.functions("dswrite").eval().nodes().isEmpty()) {
			fail("Unable to locate expected Xinu function.");
		}
	}

	/**
	 * Check that all algorithms agree on the number of paths for each Xinu function
	 */
	@Test
	public void testAlgorithmsAgree() {
		Q functions = Common.universe().nodesTaggedWithAny(XCSG.Function);

		// sort functions alphabetically (for consistency)
		ArrayList<Node> sortedFunctions = new ArrayList<Node>();
		for (Node function : functions.eval().nodes()) {
			sortedFunctions.add(function);
		}
		Collections.sort(sortedFunctions, new Comparator<Node>() {
			@Override
			public int compare(Node n1, Node n2) {
				return n1.getAttr(XCSG.name).toString().compareTo(n2.getAttr(XCSG.name).toString());
			}
		});

		for (Node function : sortedFunctions) {
			Q cfg = CommonQueries.cfg(Common.toQ(function));
			
			Set<Long> uniquePathCounts = new HashSet<Long>();
			
			try {
				CountingResult dfsPathCounter = new DFSPathCounter().countPaths(cfg);
				uniquePathCounts.add(dfsPathCounter.getPaths());
			} catch (Throwable t) {
				throw new RuntimeException("Error running DFSPathCounter on Xinu function: " + function.getAttr(XCSG.name), t);
			}
			
			try {
				CountingResult dfsPathEnumerator = new DFSPathEnumerator().countPaths(cfg);
				uniquePathCounts.add(dfsPathEnumerator.getPaths());
			} catch (Throwable t) {
				throw new RuntimeException("Error running DFSPathEnumerator on Xinu function: " + function.getAttr(XCSG.name), t);
			}
			
			if(uniquePathCounts.size() != 1) {
				fail("Path counting algorithms disagreed on path counts for Xinu function: " + function.getAttr(XCSG.name));
			}
		}
	}

}
