package org.vadere.meshing.mesh.gen;

import org.vadere.meshing.mesh.inter.IMesh;
import org.vadere.util.geometry.shapes.VLine;
import org.vadere.util.geometry.shapes.VPoint;
import org.vadere.util.geometry.shapes.VPolygon;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PointLocation<P extends VPoint> {

	private final Collection<PFace<P>> faces;
	private final List<P> orderedPointList;
	private final List<List<PHalfEdge<P>>> halfeEdgesSegments;
	private final List<List<P>> intersectionPointsInSegment;
	private final IMesh<P, PVertex<P>, PHalfEdge<P>, PFace<P>> mesh;

	private Comparator<P> pointComparatorX = (p1, p2) -> {
		double dx = p1.getX() - p2.getX();
		if(dx < 0) return -1;
		else if(dx > 0) return 1;
		else return 0;
	};

	private Comparator<P> pointComparatorY = (p1, p2) -> {
		double dy = p1.getY() - p2.getY();
		if(dy < 0) return -1;
		else if(dy > 0) return 1;
		else return 0;
	};

	private class BetweenTwoPoints implements Predicate<PHalfEdge<P>> {

		private P p1;
		private P p2;

		private BetweenTwoPoints(final P p1, final P p2) {
			this.p1 = p1;
			this.p2 = p2;
		}

		@Override
		public boolean test(final PHalfEdge<P> halfEdge) {
			P v1 = mesh.getPoint(halfEdge);
			P v2 = mesh.getPoint(mesh.getPrev(halfEdge));
			return (v1.getX() <= p1.getX() && v2.getX() >= p2.getX()) || (v2.getX() <= p1.getX() && v1.getX() >= p2.getX());
		}
	}

	private class HalfEdgeComparator implements Comparator<PHalfEdge<P>> {

		private double x1;
		private double x2;

		private HalfEdgeComparator(final double x1, final double x2) {
			this.x1 = x1;
			this.x2 = x2;
		}

		@Override
		public int compare(final PHalfEdge<P> edge1, final PHalfEdge<P> edge2) {
			VLine line1 = edge1.toLine();
			VLine line2 = edge2.toLine();
			double slope1 = line1.slope();
			double slope2 = line2.slope();

			double yIntersection1 = line1.getY1() + (x1-line1.getX1()) * slope1;
			double yIntersection2 = line2.getY1() + (x1 - line2.getX1()) * slope2;

			if(yIntersection1 < yIntersection2) return -1;
			else if(yIntersection1 > yIntersection2) return 1;
			else return 0;
		}
	}

	public PointLocation(final Collection<PFace<P>> faces, final IMesh<P, PVertex<P>, PHalfEdge<P>, PFace<P>> mesh) {
		this.faces = faces;
		this.mesh = mesh;

		//TODO distinct is maybe slow here
		Set<P> pointSet = faces.stream()
				.flatMap(face -> mesh.streamEdges(face)).map(edge -> mesh.getPoint(edge))
				.sorted(pointComparatorX).collect(Collectors.toSet());

		orderedPointList = pointSet.stream().sorted(pointComparatorX).collect(Collectors.toList());
		halfeEdgesSegments = new ArrayList<>(orderedPointList.size()-1);
		intersectionPointsInSegment = new ArrayList<>(orderedPointList.size()-1);

		for(int i = 0; i < orderedPointList.size() - 1; i++) {
			P p1 = orderedPointList.get(i);
			P p2 = orderedPointList.get(i+1);
			List<PHalfEdge<P>> halfEdges = faces.stream()
					.flatMap(face -> mesh.streamEdges(face)).filter(new BetweenTwoPoints(p1, p2))
					.sorted(new HalfEdgeComparator(p1.getX(), p2.getX())).collect(Collectors.toList());

			List<P> intersectionPoints = halfEdges.stream()
					.map(hf -> hf.toLine())
					.map(line -> intersectionWithX(p1.getX(), line))
					.distinct()
					.collect(Collectors.toList());

			halfeEdgesSegments.add(halfEdges);

			intersectionPointsInSegment.add(intersectionPoints);

			if(i == orderedPointList.size() - 2) {
				intersectionPoints = Collections.singletonList(p2);
				halfeEdgesSegments.add(halfEdges);
				intersectionPointsInSegment.add(intersectionPoints);
			}
		}

	}

	private P intersectionWithX(double x, VLine line) {
		Point2D p = line.getX1() < line.getX2() ? line.getP1() : line.getP2();

		return mesh.getPoint(mesh.createVertex(x, (p.getY() + (p.getX()-x) * line.slope())));
	}

	public Optional<PFace<P>> getFace(final P point) {
		int xSegmentIndex = Collections.binarySearch(orderedPointList, point, pointComparatorX);

		xSegmentIndex = xSegmentIndex < 0 ? -xSegmentIndex - 2 : xSegmentIndex;

		if(xSegmentIndex < 0 || xSegmentIndex >= intersectionPointsInSegment.size()) {
			return Optional.empty();
		}

		List<P> intersectionPoints = intersectionPointsInSegment.get(xSegmentIndex);
		int ySegmentIndex = Collections.binarySearch(intersectionPoints, point, pointComparatorY);

		ySegmentIndex = ySegmentIndex < 0 ? -ySegmentIndex - 2 : ySegmentIndex;


		if(ySegmentIndex < 0 || ySegmentIndex >= halfeEdgesSegments.get(xSegmentIndex).size()) {
			return Optional.empty();
		}

		PHalfEdge<P> edge = halfeEdgesSegments.get(xSegmentIndex).get(ySegmentIndex);

		PFace<P> face = mesh.getFace(edge);
		VPolygon polygon = mesh.toPolygon(face);
		if(polygon.contains(point)) {
			return Optional.of(face);
		}
		else {
			PFace<P> f = mesh.getTwinFace(edge);
			if(mesh.isBoundary(f)) {
				return Optional.empty();
			}
			else {
				return Optional.of(f);
			}
		}
	}
}
