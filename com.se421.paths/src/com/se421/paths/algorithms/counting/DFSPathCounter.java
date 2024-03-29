package com.se421.paths.algorithms.counting;

import java.util.Stack;

import com.ensoftcorp.atlas.core.db.graph.Edge;
import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.query.Q;
import com.ensoftcorp.atlas.core.script.Common;
import com.ensoftcorp.atlas.core.xcsg.XCSG;
import com.se421.paths.algorithms.PathCounter;

/**
 * This program counts all paths in a CFG by counting each path individually.
 * This implementation starts are the root and searches for leaves.
 * 
 * @author Ben Holland
 */
public class DFSPathCounter extends PathCounter {
	
	public DFSPathCounter() {}

	/**
	 * Counts the number of paths in a given CFG
	 * 
	 * Example Atlas Shell Usage:
	 * var dskqopt = functions("dskqopt")
	 * var dskqoptCFG = cfg(dskqopt)
	 * DFSPathCounter.countPaths(dskqoptCFG)
	 * 
	 * @param cfg
	 * @return
	 */
	public CountingResult countPaths(Q cfg) {
		// the total number of paths discovered
		// and the number of additions required to count the path
		long numPaths = 0;
		long additions = 0;

		// remove back edges to break cycles due to loops
		AtlasSet<Edge> backEdges = cfg.edges(XCSG.ControlFlowBackEdge).eval().edges();
		
		// create a directed acyclic graph (DAG) by remove the back edges
		Q dag = cfg.differenceEdges(Common.toQ(backEdges));
		
		// the roots and leaves of the DAG
		AtlasSet<Node> dagLeaves = dag.leaves().eval().nodes();
		Node dagRoot = dag.roots().eval().nodes().one();

		// handle some trivial edge cases
		if(dagRoot == null) {
			// function is empty, there are no paths
			return new CountingResult(0L,0L);
		} else if(dagLeaves.contains(dagRoot)) {
			// function contains a single node there must be 1 path
			return new CountingResult(0L,1L);
		}
		
		// stack for depth first search (DFS)
		Stack<Node> stack = new Stack<Node>();
		
		// start searching from the root
		stack.push(dagRoot);
		
		// depth first search on directed acyclic graph
		while (!stack.isEmpty()) {
			// next node to process
			Node currentNode = stack.pop();
			
			// get the children of the current node
			// note: we iterate by edge in case there are multiple edges from a predecessor to a successor
			for (Edge outgoingEdge : dag.forwardStep(Common.toQ(currentNode)).eval().edges()) {
				Node successor = outgoingEdge.to();
				if(dagLeaves.contains(successor)) {
					// if we reached a leaf increment the counter by 1
					numPaths++;
					additions++;
				} else {
					// push the child node on the stack to be processed
					stack.push(successor);
				}
			}
		}
		
		// at the end, we have traversed all paths once, so return the count
		return new CountingResult(additions, numPaths);
	}
	
}
