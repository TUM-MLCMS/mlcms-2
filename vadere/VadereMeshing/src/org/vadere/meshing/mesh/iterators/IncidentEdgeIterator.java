package org.vadere.meshing.mesh.iterators;


import org.vadere.meshing.mesh.inter.IFace;
import org.vadere.meshing.mesh.inter.IHalfEdge;
import org.vadere.meshing.mesh.inter.IMesh;
import org.vadere.meshing.mesh.inter.IVertex;
import org.vadere.util.geometry.shapes.IPoint;
import org.vadere.util.logging.Logger;

import java.util.Iterator;

/**
 * This iterator iterates over the adjacent edges of the vertex of the edge of this iterator.
 * Each adjacent edge contains an adjacent vertex with respect to the vertex of the edge if this
 * iterator.
 *
 * @author Benedikt Zoennchen
 * @param <P> the type of the vertex
 * @param <E> the type of the half-edge
 * @param <F> the type of the face
 */
public class IncidentEdgeIterator<P extends IPoint, V extends IVertex<P>, E extends IHalfEdge<P>, F extends IFace<P>> implements Iterator<E> {

	private static Logger log = Logger.getLogger(IncidentEdgeIterator.class);
	private IMesh<P, V, E, F> mesh;
	private E current;
	private E edge;
	private boolean first;
	int count = 0;

	public IncidentEdgeIterator(final IMesh<P, V, E, F> mesh, final V vertex) {
		this(mesh, mesh.getEdge(vertex));
	}

	public IncidentEdgeIterator(final IMesh<P, V, E, F> mesh, final E edge) {
		this.mesh = mesh;
		this.edge = edge;
		this.current = mesh.getNext(edge);
		this.first = true;
	}

	@Override
	public boolean hasNext() {
		return (first || current != mesh.getNext(edge));
	}

	@Override
	public E next() {
		E result = current;
		current = mesh.getNext(mesh.getTwin(result));
		first = false;
		count++;
		//log.info(count);

		return result;
	}
}
