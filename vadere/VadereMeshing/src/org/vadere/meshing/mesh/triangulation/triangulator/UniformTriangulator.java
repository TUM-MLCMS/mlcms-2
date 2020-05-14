package org.vadere.meshing.mesh.triangulation.triangulator;

import org.vadere.meshing.mesh.inter.IFace;
import org.vadere.meshing.mesh.inter.IHalfEdge;
import org.vadere.meshing.mesh.inter.IMesh;
import org.vadere.meshing.mesh.inter.IIncrementalTriangulation;
import org.vadere.meshing.mesh.inter.IVertex;
import org.vadere.util.geometry.shapes.IPoint;
import org.vadere.util.geometry.shapes.VRectangle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This triangulator triangulates a whole bounding box by a uniform triangulation of a specific side length.
 *
 * @author Benedikt Zoennchen
 *
 * @param <P> generic type of the point
 * @param <V> generic type of the vertex
 * @param <E> generic type of the half-edge
 * @param <F> generic type of the face
 */
public class UniformTriangulator<P extends IPoint, V extends IVertex<P>, E extends IHalfEdge<P>, F extends IFace<P>> implements ITriangulator<P, V, E, F> {

	private double left;
	private double top;
	private double width;
	private double height;
	private double minTriangleSideLength;
	private VRectangle bound;
	private final IIncrementalTriangulation<P, V, E, F> triangulation;

	/**
	 * The default constructor.
	 *
	 * @param bound                 the bounding box
	 * @param minTriangleSideLength the minimal side length
	 * @param triangulation         the triangulation i.e. the algorithm inserting points
	 */
	public UniformTriangulator(final VRectangle bound,
                               final double minTriangleSideLength,
                               final IIncrementalTriangulation<P, V, E, F> triangulation) {
		this.bound = bound;
		this.triangulation = triangulation;
		this.left = bound.getMinX();
		this.top = bound.getMinY();
		this.width = bound.getWidth();
		this.height = bound.getHeight();
		this.minTriangleSideLength = minTriangleSideLength;
	}

    @Override
    public IIncrementalTriangulation<P, V, E, F> generate() {
        triangulation.init();

        List<P> pointList = new ArrayList<>(generatePointSet());
        //Collections.shuffle(pointList);

        for(P point : pointList) {
        	if(bound.contains(point)) {
		        triangulation.insert(point);
	        }
        }

        triangulation.finish();
        return triangulation;
    }

	private IMesh<P, V, E, F> getMesh() {
        return triangulation.getMesh();
	}

	private Collection<P> generatePointSet() {
		// height of a triangle with 60 deg everywhere
		List<P> pointSet = new ArrayList<>();
		double s = minTriangleSideLength;
		double h = minTriangleSideLength * Math.sqrt(3) / 2.0;
		// create stencil with four triangle which can be used to triangulate
		// the whole rectangle seamlessly
		P add1 = getMesh().createPoint(-s / 2, h);
		P add2 = getMesh().createPoint(s / 2, h);
		P add3 = getMesh().createPoint(s, 0);
		P add4 = getMesh().createPoint(0, 2 * h);
		P add5 = getMesh().createPoint(s, 2 * h);

		for (int row = 0; row < (int) Math.ceil(height / h) + 1; row += 2) {
			for (int col = 0; col < (int) Math.ceil(width
					/ minTriangleSideLength); col++) {
				P p1 = getMesh().createPoint(left + col * minTriangleSideLength, top + row * h);

				P p2 = getMesh().createPoint(p1.getX() + add1.getX(), p1.getY() + add1.getY());
				P p3 = getMesh().createPoint(p1.getX() + add2.getX(), p1.getY() + add2.getY());
				P p4 = getMesh().createPoint(p1.getX() + add3.getX(), p1.getY() + add3.getY());
				P p5 = getMesh().createPoint(p1.getX() + add4.getX(), p1.getY() + add4.getY());
				P p6 = getMesh().createPoint(p1.getX() + add5.getX(), p1.getY() + add5.getY());

				pointSet.add(p1);
				pointSet.add(p2);
				pointSet.add(p3);
				pointSet.add(p4);
				pointSet.add(p5);
				pointSet.add(p6);
			}
		}
		return pointSet;
	}

}
