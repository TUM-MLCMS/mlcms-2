package org.vadere.meshing.mesh.impl;

import org.jetbrains.annotations.NotNull;
import org.vadere.meshing.mesh.inter.IPointLocator;
import org.vadere.meshing.mesh.gen.IncrementalTriangulation;
import org.vadere.meshing.mesh.gen.PFace;
import org.vadere.meshing.mesh.gen.PHalfEdge;
import org.vadere.meshing.mesh.gen.PVertex;
import org.vadere.util.geometry.shapes.VPoint;
import org.vadere.util.geometry.shapes.VRectangle;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author Benedikt Zoennchen
 *
 * Note: Use factory i.e. ITriangulation for the creation!
 */
public class VPTriangulation extends IncrementalTriangulation<VPoint, PVertex<VPoint>, PHalfEdge<VPoint>, PFace<VPoint>> {

	public VPTriangulation(
			@NotNull final Collection<VPoint> points,
			@NotNull final Predicate<PHalfEdge<VPoint>> illegalPredicate) {
		super(new VPMesh(), IPointLocator.Type.JUMP_AND_WALK, points, illegalPredicate);
	}

	public VPTriangulation(final Set<VPoint> points) {
		super(new VPMesh(), IPointLocator.Type.JUMP_AND_WALK, points);
	}

	public VPTriangulation(final VRectangle bound,
	                       final Predicate<PHalfEdge<VPoint>> illegalPredicate){
		super(new VPMesh(), IPointLocator.Type.JUMP_AND_WALK, bound, illegalPredicate);
	}

	public VPTriangulation(final VRectangle bound) {
		super(new VPMesh(), IPointLocator.Type.JUMP_AND_WALK, bound);
	}
}
