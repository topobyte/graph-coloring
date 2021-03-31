// Copyright 2015 Sebastian Kuerten
//
// This file is part of graph-coloring.
//
// graph-coloring is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// graph-coloring is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with graph-coloring. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.adt.graph.coloring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.adt.graph.Graph;
import de.topobyte.adt.graph.util.BreadthFirstEnumerationBuilder;

/**
 * @param <T>
 *            the type of elements constituting the nodes of the graph.
 * 
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 */
public class ColoringBuilder<T>
{

	final static Logger logger = LoggerFactory.getLogger(ColoringBuilder.class);

	public static <V> Map<V, Integer> color(Graph<V> graph, int numColors)
			throws ColoringException
	{
		BreadthFirstEnumerationBuilder<V> enumerationBuilder = new BreadthFirstEnumerationBuilder<>(
				graph);
		List<V> enumeration = enumerationBuilder.buildEnumeration();
		return color(graph, enumeration, numColors);
	}

	public static <V> Map<V, Integer> color(Graph<V> graph, List<V> enumeration,
			int numColors) throws ColoringException
	{
		return color(graph, enumeration, numColors, Integer.MAX_VALUE);
	}

	public static <V> Map<V, Integer> color(Graph<V> graph, List<V> enumeration,
			int numColors, int maxIterations) throws ColoringException
	{
		ColoringBuilder<V> coloring = new ColoringBuilder<>(graph);
		coloring.setNumberOfColors(numColors);
		boolean success = coloring.build(enumeration, maxIterations);
		if (success) {
			return coloring.getConfiguration();
		}
		return null;
	}

	private final Graph<T> graph;
	private List<T> enumeration;
	private Map<T, Integer> configuration;

	public ColoringBuilder(Graph<T> graph)
	{
		this.graph = graph;
	}

	private int numColors = 4;

	private void setNumberOfColors(int numColors)
	{
		this.numColors = numColors;
	}

	public void build(List<T> enumeration) throws ColoringException
	{
		build(enumeration, Integer.MAX_VALUE);
	}

	public boolean build(List<T> enumeration, int maxIterations)
			throws ColoringException
	{
		this.enumeration = enumeration;

		logger.debug("initializing configuration");
		configuration = new HashMap<>();
		initConfiguration();

		logger.debug("starting iteration");
		int totalIterations = 0;
		int i;
		for (i = 0; i < enumeration.size();) {
			// iteration i
			totalIterations += 1;
			if (totalIterations >= maxIterations) {
				logger.debug("STOPPING");
				return false;
			}
			boolean success = chooseNext(i);
			if (success) {
				i++;
				continue;
			}
			reset(i);
			if (i-- == 0) {
				throw new ColoringException("Unable to find a valid coloring");
			}
		}
		return true;
	}

	public Map<T, Integer> getConfiguration()
	{
		return configuration;
	}

	private void initConfiguration()
	{
		for (int i = 0; i < enumeration.size(); i++) {
			T node = enumeration.get(i);
			configuration.put(node, 0);
		}
	}

	private void reset(int i)
	{
		T node = enumeration.get(i);
		configuration.put(node, 0);
	}

	private boolean chooseNext(int i)
	{
		logger.debug("choosing: " + i);
		// get the node within the enumeration
		T node = enumeration.get(i);
		// and the associated current coloring of that node
		int current = configuration.get(node);

		// this list is about the node's neighbors' coloring
		// for each of the colors keep a boolean whether it is
		// already in use within the scope of the neighbors.
		ArrayList<Boolean> bools = new ArrayList<>();
		for (int k = 0; k <= numColors; k++) {
			bools.add(false);
		}
		// for each neighbor, get the current configuration's
		// color for it and save in the boolean-list.
		Set<T> neighbours = graph.getEdgesOut(node);
		for (T neighbour : neighbours) {
			int color = configuration.get(neighbour);
			bools.set(color, true);
		}
		// now find the first color thats higher than
		// the current one and not in use by any of the neighbors.
		for (int k = current + 1; k <= numColors; k++) {
			if (!bools.get(k)) {
				configuration.put(node, k);
				logger.debug("chose " + i + ": " + k + "");
				// return true on success
				return true;
			}
		}
		// return false if no valid color could be found.
		return false;
	}

}
