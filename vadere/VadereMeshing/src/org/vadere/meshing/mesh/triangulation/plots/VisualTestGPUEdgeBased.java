package org.vadere.meshing.mesh.triangulation.plots;

import org.apache.commons.lang3.time.StopWatch;
import org.vadere.meshing.mesh.gen.AFace;
import org.vadere.meshing.mesh.gen.AHalfEdge;
import org.vadere.meshing.mesh.gen.AMesh;
import org.vadere.meshing.mesh.gen.AVertex;
import org.vadere.meshing.mesh.gen.MeshPanel;
import org.vadere.meshing.mesh.inter.IMeshSupplier;
import org.vadere.meshing.mesh.inter.IPointConstructor;
import org.vadere.meshing.mesh.triangulation.IEdgeLengthFunction;
import org.vadere.meshing.mesh.triangulation.improver.eikmesh.EikMeshPoint;
import org.vadere.meshing.mesh.triangulation.improver.eikmesh.opencl.CLEikMesh;
import org.vadere.util.geometry.shapes.VRectangle;
import org.vadere.util.geometry.shapes.VShape;
import org.vadere.util.logging.Logger;
import org.vadere.util.math.IDistanceFunction;
import org.vadere.util.opencl.OpenCLException;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class VisualTestGPUEdgeBased {

	private static final Logger log = Logger.getLogger(RunTimeGPUEdgeBased.class);

	private static final VRectangle bbox = new VRectangle(-11, -11, 22, 22);
	private static final IEdgeLengthFunction uniformEdgeLength = p -> 1.0;
	private static final IPointConstructor<EikMeshPoint> pointConstructor = (x, y) -> new EikMeshPoint(x, y, false);
	private static final double initialEdgeLength =  1.5;

	private static void overallUniformRing() throws OpenCLException {

		IMeshSupplier<AVertex, AHalfEdge, AFace> supplier = () -> new AMesh();
		IDistanceFunction distanceFunc = p -> Math.abs(7 - Math.sqrt(p.getX() * p.getX() + p.getY() * p.getY())) - 3;
		List<VShape> obstacles = new ArrayList<>();

		CLEikMesh meshGenerator = new CLEikMesh(distanceFunc, uniformEdgeLength, bbox, new ArrayList<>(), supplier);
		meshGenerator.initialize();

		MeshPanel<AVertex, AHalfEdge, AFace> distmeshPanel = new MeshPanel(meshGenerator.getMesh(), f -> false, 1000, 800);
		JFrame frame = distmeshPanel.display();
		frame.setVisible(true);
		frame.setTitle("uniformRing()");
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		distmeshPanel.repaint();

		StopWatch overAllTime = new StopWatch();
		overAllTime.start();
		overAllTime.suspend();
		int nSteps = 0;
		while (nSteps < 300) {
			nSteps++;
			overAllTime.resume();
			meshGenerator.improve();
			overAllTime.suspend();
			meshGenerator.refresh();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			distmeshPanel.repaint();
		}
		overAllTime.stop();

		log.info("#vertices: " + meshGenerator.getMesh().getVertices().size());
		log.info("#edges: " + meshGenerator.getMesh().getEdges().size());
		log.info("#faces: " + meshGenerator.getMesh().getFaces().size());
		log.info("quality: " + meshGenerator.getQuality());
		log.info("overall time: " + overAllTime.getTime() + "[ms]");

	}

	public static void main(String[] args) throws OpenCLException {
		overallUniformRing();
	}

}
