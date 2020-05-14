package org.vadere.state.attributes.models;

import org.vadere.annotation.factories.attributes.ModelAttributeClass;
import org.vadere.state.attributes.Attributes;
import org.vadere.state.types.EikonalSolverType;

@ModelAttributeClass
public class AttributesFloorField extends Attributes {

	private EikonalSolverType createMethod = EikonalSolverType.HIGH_ACCURACY_FAST_MARCHING;

	/**
	 * These attribute values should only be used if createMethod.isUsingCellGrid() == true.
	 *
	 * TODO [refactoring]: However potentialFieldResolution is also used for the {@link org.vadere.simulator.models.potential.solver.timecost.ITimeCostFunction}
	 * for the density computation, i.e. it is the resolution of the matrix used in the discrete convolution. This should be changed!
	 * Furthermore, theare are many unused parameters in {@link AttributesTimeCost}.
	 * Solution:
	 * (1) change AttributesTimeCost timeCostAttributes to ITimeCostFunction like the potential classes in AttributesOSM
	 * (2) split AttributesTimeCost timeCostAttributes into multiple classes
	 * (3) add a new AttributesTimeCost into the top level (i.e. attributesModel) json.
	 */
	private double potentialFieldResolution = 0.1;
	private double obstacleGridPenalty = 0.1;
	private double targetAttractionStrength = 1.0;
	//private double

	private AttributesTimeCost timeCostAttributes;

	public AttributesFloorField() {
		timeCostAttributes = new AttributesTimeCost();
	}

	// Getters...
	public EikonalSolverType getCreateMethod() {
		return createMethod;
	}

	public double getPotentialFieldResolution() {
		return potentialFieldResolution;
	}

	public double getObstacleGridPenalty() {
		return obstacleGridPenalty;
	}

	public double getTargetAttractionStrength() {
		return targetAttractionStrength;
	}

	public AttributesTimeCost getTimeCostAttributes() {
		return timeCostAttributes;
	}
}
