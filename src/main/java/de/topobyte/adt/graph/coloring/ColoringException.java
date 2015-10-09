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

public class ColoringException extends Exception
{

	private static final long serialVersionUID = -6739118189344594454L;

	public ColoringException(String message)
	{
		super(message);
	}

}
