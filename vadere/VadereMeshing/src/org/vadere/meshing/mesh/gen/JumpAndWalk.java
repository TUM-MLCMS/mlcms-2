package org.vadere.meshing.mesh.gen;

import org.jetbrains.annotations.NotNull;
import org.vadere.meshing.mesh.inter.IFace;
import org.vadere.meshing.mesh.inter.IHalfEdge;
import org.vadere.meshing.mesh.inter.IPointLocator;
import org.vadere.meshing.mesh.inter.IIncrementalTriangulation;
import org.vadere.meshing.mesh.inter.IVertex;
import org.vadere.util.geometry.shapes.IPoint;
import org.vadere.util.geometry.shapes.VPoint;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * @author Benedikt Zoennchen
 */
public class JumpAndWalk<P extends IPoint, V extends IVertex<P>, E extends IHalfEdge<P>, F extends IFace<P>> implements IPointLocator<P, V, E, F> {

	private final IIncrementalTriangulation<P, V, E, F> triangulation;
	private final Random random;

	public JumpAndWalk(@NotNull final IIncrementalTriangulation<P, V, E, F> triangulation) {
		this.triangulation = triangulation;
		this.random = new Random();
	}

	private Optional<F> getStartFace(final IPoint endPoint) {
		int n = triangulation.getMesh().getNumberOfVertices();

		if(n < 20) {
			return Optional.empty();
		}
		else {
			V result = null;
			double max = Math.pow(n, 1.0/3.0);

			for(int i = 0; i < max; i++) {

				V vertex = triangulation.getMesh().getRandomVertex(random);

				if(result == null || endPoint.distanceSq(vertex) < endPoint.distanceSq(result)) {
					result = vertex;
				}
			}
			return Optional.ofNullable(triangulation.getMesh().getFace(result));
		}
	}

	@Override
	public F locatePoint(P point) {
		return locate(point).get();
	}

	@Override
	public Optional<F> locate(P point) {
		Optional<F> startFace = getStartFace(point);
		if(startFace.isPresent()) {
			return triangulation.locateFace(point.getX(), point.getY(), startFace.get());
		}
		else {
			return triangulation.locateFace(point.getX(), point.getY());
		}
	}

	@Override
	public Optional<F> locate(double x, double y) {
		Optional<F> startFace = getStartFace(new VPoint(x, y));
		if(startFace.isPresent()) {
			return triangulation.locateFace(x, y, startFace.get());
		}
		else {
			return triangulation.locateFace(x, y);
		}
	}

	@Override
	public Type getType() {
		return Type.JUMP_AND_WALK;
	}

	@Override
	public void postSplitTriangleEvent(F original, F f1, F f2, F f3) {}

	@Override
	public void postSplitHalfEdgeEvent(F original, F f1, F f2) {}

	@Override
	public void postFlipEdgeEvent(F f1, F f2) {}

	@Override
	public void postInsertEvent(V vertex) {}
}
