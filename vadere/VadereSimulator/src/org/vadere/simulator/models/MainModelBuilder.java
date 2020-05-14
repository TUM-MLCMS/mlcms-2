package org.vadere.simulator.models;

import java.util.Random;

import org.vadere.simulator.projects.ScenarioStore;
import org.vadere.state.attributes.AttributesSimulation;
import org.vadere.util.reflection.DynamicClassInstantiator;

/**
 * This class encapsulates the creation of MainModel.
 * 
 * For creation of submodels, see {@link SubModelBuilder}! The SubModelBuilder
 * should be used in the {@link MainModel#initialize} method.
 */
public class MainModelBuilder {

	private ScenarioStore scenarioStore;
	private MainModel model;
	private Random random;

	public MainModelBuilder(ScenarioStore scenarioStore) {
		this.scenarioStore = scenarioStore;
	}

	public void createModelAndRandom()
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {

		final AttributesSimulation attributesSimulation = scenarioStore.getAttributesSimulation();
		if (attributesSimulation.isUseFixedSeed()) {
			long seed = attributesSimulation.getFixedSeed();
			attributesSimulation.setSimulationSeed(seed);
			random = new Random(seed);
		} else {
			long seed = new Random().nextLong();
			attributesSimulation.setSimulationSeed(seed);
			random = new Random(seed);
		}

		model = instantiateMainModel(random);

	}

	public MainModel getModel() {
		return model;
	}

	public Random getRandom() {
		return random;
	}

	private MainModel instantiateMainModel(Random random) {
		String mainModelName = scenarioStore.getMainModel();
		DynamicClassInstantiator<MainModel> instantiator = new DynamicClassInstantiator<>();
		MainModel mainModel = instantiator.createObject(mainModelName);
		mainModel.initialize(scenarioStore.getAttributesList(), scenarioStore.getTopography(),
				scenarioStore.getTopography().getAttributesPedestrian(), random);
		return mainModel;
	}

}
