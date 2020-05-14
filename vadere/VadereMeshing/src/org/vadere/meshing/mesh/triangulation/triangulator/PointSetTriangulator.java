package org.vadere.meshing.mesh.triangulation.triangulator;

import org.vadere.meshing.mesh.gen.IncrementalTriangulation;
import org.vadere.meshing.mesh.inter.IFace;
import org.vadere.meshing.mesh.inter.IHalfEdge;
import org.vadere.meshing.mesh.inter.IIncrementalTriangulation;
import org.vadere.meshing.mesh.inter.IMesh;
import org.vadere.meshing.mesh.inter.IPointLocator;
import org.vadere.meshing.mesh.inter.IVertex;
import org.vadere.util.geometry.shapes.IPoint;

import java.util.Collection;

/**
 * <p>A default triangulator: This triangulator takes a set of points P and constructs the Delaunay-Triangulation of P.</p>
 *
 * @author Benedikt Zoennchen
 *
 * @param <P> generic type of the point
 * @param <V> generic type of the vertex
 * @param <E> generic type of the half-edge
 * @param <F> generic type of the face
 */
public class PointSetTriangulator<P extends IPoint, V extends IVertex<P>, E extends IHalfEdge<P>, F extends IFace<P>> implements ITriangulator<P, V, E, F> {

	/**
	 * the triangulation which determines how points will be inserted.
	 */
    private final IIncrementalTriangulation<P, V, E, F> triangulation;

	/**
	 * the collection of points P.
	 */
	private final Collection<P> points;

	/**
	 * <p>The default constructor.</p>
	 *
	 * @param points        the collection of points P
	 * @param triangulation a triangulation which determines how points will be inserted
	 */
    public PointSetTriangulator(final Collection<P> points, final IIncrementalTriangulation<P, V, E, F> triangulation) {
        this.triangulation = triangulation;
        this.points = points;
    }

	/**
	 * <p>The default constructor.</p>
	 *
	 * @param points        the collection of points P
	 */
	public PointSetTriangulator(final Collection<P> points, IMesh<P, V, E, F> mesh) {
		this.triangulation = new IncrementalTriangulation<>(mesh, IPointLocator.Type.JUMP_AND_WALK, points);
		this.points = points;
	}

    @Override
    public IIncrementalTriangulation<P, V, E, F> generate() {
        triangulation.init();
        triangulation.insert(points);
        triangulation.finish();
        return triangulation;
    }
}
