package org.vadere.state.attributes;

import org.vadere.state.attributes.Attributes;
import org.vadere.util.factory.attributes.AttributeBaseFactory;

import org.vadere.state.attributes.models.AttributesSingleTargetStrategy;
import org.vadere.state.attributes.models.AttributesOVM;
import org.vadere.state.attributes.models.AttributesPotentialGNM;
import org.vadere.state.attributes.models.AttributesSFM;
import org.vadere.state.attributes.models.AttributesCA;
import org.vadere.state.attributes.models.AttributesPotentialParticles;
import org.vadere.state.attributes.models.AttributesOSM;
import org.vadere.state.attributes.models.AttributesSeating;
import org.vadere.state.attributes.models.AttributesBMM;
import org.vadere.state.attributes.models.AttributesCGM;
import org.vadere.state.attributes.models.AttributesSIRG;
import org.vadere.state.attributes.models.AttributesQueuingGame;
import org.vadere.state.attributes.models.AttributesPotentialCompactSoftshell;
import org.vadere.state.attributes.models.AttributesODEIntegrator;
import org.vadere.state.attributes.models.AttributesTimeCost;
import org.vadere.state.attributes.models.AttributesGFM;
import org.vadere.state.attributes.models.AttributesReynolds;
import org.vadere.state.attributes.models.AttributesBHM;
import org.vadere.state.attributes.models.AttributesPotentialCompact;
import org.vadere.state.attributes.models.AttributesGNM;
import org.vadere.state.attributes.models.AttributesPotentialSFM;
import org.vadere.state.attributes.models.AttributesPotentialRingExperiment;
import org.vadere.state.attributes.models.AttributesParticles;
import org.vadere.state.attributes.models.AttributesFloorField;
import org.vadere.state.attributes.models.AttributesPotentialOSM;
import org.vadere.state.attributes.models.AttributesSTOM;



public class ModelAttributeFactory extends AttributeBaseFactory<Attributes> {


	private static ModelAttributeFactory instance;

	//good performance threadsafe Singletone. Sync block will only be used once
	public static ModelAttributeFactory instance(){
		if(instance ==  null){
			synchronized (ModelAttributeFactory.class){
				if(instance == null){
					instance = new ModelAttributeFactory();
				}
			}
		}
		return instance;
	}


	private ModelAttributeFactory(){

		addMember(AttributesSingleTargetStrategy.class, this::getAttributesSingleTargetStrategy);
		addMember(AttributesOVM.class, this::getAttributesOVM);
		addMember(AttributesPotentialGNM.class, this::getAttributesPotentialGNM);
		addMember(AttributesSFM.class, this::getAttributesSFM);
		addMember(AttributesCA.class, this::getAttributesCA);
		addMember(AttributesPotentialParticles.class, this::getAttributesPotentialParticles);
		addMember(AttributesOSM.class, this::getAttributesOSM);
		addMember(AttributesSeating.class, this::getAttributesSeating);
		addMember(AttributesBMM.class, this::getAttributesBMM);
		addMember(AttributesCGM.class, this::getAttributesCGM);
		addMember(AttributesSIRG.class, this::getAttributesSIRG);
		addMember(AttributesQueuingGame.class, this::getAttributesQueuingGame);
		addMember(AttributesPotentialCompactSoftshell.class, this::getAttributesPotentialCompactSoftshell);
		addMember(AttributesODEIntegrator.class, this::getAttributesODEIntegrator);
		addMember(AttributesTimeCost.class, this::getAttributesTimeCost);
		addMember(AttributesGFM.class, this::getAttributesGFM);
		addMember(AttributesReynolds.class, this::getAttributesReynolds);
		addMember(AttributesBHM.class, this::getAttributesBHM);
		addMember(AttributesPotentialCompact.class, this::getAttributesPotentialCompact);
		addMember(AttributesGNM.class, this::getAttributesGNM);
		addMember(AttributesPotentialSFM.class, this::getAttributesPotentialSFM);
		addMember(AttributesPotentialRingExperiment.class, this::getAttributesPotentialRingExperiment);
		addMember(AttributesParticles.class, this::getAttributesParticles);
		addMember(AttributesFloorField.class, this::getAttributesFloorField);
		addMember(AttributesPotentialOSM.class, this::getAttributesPotentialOSM);
		addMember(AttributesSTOM.class, this::getAttributesSTOM);
	}


	// Getters
	public AttributesSingleTargetStrategy getAttributesSingleTargetStrategy(){
		return new AttributesSingleTargetStrategy();
	}

	public AttributesOVM getAttributesOVM(){
		return new AttributesOVM();
	}

	public AttributesPotentialGNM getAttributesPotentialGNM(){
		return new AttributesPotentialGNM();
	}

	public AttributesSFM getAttributesSFM(){
		return new AttributesSFM();
	}

	public AttributesCA getAttributesCA(){
		return new AttributesCA();
	}

	public AttributesPotentialParticles getAttributesPotentialParticles(){
		return new AttributesPotentialParticles();
	}

	public AttributesOSM getAttributesOSM(){
		return new AttributesOSM();
	}

	public AttributesSeating getAttributesSeating(){
		return new AttributesSeating();
	}

	public AttributesBMM getAttributesBMM(){
		return new AttributesBMM();
	}

	public AttributesCGM getAttributesCGM(){
		return new AttributesCGM();
	}

	public AttributesSIRG getAttributesSIRG(){
		return new AttributesSIRG();
	}

	public AttributesQueuingGame getAttributesQueuingGame(){
		return new AttributesQueuingGame();
	}

	public AttributesPotentialCompactSoftshell getAttributesPotentialCompactSoftshell(){
		return new AttributesPotentialCompactSoftshell();
	}

	public AttributesODEIntegrator getAttributesODEIntegrator(){
		return new AttributesODEIntegrator();
	}

	public AttributesTimeCost getAttributesTimeCost(){
		return new AttributesTimeCost();
	}

	public AttributesGFM getAttributesGFM(){
		return new AttributesGFM();
	}

	public AttributesReynolds getAttributesReynolds(){
		return new AttributesReynolds();
	}

	public AttributesBHM getAttributesBHM(){
		return new AttributesBHM();
	}

	public AttributesPotentialCompact getAttributesPotentialCompact(){
		return new AttributesPotentialCompact();
	}

	public AttributesGNM getAttributesGNM(){
		return new AttributesGNM();
	}

	public AttributesPotentialSFM getAttributesPotentialSFM(){
		return new AttributesPotentialSFM();
	}

	public AttributesPotentialRingExperiment getAttributesPotentialRingExperiment(){
		return new AttributesPotentialRingExperiment();
	}

	public AttributesParticles getAttributesParticles(){
		return new AttributesParticles();
	}

	public AttributesFloorField getAttributesFloorField(){
		return new AttributesFloorField();
	}

	public AttributesPotentialOSM getAttributesPotentialOSM(){
		return new AttributesPotentialOSM();
	}

	public AttributesSTOM getAttributesSTOM(){
		return new AttributesSTOM();
	}


}
