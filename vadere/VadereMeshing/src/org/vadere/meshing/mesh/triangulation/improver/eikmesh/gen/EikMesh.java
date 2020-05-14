package org.vadere.meshing.mesh.triangulation.improver.eikmesh.gen;


import org.jetbrains.annotations.NotNull;
import org.vadere.meshing.mesh.inter.IFace;
import org.vadere.meshing.mesh.inter.IHalfEdge;
import org.vadere.meshing.mesh.inter.IIncrementalTriangulation;
import org.vadere.meshing.mesh.inter.IMesh;
import org.vadere.meshing.mesh.inter.IMeshSupplier;
import org.vadere.meshing.mesh.inter.IVertex;
import org.vadere.meshing.mesh.triangulation.IEdgeLengthFunction;
import org.vadere.meshing.mesh.triangulation.improver.IMeshImprover;
import org.vadere.meshing.mesh.triangulation.improver.distmesh.Parameters;
import org.vadere.meshing.mesh.triangulation.improver.eikmesh.EikMeshPoint;
import org.vadere.meshing.mesh.triangulation.triangulator.ITriangulator;
import org.vadere.meshing.mesh.triangulation.triangulator.UniformRefinementTriangulatorSFC;
import org.vadere.util.geometry.GeometryUtils;
import org.vadere.util.geometry.shapes.IPoint;
import org.vadere.util.geometry.shapes.VLine;
import org.vadere.util.geometry.shapes.VPoint;
import org.vadere.util.geometry.shapes.VPolygon;
import org.vadere.util.geometry.shapes.VRectangle;
import org.vadere.util.geometry.shapes.VShape;
import org.vadere.util.geometry.shapes.VTriangle;
import org.vadere.util.logging.Logger;
import org.vadere.util.math.DistanceFunction;
import org.vadere.util.math.IDistanceFunction;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Benedikt Zoennchen
 */
public class EikMesh<P extends EikMeshPoint, V extends IVertex<P>, E extends IHalfEdge<P>, F extends IFace<P>> implements IMeshImprover<P, V, E, F>, ITriangulator<P, V, E, F> {

	private boolean illegalMovement = false;
	private UniformRefinementTriangulatorSFC triangulatorSFC;
	private IDistanceFunction distanceFunc;
	private IEdgeLengthFunction edgeLengthFunc;
	private IIncrementalTriangulation<P, V, E, F> triangulation;
	private VRectangle bound;
	private double scalingFactor;
	private double deps;
	private static final int MAX_STEPS = 100;
	private int nSteps;
	private double initialEdgeLen;

	private boolean runParallel = false;
	private boolean profiling = false;
	private double minDeltaTravelDistance = 0.0;
	private double delta = Parameters.DELTAT;
	private List<V> collapseVertices;
	private List<E> splitEdges;

	// only for logging
    private static final Logger log = Logger.getLogger(EikMesh.class);

	public EikMesh(
            final IDistanceFunction distanceFunc,
            final IEdgeLengthFunction edgeLengthFunc,
            final double initialEdgeLen,
            final VRectangle bound,
            final Collection<? extends VShape> obstacleShapes,
            final IMeshSupplier<P, V, E, F> meshSupplier) {

		this.bound = bound;
		this.distanceFunc = distanceFunc;
		this.edgeLengthFunc = edgeLengthFunc;
		this.initialEdgeLen =initialEdgeLen;
		this.deps = 0.00001 * initialEdgeLen;
		this.nSteps = 0;
		this.collapseVertices = new ArrayList<>();
		this.splitEdges = new ArrayList<>();

        log.info("##### (start) generate a triangulation #####");
		Set<P> fixPoints = getFixPoints(meshSupplier.get(), obstacleShapes);
		triangulatorSFC = new UniformRefinementTriangulatorSFC(
		        meshSupplier,
                bound,
                obstacleShapes,
                edgeLengthFunc,
				initialEdgeLen,
                distanceFunc,
		        fixPoints);
        triangulation = triangulatorSFC.init();
        log.info("##### (end) generate a triangulation #####");
	}

	public EikMesh(
			final IDistanceFunction distanceFunc,
			final IEdgeLengthFunction edgeLengthFunc,
			final double initialEdgeLen,
			final VRectangle bound,
			final IMeshSupplier<P, V, E, F> meshSupplier) {
		this(distanceFunc, edgeLengthFunc, initialEdgeLen, bound, Collections.EMPTY_LIST, meshSupplier);
	}

	public EikMesh(final VPolygon boundary,
	               final double initialEdgeLen,
	               final Collection<? extends VShape> obstacleShapes,
	               final IMeshSupplier<P, V, E, F> meshSupplier){

		Rectangle2D rect = boundary.getBounds2D();
		this.bound = new VRectangle(
				rect.getX() - 0.1 * rect.getWidth(), rect.getY() - 0.1 * rect.getHeight(),  1.2 * rect.getWidth(),  1.2 * rect.getHeight());
		this.distanceFunc = new DistanceFunction(boundary, obstacleShapes);
		this.edgeLengthFunc = p -> 1.0;
		this.initialEdgeLen =initialEdgeLen;
		this.deps = 0.00001 * initialEdgeLen;
		this.nSteps = 0;
		this.collapseVertices = new ArrayList<>();
		this.splitEdges = new ArrayList<>();

		log.info("##### (start) generate a triangulation #####");
		//Set<P> fixPoints = new HashSet<>();
		Set<P> fixPoints = getFixPoints(meshSupplier.get(), obstacleShapes);
		addFixPoints(meshSupplier.get(), boundary, fixPoints);

		triangulatorSFC = new UniformRefinementTriangulatorSFC(
				meshSupplier,
				bound,
				obstacleShapes,
				edgeLengthFunc,
				initialEdgeLen,
				distanceFunc,
				fixPoints);
		triangulation = triangulatorSFC.init();

		log.info("##### (end) generate a triangulation #####");
	}

	public void step() {
		improve();
	}

	public void initialize() {
		while (!triangulatorSFC.isFinished()) {
			stepInitialize();
		}
	}

	public void stepInitialize() {
		triangulatorSFC.step();
	}

	public boolean initializationFinished() {
		return triangulatorSFC.isFinished();
	}

	@Override
	public IIncrementalTriangulation<P, V, E, F> generate() {

		if(!initializationFinished()) {
			initialize();
		}

		double quality = getQuality();
		//log.info("quality: " + quality);
		while (quality < Parameters.qualityMeasurement && nSteps < MAX_STEPS) {
			improve();
			quality = getQuality();
			//log.info("quality: " + quality);
		}

		removeTrianglesInsideObstacles();

		getMesh().garbageCollection();

		return getTriangulation();
	}

	public boolean isFinished() {
		return /*getQuality() >= Parameters.qualityMeasurement ||*/ nSteps >= MAX_STEPS;
	}

	@Override
	public IMesh<P, V, E, F> getMesh() {
		return triangulation.getMesh();
	}

	@Override
    public void improve() {
		synchronized (getMesh()) {

			if(!triangulatorSFC.isFinished()) {
				stepInitialize();
			}
			else {
				removeBoundaryLowQualityTriangles();
				triangulation.smoothBoundary();
				if(triangulation.isValid()) {
					flipEdges();
					removeTrianglesInsideHoles();
				}
				else {
					log.info("EikMesh re-triangulates in step " + nSteps);
					retriangulate();
					log.info("end re-triangulates in step " + nSteps);
					removeTrianglesOutsideBBox();
					removeTrianglesInsideObstacles();
				}

				computeScalingFactor();
				computeVertexForces();
				updateVertices();
				collapseEdges();
				splitEdges();

				nSteps++;
			}
		}
    }

    private void collapseEdges() {
		for(V vertex : collapseVertices) {
			triangulation.collapse3DVertex(vertex, true);
		}
		collapseVertices = new ArrayList<>();
    }

	private void splitEdges() {
		for(E boundaryEdge : splitEdges) {
			if(!triangulation.getMesh().isDestroyed(boundaryEdge)) {
				triangulation.splitEdge(boundaryEdge, true);
			}
		}
		splitEdges = new ArrayList<>();
	}

    @Override
    public IIncrementalTriangulation<P, V, E, F> getTriangulation() {
		return triangulation;
    }

    @Override
    public synchronized Collection<VTriangle> getTriangles() {
        return triangulation.streamTriangles().collect(Collectors.toList());
    }

    // TODO: parallize the whole triangulation
    public void retriangulate() {
  //      removeBoundaryLowQualityTriangles();
        triangulation.recompute();
        //removeTrianglesInsideObstacles();
    }

	/*public void cup() {
		double quality = getQuality();
		while (quality < Parameters.qualityMeasurement) {
			improve();
			quality = getQuality();
			log.info("quality = " + quality);
		}
		retriangulate();
	}*/


    /**
     * computes the edge forces / velocities for all half-edge i.e. for each edge twice!
     */
    private void computeForces() {
	    streamEdges().forEach(e -> computeForces(e));
	}

	private void computeVertexForces() {
    	streamVertices().forEach(v -> computeForce(v));
	}

	private void computeForce(final V vertex) {
		EikMeshPoint p1 = getMesh().getPoint(vertex);
		for(V v2 : getMesh().getAdjacentVertexIt(vertex)) {
			EikMeshPoint p2 = getMesh().getPoint(v2);

			double len = Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) + (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
			double desiredLen = edgeLengthFunc.apply(new VPoint((p1.getX() + p2.getX()) * 0.5, (p1.getY() + p2.getY()) * 0.5)) * Parameters.FSCALE * scalingFactor;

			double lenDiff = Math.max(desiredLen - len, 0);
			VPoint force = new VPoint((p1.getX() - p2.getX()) * (lenDiff / len), (p1.getY() - p2.getY()) * (lenDiff / len));
			p1.increaseVelocity(force);
			p1.increaseAbsoluteForce(force.distanceToOrigin());
		}
	}

    /**
     * computes the edge force / velocity for a single half-edge and adds it to its end vertex.
     *
     * @param edge
     */
    private void computeForces(final E edge) {
        EikMeshPoint p1 = getMesh().getPoint(edge);
        EikMeshPoint p2 = getMesh().getPoint(getMesh().getPrev(edge));

        double len = Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) + (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
        double desiredLen = edgeLengthFunc.apply(new VPoint((p1.getX() + p2.getX()) * 0.5, (p1.getY() + p2.getY()) * 0.5)) * Parameters.FSCALE * scalingFactor;

		double lenDiff = Math.max(desiredLen - len, 0);
        p1.increaseVelocity(new VPoint((p1.getX() - p2.getX()) * (lenDiff / len), (p1.getY() - p2.getY()) * (lenDiff / len)));
    }

	private void computeForcesBossan(final E edge) {
		EikMeshPoint p1 = getMesh().getPoint(edge);
		EikMeshPoint p2 = getMesh().getPoint(getMesh().getPrev(edge));

		double len = Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) + (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
		double desiredLen = edgeLengthFunc.apply(new VPoint((p1.getX() + p2.getX()) * 0.5, (p1.getY() + p2.getY()) * 0.5)) * Parameters.FSCALE * scalingFactor;

		double lenDiff = Math.max(desiredLen - len, 0);
		p1.increaseVelocity(new VPoint((p1.getX() - p2.getX()) * (lenDiff / (len / desiredLen)), (p1.getY() - p2.getY()) * (lenDiff / (len / desiredLen))));
	}
    /**
     * moves (which may include a back projection) each vertex according to their forces / velocity
     * and resets their forces / velocities.
     */
    private void updateVertices() {
	    streamVertices().forEach(v -> updateVertex(v));
	}

    /**
     * move the vertex (this may project the vertex back) and resets its velocity / forceå.
     *
     * @param vertex
     */
    private void updateVertex(final V vertex) {
	    // modify point placement only if it is not a fix point
	    EikMeshPoint point = getMesh().getPoint(vertex);
    	if(!isFixedVertex(vertex)) {

    		// 1. p_{k+1} = p_k + dt * F(p_k)
			applyForce(vertex);

		    // 2. project points outside of the boundary or if they are at the boundary
		    //E optBoundaryEdge = getMesh().getEdge(vertex);
		    boolean isAtBoundary = getMesh().isAtBoundary(vertex);
			double distance = distanceFunc.apply(vertex);
			if(distance > 0 || isAtBoundary) {
				//(Math.abs(distance) <= deps &&
				// to prevent from large movements we project only by 0.5 if the distance is large!
				projectBackVertex(vertex, distance, distance > initialEdgeLen / 3.0 ? 0.5 : 1.0, isAtBoundary);
			}

			if(isAtBoundary) {
				E boundaryEdge = getMesh().getBoundaryEdge(vertex).get();
				boolean collapse = false;

				// 3. remove unnecessary points
				if(getMesh().degree(vertex) == 3) {
					double force = getVelocity(vertex).distanceToOrigin();
					if(point.getAbsoluteForce() > 0 && force / point.getAbsoluteForce() < Parameters.MIN_FORCE_RATIO) {
						collapseVertices.add(vertex);
						collapse = true;
					}
				}

				// 4. split for low triangle quality
				if(!collapse && getMesh().isLongestEdge(boundaryEdge)
						&& faceToQuality(getMesh().getTwinFace(boundaryEdge)) < Parameters.MIN_SPLIT_TRIANGLE_QUALITY) {
					splitEdges.add(boundaryEdge);
				}
			}
		}
	    point.setVelocity(new VPoint(0,0));
	    point.setAbsoluteForce(0);
	}

    /**
     * projects the vertex back if it is no longer inside the boundary or inside an obstacle.
     *
     * @param vertex the vertex
     */
    private void projectBackVertex(@NotNull final V vertex, double distance, final double scale, final boolean isAtBoundary) {
        EikMeshPoint position = getMesh().getPoint(vertex);
	    double dGradPX = (distanceFunc.apply(position.toVPoint().add(new VPoint(deps, 0))) - distance) / deps;
	    double dGradPY = (distanceFunc.apply(position.toVPoint().add(new VPoint(0, deps))) - distance) / deps;
	    VPoint projection = new VPoint(dGradPX * distance * scale, dGradPY * distance * scale);

	    VPoint newPosition = getMesh().toPoint(vertex).subtract(projection);

	    if(isAtBoundary) {
	    	E boundaryEdge = getMesh().getBoundaryEdge(vertex).get();
	    	VPoint p = getMesh().toPoint(vertex);
	    	VPoint q = getMesh().toPoint(getMesh().getNext(boundaryEdge));
	    	VPoint r = getMesh().toPoint(getMesh().getPrev(boundaryEdge));
	    	double angle = GeometryUtils.angle(r, p, q);

	    	// back projection to the inside i.e. towards the inside of its triangle
	    	if(distance > 0) {
				//if(Utils.isRightOf(r, p, newPosition)
				//		&& Utils.isRightOf(p, q, newPosition)
				//		&& Utils.isRightOf(q, r, newPosition)){
					position.subtract(projection);
				//}
		    }
		    // back projection to the outside
		    else {
			    if(angle > Math.PI || (GeometryUtils.isLeftOf(r, p, newPosition) && GeometryUtils.isLeftOf(p, q, newPosition))) {
				    position.subtract(projection);
			    }
			    else {
			    	//triangulation.createFaceAtBoundary(boundaryEdge);
				   // VTriangle triangle = new VTriangle(p, q, r);
				   // position.add(triangle.midPoint().subtract(position));
			    }
		    }
	    }
    }

    /**
     * flips all edges which are illegal. Afterwards the triangulation is delaunay.
     *
     * @return true, if any flip was necessary, false otherwise.
     */
    private boolean flipEdges() {
	    if(runParallel) {
	        streamEdges().filter(e -> triangulation.isIllegal(e)).forEach(e -> triangulation.flipSync(e));
        }
        else {
		    streamEdges().filter(e -> triangulation.isIllegal(e)).forEach(e -> triangulation.flip(e));
        }
        return false;
    }

    /**
     * Computation of the global scaling factor which is used to
     */
    private void computeScalingFactor() {
        double edgeLengthSum = streamEdges()
                .map(edge -> getMesh().toLine(edge))
                .mapToDouble(line -> line.length())
                .sum();

        double desiredEdgeLenSum = streamEdges()
                .map(edge -> getMesh().toLine(edge))
                .map(line -> line.midPoint())
                .mapToDouble(midPoint -> edgeLengthFunc.apply(midPoint)).sum();
        scalingFactor =  Math.sqrt((edgeLengthSum * edgeLengthSum) / (desiredEdgeLenSum * desiredEdgeLenSum));
    }


    // helper methods
    private Stream<E> streamEdges() {
        return runParallel ? getMesh().streamEdgesParallel() : getMesh().streamEdges();
    }

    private Stream<V> streamVertices() {
        return runParallel ? getMesh().streamVerticesParallel() : getMesh().streamVertices();
    }

    private void computeDelta() {
        delta = Parameters.DELTAT;
        for(V vertex : getMesh().getVertices()) {
            if(!isFixedVertex(vertex)) {
                EikMeshPoint meshPoint = getMesh().getPoint(vertex);
                IPoint velocity = getVelocity(vertex);

                double desiredLen = edgeLengthFunc.apply(vertex) * Parameters.FSCALE * scalingFactor;
                delta = Math.min(delta, desiredLen * 0.1 / velocity.distanceToOrigin() );
            }
        }
        //log.info("delta: " + delta);
    }

    private boolean isMovementIllegal() {
        boolean result = false;
        for(F face : getMesh().getFaces()) {
            if(!getMesh().isBoundary(face) && !getMesh().isDestroyed(face) && !triangulation.isCCW(face)) {
                result = true;

                // is this a triangle at the boundary+
                for(E edge : getMesh().getEdgeIt(face)) {
                    if(getMesh().isAtBoundary(edge)) {
                        log.info("illegal face at the boundary.");
                    }
                    else {
                        log.info("illegal face.");
                    }
                }

            }
        }
        return result;
    }


	private boolean isFixedVertex(final V vertex) {
		return getMesh().getPoint(vertex).isFixPoint();
	}

	private IPoint getVelocity(final V vertex) {
		return getMesh().getPoint(vertex).getVelocity();
	}

	private void applyForce(final V vertex) {
		IPoint velocity = getVelocity(vertex);
		IPoint movement = velocity.scalarMultiply(delta);
		getMesh().getPoint(vertex).add(movement);
	}

	private Set<P> getFixPoints(final IMesh<P, V, E, F> mesh, @NotNull VShape... shapes) {
		Set<P> pointSet = new HashSet<>();
		for(VShape shape : shapes) {
			addFixPoints(mesh, shape, pointSet);
		}

		return pointSet;
	}

	private Set<P> getFixPoints(final IMesh<P, V, E, F> mesh, @NotNull Collection<? extends  VShape> shapes) {
		Set<P> pointSet = new HashSet<>();
		for(VShape shape : shapes) {
			addFixPoints(mesh, shape, pointSet);
		}

		return pointSet;
	}

	private void addFixPoints(@NotNull final IMesh<P, V, E, F> mesh, @NotNull final VShape shape, @NotNull final Set<P> fixPoints) {
		shape.getPath()
				.stream()
				.filter(p -> bound.contains(p))
				.map(p ->  mesh.createPoint(p.getX(), p.getY()))
				.filter(p -> !fixPoints.contains(p))
				.forEach(p -> {
					p.setFixPoint(true);
					fixPoints.add(p);
				});
	}

	/*public PriorityQueue<PFace<MeshPoint>> getQuailties() {
		PriorityQueue<PFace<MeshPoint>> heap = new PriorityQueue<>(new FaceComparator());
		heap.addAll(getMesh().getTriangles());
		return heap;
	}

    private void updatePoint(final MeshPoint point, final IPoint force) {

		if(!point.isFixPoint()) {
			IPoint movement = force.scalarMultiply(delta);
			point.add(movement);

			double distance = distanceFunc.apply(point);
			if(distance > 0) {
				double dGradPX = (distanceFunc.apply(point.toVPoint().add(new VPoint(deps,0))) - distance) / deps;
				double dGradPY = (distanceFunc.apply(point.toVPoint().add(new VPoint(0,deps))) - distance) / deps;

				point.subtract(new VPoint(dGradPX * distance, dGradPY * distance));
			}
		}
	}

	private void adjustMesh(final PVertex<MeshPoint> vertex) {
		PHalfEdge<MeshPoint> halfEdge = triangulation.getMesh().getEdge(vertex);
		PVertex<MeshPoint> p = triangulation.getMesh().getVertex(halfEdge);
		PHalfEdge<MeshPoint> current = halfEdge;
		boolean first = true;

		List<PHalfEdge<MeshPoint>> candidates = new ArrayList<>();
		while (first || current != halfEdge) {
			first = false;
			candidates.add(triangulation.getMesh().getPrev(current));
			//triangulation.legalizeRecursively(triangulation.getMesh().getPrev(current), p);
			current = triangulation.getMesh().getTwin(triangulation.getMesh().getNext(current));
		}

		for(PHalfEdge<MeshPoint> edge : candidates) {
			//triangulation.legalizeRecursively(triangulation.getMesh().getPrev(current), p);
		}

	}*/

	private void removeTrianglesInsideHoles() {
		List<F> holes = triangulation.getMesh().getHoles();
		Predicate<F> mergeCondition = f -> !triangulation.getMesh().isBoundary(f) && distanceFunc.apply(triangulation.getMesh().toTriangle(f).midPoint()) > 0;
		for(F face : holes) {
			triangulation.mergeFaces(face, mergeCondition, true);
		}
	}

	private void removeTrianglesInsideObstacles() {
		List<F> faces = triangulation.getMesh().getFaces();
		for(F face : faces) {
			if(!triangulation.getMesh().isDestroyed(face) && !triangulation.getMesh().isHole(face)) {
				triangulation.createHole(face, f -> distanceFunc.apply(triangulation.getMesh().toTriangle(f).midPoint()) > 0, true);
			}
		}
	}

	public void removeTrianglesOutsideBBox() {
		triangulation.shrinkBorder(f -> distanceFunc.apply(triangulation.getMesh().toTriangle(f).midPoint()) > 0, true);
	}

	/*private void removeTrianglesInsideObstacles() {
		List<F> faces = triangulation.getMesh().getFaces();
		triangulation.getMesh().getHoles();
		for(F face : faces) {
			if(!triangulation.getMesh().isDestroyed(face) && distanceFunc.apply(triangulation.getMesh().toTriangle(face).midPoint()) > 0) {
				triangulation.removeFaceAtBorder(face, true);
			}
		}
	}*/

	/*public void removeTrianglesInsideObstacles() {
		List<F> faces = triangulation.getMesh().getFaces();
		for(F face : faces) {
			if(!triangulation.getMesh().isDestroyed(face) && !triangulation.getMesh().isHole(face)) {
				triangulation.createHole(face, f -> distanceFunc.apply(triangulation.getMesh().toTriangle(f).midPoint()) > 0, true);
			}
		}
	}*/

	private void removeBoundaryLowQualityTriangles() {
		List<F> holes = triangulation.getMesh().getHoles();
		Predicate<F> mergeCondition = f -> !triangulation.getMesh().isDestroyed(f)
				&& !triangulation.getMesh().isBoundary(f)
				&& triangulation.getMesh().isAtBorder(f)
				&& (isCorner(f) || !isShortBoundaryEdge(f))
				//&& -distanceFunc.apply(triangulation.getMesh().toTriangle(f).getIncenter()) > 0.1 * initialEdgeLen
				&& faceToQuality(f) < Parameters.MIN_TRIANGLE_QUALITY;

		for(F face : holes) {
			List<F> neighbouringFaces = getMesh().streamEdges(face).map(e -> getMesh().getTwinFace(e)).collect(Collectors.toList());
			for (F neighbouringFace : neighbouringFaces) {
				if (mergeCondition.test(neighbouringFace)) {
					triangulation.removeEdges(face, neighbouringFace, true);
				}
			}
		}

		List<F> neighbouringFaces = getMesh().streamEdges(getMesh().getBorder()).map(e -> getMesh().getTwinFace(e)).collect(Collectors.toList());
		for (F neighbouringFace : neighbouringFaces) {
			if (mergeCondition.test(neighbouringFace)) {
				triangulation.removeEdges(getMesh().getBorder(), neighbouringFace, true);
			}
		}

		//triangulation.mergeFaces(getMesh().getBorder(), mergeCondition, true);
	}

	private boolean isCorner(@NotNull final F face) {
		E edge = getMesh().getBoundaryEdge(face).get();
		// corner => can be deleted
		if(getMesh().isBoundary(getMesh().getNext(edge)) || getMesh().isBoundary(getMesh().getPrev(edge))) {
			return true;
		}
		return false;
	}

	private boolean isShortBoundaryEdge(@NotNull final F face) {
		E edge = getMesh().getBoundaryEdge(face).get();
		// corner => can be deleted

		VLine l1 = getMesh().toLine(edge);
		VLine l2 = getMesh().toLine(getMesh().getNext(edge));
		VLine l3 = getMesh().toLine(getMesh().getPrev(edge));

		if(l1.length() < l2.length() || l1.length() < l3.length()) {
			return true;
		}

		return false;
	}

    /*private void removeBoundaryLowQualityTriangles() {
		if(runParallel) {
			streamEdges()
					.filter(e -> getMesh().isBoundary(e))
					.map(e -> getMesh().getTwinFace(e))
					.filter(face -> !getMesh().isDestroyed(face))
					.filter(face -> faceToQuality(face) < Parameters.MIN_TRIANGLE_QUALITY)
					.findAny().ifPresent(face -> triangulation.removeFaceAtBorder(face, true));
		}
		else {
			streamEdges()
					.filter(e -> getMesh().isBoundary(e))
					.map(e -> getMesh().getTwinFace(e))
					.filter(face -> !getMesh().isDestroyed(face))
					.filter(face -> !getMesh().isBoundary(face))
					.filter(face -> faceToQuality(face) < Parameters.MIN_TRIANGLE_QUALITY)
					.forEach(face -> triangulation.removeFaceAtBorder(face, true));
		}
    }*/
}
