package org.vadere.simulator.models.potential.fields;

import org.jetbrains.annotations.NotNull;
import org.vadere.meshing.mesh.inter.IMesh;
import org.vadere.simulator.models.potential.solver.calculators.EikonalSolver;
import org.vadere.state.attributes.Attributes;
import org.vadere.state.attributes.models.AttributesFloorField;
import org.vadere.state.attributes.scenario.AttributesAgent;
import org.vadere.state.scenario.Agent;
import org.vadere.state.scenario.ScenarioElement;
import org.vadere.state.scenario.Target;
import org.vadere.state.scenario.TargetPedestrian;
import org.vadere.state.scenario.Topography;
import org.vadere.util.data.cellgrid.IPotentialPoint;
import org.vadere.util.geometry.shapes.IPoint;
import org.vadere.util.geometry.shapes.VPoint;
import org.vadere.util.geometry.shapes.VShape;
import org.vadere.util.geometry.shapes.Vector2D;
import org.vadere.util.logging.Logger;
import org.vadere.util.math.InterpolationUtil;
import org.vadere.util.math.MathUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

/**
 * @author Benedikt Zoennchen
 */
public class PotentialFieldTarget implements IPotentialFieldTarget {

	private static Logger logger = Logger.getLogger(PotentialFieldTargetGrid.class);

	/**
	 * the simulation time in seconds in which the last update was performed.
	 */
	protected double lastUpdateTimestamp;

	/**
	 * the topography of the running simulation.
	 */
	protected final Topography topography;

	/**
	 * false if and only if there exits no dynamic potential field.
	 */
	private boolean potentialFieldsNeedUpdate;

	/**
	 * configuration of the potential fields.
	 */
	private AttributesFloorField attributes;

	/**
	 * configuration of the agents.
	 */
	private AttributesAgent attributesPedestrian;


	/**
	 * Stores all potential fields which represent to a target (targetId).
	 * This Map has to be filled by classes extending this class.
	 */
	protected final Map<Integer, EikonalSolver> eikonalSolvers;


	public PotentialFieldTarget(@NotNull final Topography topography,
	                            @NotNull final AttributesAgent attributesPedestrian,
	                            @NotNull final AttributesFloorField attributesPotential) {
		this.topography = topography;
		this.attributesPedestrian = attributesPedestrian;
		this.attributes = attributesPotential;
		this.eikonalSolvers = new HashMap<>();
	}

	@Override
	public IPotentialField getSolution() {
		Map<Integer, Function<IPoint, Double>> clone = new HashMap<>();

		for(Map.Entry<Integer, EikonalSolver> entry : eikonalSolvers.entrySet()) {
			Integer targetId = entry.getKey();
			EikonalSolver eikonalSolver = entry.getValue();

			clone.put(targetId, eikonalSolver.getPotentialField());
		}

		return (pos, agent) -> clone.get(agent.getNextTargetId()).apply(pos);
	}

	@Override
	public Function<Agent, IMesh<? extends IPotentialPoint, ?, ?, ?>> getDiscretization() {
		Map<Integer, IMesh<? extends IPotentialPoint, ?, ?, ?>> clone = new HashMap<>();

		for(Map.Entry<Integer, EikonalSolver> entry : eikonalSolvers.entrySet()) {
			Integer targetId = entry.getKey();
			EikonalSolver eikonalSolver = entry.getValue();
			clone.put(targetId, eikonalSolver.getDiscretization());
		}

		return agent -> clone.get(agent.getNextTargetId());
	}

	@Override
	public Vector2D getTargetPotentialGradient(VPoint pos, Agent ped) {
		//double potential = getPotential(pos, ped);
		// according to https://en.wikipedia.org/wiki/Numerical_differentiation#Practical_considerations_using_floating_point_arithmetic
		double eps = Math.max(pos.x, pos.y) * MathUtil.EPSILON;
		//double eps = 0.001;
		double dGradPX = (getPotential(pos.add(new VPoint(eps, 0)), ped) - getPotential(pos.subtract(new VPoint(eps, 0)), ped)) / (2*eps);
		double dGradPY = (getPotential(pos.add(new VPoint(0, eps)), ped) - getPotential(pos.subtract(new VPoint(0, eps)), ped)) / (2*eps);
		return new Vector2D(dGradPX, dGradPY);
	}

	/**
	 * Returns the target potential at pos for the agent: ((x,y), agent) -> potential
	 * This does not take obstacle repulsion and pedestrian repulsion into
	 * account. The target potential does not exists (i.e. is equal to 0) if:
	 *  - the agent has reached his target (geometrically) or
	 *  - the agent does not have a next target or
	 *  - there is no target potential field computed for the target of the agent (error!).
	 *
	 * @param pos   the position for which the potential will be evaluated
	 * @param agent the agent for which the potential will be evaluated
	 * @return the target potential of the agent at position pos if it exists, 0 otherwise.
	 */
	@Override
	public double getPotential(final IPoint pos, final Agent agent) {

		if (!agent.hasNextTarget()) {
			return 0.0;
		}

		int targetId = agent.getNextTargetId();

		// the agent has reached his current target
		if (topography.getTarget(targetId).getShape().contains(pos)) {
			return 0.0;
		}

		// the agent is inside an obstacle
		for (ScenarioElement b : topography.getObstacles()) {
			if (b.getShape().contains(pos)) {
				return Double.MAX_VALUE;
			}
		}

		/* Find minimal potential of given targets. */
		Optional<EikonalSolver> optEikonalSolver = getSolver(targetId);

		// no target exist
		if (!optEikonalSolver.isPresent()) {
			logger.error("no target potential field for target = " + targetId + ", was found!");
			return 0.0;
		}

		EikonalSolver eikonalSolver = optEikonalSolver.get();
		return eikonalSolver.getPotential(pos);
	}

	/**
	 * Updates a specific potential field (defined by (targetId, targetShapes)) if it is:
	 * - dynamic
	 * - the target is a agent (i.e. its moving) and the agent is still alive (see helping behaviour)
	 * - the target is moving in general.
	 *
	 *
	 * @param simTimeInSec  the simulation time at which upate was called.
	 * @param target        the target itself
	 * @param targetShapes  the area of the target
	 */
	protected void updatePotentialField(final double simTimeInSec, final Target target, final List<VShape> targetShapes) {
		if (target.isTargetPedestrian()) {
			if (!((TargetPedestrian) target).isDeleted()) {
				addEikonalSolver(target.getId(), targetShapes);
			}
		} else if (target.isMovingTarget()) {
			addEikonalSolver(target.getId(), targetShapes);
		}

		if (eikonalSolvers.containsKey(target.getId())) {
			eikonalSolvers.get(target.getId()).update();
		} else {
			logger.warn("potential field for target " + target.getId() + " is not contained in " + this);
		}
	}

	/**
	 * Adds an EikonalSolver for a specific target defined by (targetId, shapes).
	 * If there already exists an EikonalSolver for the same target, identified by
	 * targetId, it will be replaced.
	 *
	 * @param targetId  the targetId of the target
	 * @param shapes    the target area
	 */
	protected void addEikonalSolver(final int targetId, final List<VShape> shapes) {
		EikonalSolver eikonalSolver = IPotentialField.create(topography, targetId, shapes, attributesPedestrian, attributes);
		potentialFieldsNeedUpdate = potentialFieldsNeedUpdate || eikonalSolver.needsUpdate();
		eikonalSolvers.put(targetId, eikonalSolver);
	}

	@Override
	public void initialize(List<Attributes> attributesList, Topography topography, AttributesAgent attributesPedestrian, Random random) {}

	private void addMissingEikonalSolvers() {
		Map<Integer, List<VShape>> mergeMap = topography.getTargetShapes();
		topography.getTargets().stream()
				.filter(t -> !getSolver(t.getId()).isPresent())
				.forEach(t -> addEikonalSolver(t.getId(), mergeMap.get(t.getId())));
	}

	/**
	 * Returns true if the dynamic potential fields has to be updated, false otherwise.
	 *
	 * @param simTimeInSec the current simulation time in seconds
	 * @return true if the dynamic potential fields has to be updated, false otherwise.
	 */
	protected boolean isNeedsUpdate(final double simTimeInSec) {
		return lastUpdateTimestamp < simTimeInSec && (potentialFieldsNeedUpdate || topography.containsTarget(t -> (t.isMovingTarget() || t.isTargetPedestrian())));
	}

	/**
	 * Returns a EikonalSover which solves the eikonal equation for a specific target (defined by the targetId).
	 * If there exists no such solver the Optional is empty.
	 *
	 * @param targetId  the targetId which defines the target
	 * @return an Optional of EikonalSover which solves the eikonal equation for a specific target
	 */
	protected Optional<EikonalSolver> getSolver(int targetId) {
		if(eikonalSolvers.containsKey(targetId)) {
			return Optional.of(eikonalSolvers.get(targetId));
		} else {
			return Optional.empty();
		}
	}

	@Override
	public boolean needsUpdate() {
		return potentialFieldsNeedUpdate;
	}

	@Override
	public void preLoop(final double simTimeInSec) {
		addMissingEikonalSolvers();
	}

	/**
	 * Updates all dynamic potential fields or potential field for a moving area if necessary.
	 *
	 * @param simTimeInSec the simulation time in seconds at which the update was called.
	 */
	@Override
	public void update(final double simTimeInSec) {
		if (isNeedsUpdate(simTimeInSec)) {
			List<Target> targets = topography.getTargets();
			Map<Integer, List<VShape>> mergeMap = topography.getTargetShapes();
			lastUpdateTimestamp = simTimeInSec;
			targets.stream().forEach(target -> updatePotentialField(simTimeInSec, target, mergeMap.get(target.getId())));
		}
	}

	@Override
	public void postLoop(final double simTimeInSec) {}
}
