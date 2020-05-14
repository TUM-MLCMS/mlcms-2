package org.vadere.simulator.projects.dataprocessing.processor;

import org.vadere.simulator.control.SimulationState;
import org.vadere.state.scenario.Pedestrian;
import org.vadere.util.geometry.shapes.VPoint;

/**
 * @author Mario Teixeira Parente
 *
 */

public class PointDensityCountingAlgorithm extends PointDensityAlgorithm {
	private double radius;
	private double circArea;

	public PointDensityCountingAlgorithm(double radius) {
		super("counting");

		this.radius = radius;
		this.circArea = this.radius * this.radius * Math.PI;
	}

	@Override
	public double getDensity(VPoint pos, SimulationState state) {
		int numberOfPedsInCircle = 0;

		for (Pedestrian ped : state.getTopography().getElements(Pedestrian.class)) {
			if (pos.distance(ped.getPosition()) < this.radius) {
				numberOfPedsInCircle++;
			}
		}

		return (numberOfPedsInCircle / this.circArea);
	}
}
