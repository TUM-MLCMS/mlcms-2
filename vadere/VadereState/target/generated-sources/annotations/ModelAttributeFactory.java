package org.vadere.state.attributes;

import org.vadere.state.attributes.Attributes;
import org.vadere.util.factory.attributes.AttributeBaseFactory;

import org.vadere.state.attributes.models.AttributesPotentialSFM;
import org.vadere.state.attributes.models.AttributesPotentialParticles;
import org.vadere.state.attributes.models.AttributesPotentialOSM;
import org.vadere.state.attributes.models.AttributesCA;
import org.vadere.state.attributes.models.AttributesOVM;
import org.vadere.state.attributes.models.AttributesOSM;
import org.vadere.state.attributes.models.AttributesReynolds;
import org.vadere.state.attributes.models.AttributesGNM;
import org.vadere.state.attributes.models.AttributesQueuingGame;
import org.vadere.state.attributes.models.AttributesBMM;
import org.vadere.state.attributes.models.AttributesSeating;
import org.vadere.state.attributes.models.AttributesODEIntegrator;
import org.vadere.state.attributes.models.AttributesFloorField;
import org.vadere.state.attributes.models.AttributesSFM;
import org.vadere.state.attributes.models.AttributesBHM;
import org.vadere.state.attributes.models.AttributesTimeCost;
import org.vadere.state.attributes.models.AttributesPotentialCompactSoftshell;
import org.vadere.state.attributes.models.AttributesSingleTargetStrategy;
import org.vadere.state.attributes.models.AttributesSIRG;
import org.vadere.state.attributes.models.AttributesCGM;
import org.vadere.state.attributes.models.AttributesPotentialCompact;
import org.vadere.state.attributes.models.AttributesPotentialGNM;
import org.vadere.state.attributes.models.AttributesPotentialRingExperiment;
import org.vadere.state.attributes.models.AttributesGFM;
import org.vadere.state.attributes.models.AttributesSTOM;
import org.vadere.state.attributes.models.AttributesParticles;



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

		addMember(AttributesPotentialSFM.class, this::getAttributesPotentialSFM);
		addMember(AttributesPotentialParticles.class, this::getAttributesPotentialParticles);
		addMember(AttributesPotentialOSM.class, this::getAttributesPotentialOSM);
		addMember(AttributesCA.class, this::getAttributesCA);
		addMember(AttributesOVM.class, this::getAttributesOVM);
		addMember(AttributesOSM.class, this::getAttributesOSM);
		addMember(AttributesReynolds.class, this::getAttributesReynolds);
		addMember(AttributesGNM.class, this::getAttributesGNM);
		addMember(AttributesQueuingGame.class, this::getAttributesQueuingGame);
		addMember(AttributesBMM.class, this::getAttributesBMM);
		addMember(AttributesSeating.class, this::getAttributesSeating);
		addMember(AttributesODEIntegrator.class, this::getAttributesODEIntegrator);
		addMember(AttributesFloorField.class, this::getAttributesFloorField);
		addMember(AttributesSFM.class, this::getAttributesSFM);
		addMember(AttributesBHM.class, this::getAttributesBHM);
		addMember(AttributesTimeCost.class, this::getAttributesTimeCost);
		addMember(AttributesPotentialCompactSoftshell.class, this::getAttributesPotentialCompactSoftshell);
		addMember(AttributesSingleTargetStrategy.class, this::getAttributesSingleTargetStrategy);
		addMember(AttributesSIRG.class, this::getAttributesSIRG);
		addMember(AttributesCGM.class, this::getAttributesCGM);
		addMember(AttributesPotentialCompact.class, this::getAttributesPotentialCompact);
		addMember(AttributesPotentialGNM.class, this::getAttributesPotentialGNM);
		addMember(AttributesPotentialRingExperiment.class, this::getAttributesPotentialRingExperiment);
		addMember(AttributesGFM.class, this::getAttributesGFM);
		addMember(AttributesSTOM.class, this::getAttributesSTOM);
		addMember(AttributesParticles.class, this::getAttributesParticles);
	}


	// Getters
	public AttributesPotentialSFM getAttributesPotentialSFM(){
		return new AttributesPotentialSFM();
	}

	public AttributesPotentialParticles getAttributesPotentialParticles(){
		return new AttributesPotentialParticles();
	}

	public AttributesPotentialOSM getAttributesPotentialOSM(){
		return new AttributesPotentialOSM();
	}

	public AttributesCA getAttributesCA(){
		return new AttributesCA();
	}

	public AttributesOVM getAttributesOVM(){
		return new AttributesOVM();
	}

	public AttributesOSM getAttributesOSM(){
		return new AttributesOSM();
	}

	public AttributesReynolds getAttributesReynolds(){
		return new AttributesReynolds();
	}

	public AttributesGNM getAttributesGNM(){
		return new AttributesGNM();
	}

	public AttributesQueuingGame getAttributesQueuingGame(){
		return new AttributesQueuingGame();
	}

	public AttributesBMM getAttributesBMM(){
		return new AttributesBMM();
	}

	public AttributesSeating getAttributesSeating(){
		return new AttributesSeating();
	}

	public AttributesODEIntegrator getAttributesODEIntegrator(){
		return new AttributesODEIntegrator();
	}

	public AttributesFloorField getAttributesFloorField(){
		return new AttributesFloorField();
	}

	public AttributesSFM getAttributesSFM(){
		return new AttributesSFM();
	}

	public AttributesBHM getAttributesBHM(){
		return new AttributesBHM();
	}

	public AttributesTimeCost getAttributesTimeCost(){
		return new AttributesTimeCost();
	}

	public AttributesPotentialCompactSoftshell getAttributesPotentialCompactSoftshell(){
		return new AttributesPotentialCompactSoftshell();
	}

	public AttributesSingleTargetStrategy getAttributesSingleTargetStrategy(){
		return new AttributesSingleTargetStrategy();
	}

	public AttributesSIRG getAttributesSIRG(){
		return new AttributesSIRG();
	}

	public AttributesCGM getAttributesCGM(){
		return new AttributesCGM();
	}

	public AttributesPotentialCompact getAttributesPotentialCompact(){
		return new AttributesPotentialCompact();
	}

	public AttributesPotentialGNM getAttributesPotentialGNM(){
		return new AttributesPotentialGNM();
	}

	public AttributesPotentialRingExperiment getAttributesPotentialRingExperiment(){
		return new AttributesPotentialRingExperiment();
	}

	public AttributesGFM getAttributesGFM(){
		return new AttributesGFM();
	}

	public AttributesSTOM getAttributesSTOM(){
		return new AttributesSTOM();
	}

	public AttributesParticles getAttributesParticles(){
		return new AttributesParticles();
	}


}
