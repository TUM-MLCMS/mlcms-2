package org.vadere.simulator.projects.dataprocessing.processor;

import org.vadere.simulator.projects.dataprocessing.processor.DataProcessor;
import org.vadere.util.factory.processors.ProcessorBaseFactory;

import org.vadere.simulator.projects.dataprocessing.processor.PedestrianGroupSizeProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.MeshProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianGroupMaxDistProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.FundamentalDiagramEProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.BonnMotionTrajectoryProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianTargetIdProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.FlowProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.tests.TestNumberOverlapsProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianSourceIdProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianOSMStrideLengthProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianWaitingEndTimeProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.tests.TestEvacuationTimeProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianSpeedInAreaProcessorUsingAgentVelocity;
import org.vadere.simulator.projects.dataprocessing.processor.tests.TestOptimizationMetricNelderMeadProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianCrossingTimeProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianLineCrossProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianPositionProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.FootStepPsychologyStatusProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianFlowProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.MeshDensityCountingProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianVelocityDefaultProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianBehaviourProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.FootStepMostImportantStimulusProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianVelocityProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.MaxOverlapProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.FundamentalDiagramAProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.FundamentalDiagramDProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.EvacuationTimeProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianEvacuationTimeProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.AreaDensityGridCountingProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianLastPositionProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.GroupMemberSeparatedByObstacle;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianOffsetPositionProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianStartTimeProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.MaxAreaDensityVoronoiProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianTrajectoryProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianDensityCountingProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.MeanPedestrianEvacuationTimeProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.tests.TestPedestrianWaitingTimeProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianEndTimeProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianOverlapProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.AreaSpeedProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianMetricOptimizationProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedStimulusCountingProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianSpeedInAreaProcessorUsingAgentTrajectory;
import org.vadere.simulator.projects.dataprocessing.processor.FootStepGroupIDProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianWaitingTimeProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianPotentialProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianGroupIDProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.NumberOverlapsProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.FootStepTargetIDProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.MeanAreaDensityVoronoiProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.GroupMemberEuclideanDist;
import org.vadere.simulator.projects.dataprocessing.processor.GroupMemberPotentialDist;
import org.vadere.simulator.projects.dataprocessing.processor.FootStepGroupSizeProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.MeshPedStimulusCountingProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.TargetFloorFieldGridProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.FundamentalDiagramCProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianVelocityByTrajectoryProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianStateProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.AreaDensityCountingProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.FootStepSelfCategoryProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.tests.TestPedestrianEvacuationTimeProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.FootStepProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.QueueWidthProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.MeanFlowProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.PedestrianDensityGaussianProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.AreaDensityVoronoiProcessor;
import org.vadere.simulator.projects.dataprocessing.processor.FundamentalDiagramBProcessor;

import org.vadere.simulator.projects.dataprocessing.store.DataProcessorStore;


public class DataProcessorFactory extends ProcessorBaseFactory<DataProcessor<?, ?>> {


	private static DataProcessorFactory instance;

	//good performance threadsafe Singletone. Sync block will only be used once
	public static DataProcessorFactory instance(){
		if(instance ==  null){
			synchronized (DataProcessorFactory.class){
				if(instance == null){
					instance = new DataProcessorFactory();
				}
			}
		}
		return instance;
	}


	private DataProcessorFactory(){

		addMember(this::getPedestrianGroupSizeProcessor, "PedestrianGroupSizeProcessor", "", PedestrianGroupSizeProcessor.class); 
		addMember(this::getMeshProcessor, "MeshProcessor", "", MeshProcessor.class); 
		addMember(this::getPedestrianGroupMaxDistProcessor, "PedestrianGroupMaxDistProcessor", "", PedestrianGroupMaxDistProcessor.class); 
		addMember(this::getFundamentalDiagramEProcessor, "FundamentalDiagramEProcessor", "", FundamentalDiagramEProcessor.class, "UsesMeasurementArea"); 
		addMember(this::getBonnMotionTrajectoryProcessor, "BonnMotionTrajectoryProcessor", "", BonnMotionTrajectoryProcessor.class); 
		addMember(this::getPedestrianTargetIdProcessor, "PedestrianTargetIdProcessor", "", PedestrianTargetIdProcessor.class); 
		addMember(this::getFlowProcessor, "FlowProcessor", "", FlowProcessor.class); 
		addMember(this::getTestNumberOverlapsProcessor, "TestNumberOverlapsProcessor", "", TestNumberOverlapsProcessor.class); 
		addMember(this::getPedestrianSourceIdProcessor, "PedestrianSourceIdProcessor", "", PedestrianSourceIdProcessor.class); 
		addMember(this::getPedestrianOSMStrideLengthProcessor, "PedestrianOSMStrideLengthProcessor", "", PedestrianOSMStrideLengthProcessor.class); 
		addMember(this::getPedestrianWaitingEndTimeProcessor, "PedestrianWaitingEndTimeProcessor", "", PedestrianWaitingEndTimeProcessor.class, "UsesMeasurementArea"); 
		addMember(this::getTestEvacuationTimeProcessor, "TestEvacuationTimeProcessor", "", TestEvacuationTimeProcessor.class); 
		addMember(this::getPedestrianSpeedInAreaProcessorUsingAgentVelocity, "PedestrianSpeedInAreaProcessorUsingAgentVelocity", "", PedestrianSpeedInAreaProcessorUsingAgentVelocity.class); 
		addMember(this::getTestOptimizationMetricNelderMeadProcessor, "TestOptimizationMetricNelderMeadProcessor", "", TestOptimizationMetricNelderMeadProcessor.class); 
		addMember(this::getPedestrianCrossingTimeProcessor, "PedestrianCrossingTimeProcessor", "", PedestrianCrossingTimeProcessor.class, "UsesMeasurementArea"); 
		addMember(this::getPedestrianLineCrossProcessor, "PedestrianLineCrossProcessor", "", PedestrianLineCrossProcessor.class); 
		addMember(this::getPedestrianPositionProcessor, "PedestrianPositionProcessor", "", PedestrianPositionProcessor.class); 
		addMember(this::getFootStepPsychologyStatusProcessor, "FootStepPsychologyStatusProcessor", "", FootStepPsychologyStatusProcessor.class); 
		addMember(this::getPedestrianFlowProcessor, "PedestrianFlowProcessor", "", PedestrianFlowProcessor.class); 
		addMember(this::getMeshDensityCountingProcessor, "MeshDensityCountingProcessor", "", MeshDensityCountingProcessor.class); 
		addMember(this::getPedestrianVelocityDefaultProcessor, "PedestrianVelocityDefaultProcessor", "", PedestrianVelocityDefaultProcessor.class); 
		addMember(this::getPedestrianBehaviourProcessor, "PedestrianBehaviourProcessor", "", PedestrianBehaviourProcessor.class); 
		addMember(this::getFootStepMostImportantStimulusProcessor, "FootStepMostImportantStimulusProcessor", "", FootStepMostImportantStimulusProcessor.class); 
		addMember(this::getPedestrianVelocityProcessor, "PedestrianVelocityProcessor", "", PedestrianVelocityProcessor.class); 
		addMember(this::getMaxOverlapProcessor, "MaxOverlapProcessor", "", MaxOverlapProcessor.class); 
		addMember(this::getFundamentalDiagramAProcessor, "FundamentalDiagramAProcessor", "", FundamentalDiagramAProcessor.class); 
		addMember(this::getFundamentalDiagramDProcessor, "FundamentalDiagramDProcessor", "", FundamentalDiagramDProcessor.class, "UsesMeasurementArea"); 
		addMember(this::getEvacuationTimeProcessor, "EvacuationTimeProcessor", "", EvacuationTimeProcessor.class); 
		addMember(this::getPedestrianEvacuationTimeProcessor, "PedestrianEvacuationTimeProcessor", "", PedestrianEvacuationTimeProcessor.class); 
		addMember(this::getAreaDensityGridCountingProcessor, "AreaDensityGridCountingProcessor", "", AreaDensityGridCountingProcessor.class); 
		addMember(this::getPedestrianLastPositionProcessor, "PedestrianLastPositionProcessor", "", PedestrianLastPositionProcessor.class); 
		addMember(this::getGroupMemberSeparatedByObstacle, "GroupMemberSeparatedByObstacle", "", GroupMemberSeparatedByObstacle.class); 
		addMember(this::getPedestrianOffsetPositionProcessor, "PedestrianOffsetPositionProcessor", "", PedestrianOffsetPositionProcessor.class); 
		addMember(this::getPedestrianStartTimeProcessor, "PedestrianStartTimeProcessor", "", PedestrianStartTimeProcessor.class); 
		addMember(this::getMaxAreaDensityVoronoiProcessor, "MaxAreaDensityVoronoiProcessor", "", MaxAreaDensityVoronoiProcessor.class); 
		addMember(this::getPedestrianTrajectoryProcessor, "PedestrianTrajectoryProcessor", "", PedestrianTrajectoryProcessor.class); 
		addMember(this::getPedestrianDensityCountingProcessor, "PedestrianDensityCountingProcessor", "", PedestrianDensityCountingProcessor.class); 
		addMember(this::getMeanPedestrianEvacuationTimeProcessor, "MeanPedestrianEvacuationTimeProcessor", "", MeanPedestrianEvacuationTimeProcessor.class); 
		addMember(this::getTestPedestrianWaitingTimeProcessor, "TestPedestrianWaitingTimeProcessor", "", TestPedestrianWaitingTimeProcessor.class); 
		addMember(this::getPedestrianEndTimeProcessor, "PedestrianEndTimeProcessor", "", PedestrianEndTimeProcessor.class); 
		addMember(this::getPedestrianOverlapProcessor, "PedestrianOverlapProcessor", "", PedestrianOverlapProcessor.class); 
		addMember(this::getAreaSpeedProcessor, "AreaSpeedProcessor", "", AreaSpeedProcessor.class); 
		addMember(this::getPedestrianMetricOptimizationProcessor, "PedestrianMetricOptimizationProcessor", "", PedestrianMetricOptimizationProcessor.class); 
		addMember(this::getPedStimulusCountingProcessor, "PedStimulusCountingProcessor", "", PedStimulusCountingProcessor.class); 
		addMember(this::getPedestrianSpeedInAreaProcessorUsingAgentTrajectory, "PedestrianSpeedInAreaProcessorUsingAgentTrajectory", "", PedestrianSpeedInAreaProcessorUsingAgentTrajectory.class); 
		addMember(this::getFootStepGroupIDProcessor, "FootStepGroupIDProcessor", "", FootStepGroupIDProcessor.class); 
		addMember(this::getPedestrianWaitingTimeProcessor, "PedestrianWaitingTimeProcessor", "", PedestrianWaitingTimeProcessor.class, "UsesMeasurementArea"); 
		addMember(this::getPedestrianPotentialProcessor, "PedestrianPotentialProcessor", "", PedestrianPotentialProcessor.class); 
		addMember(this::getPedestrianGroupIDProcessor, "PedestrianGroupIDProcessor", "", PedestrianGroupIDProcessor.class); 
		addMember(this::getNumberOverlapsProcessor, "NumberOverlapsProcessor", "", NumberOverlapsProcessor.class); 
		addMember(this::getFootStepTargetIDProcessor, "FootStepTargetIDProcessor", "", FootStepTargetIDProcessor.class); 
		addMember(this::getMeanAreaDensityVoronoiProcessor, "MeanAreaDensityVoronoiProcessor", "", MeanAreaDensityVoronoiProcessor.class); 
		addMember(this::getGroupMemberEuclideanDist, "GroupMemberEuclideanDist", "", GroupMemberEuclideanDist.class); 
		addMember(this::getGroupMemberPotentialDist, "GroupMemberPotentialDist", "", GroupMemberPotentialDist.class); 
		addMember(this::getFootStepGroupSizeProcessor, "FootStepGroupSizeProcessor", "", FootStepGroupSizeProcessor.class); 
		addMember(this::getMeshPedStimulusCountingProcessor, "MeshPedStimulusCountingProcessor", "", MeshPedStimulusCountingProcessor.class); 
		addMember(this::getTargetFloorFieldGridProcessor, "TargetFloorFieldGridProcessor", "", TargetFloorFieldGridProcessor.class); 
		addMember(this::getFundamentalDiagramCProcessor, "FundamentalDiagramCProcessor", "", FundamentalDiagramCProcessor.class, "UsesMeasurementArea"); 
		addMember(this::getPedestrianVelocityByTrajectoryProcessor, "PedestrianVelocityByTrajectoryProcessor", "", PedestrianVelocityByTrajectoryProcessor.class); 
		addMember(this::getPedestrianStateProcessor, "PedestrianStateProcessor", "", PedestrianStateProcessor.class); 
		addMember(this::getAreaDensityCountingProcessor, "AreaDensityCountingProcessor", "", AreaDensityCountingProcessor.class); 
		addMember(this::getFootStepSelfCategoryProcessor, "FootStepSelfCategoryProcessor", "", FootStepSelfCategoryProcessor.class); 
		addMember(this::getTestPedestrianEvacuationTimeProcessor, "TestPedestrianEvacuationTimeProcessor", "", TestPedestrianEvacuationTimeProcessor.class); 
		addMember(this::getFootStepProcessor, "FootStepProcessor", "", FootStepProcessor.class); 
		addMember(this::getQueueWidthProcessor, "QueueWidthProcessor", "", QueueWidthProcessor.class); 
		addMember(this::getMeanFlowProcessor, "MeanFlowProcessor", "", MeanFlowProcessor.class); 
		addMember(this::getPedestrianDensityGaussianProcessor, "PedestrianDensityGaussianProcessor", "", PedestrianDensityGaussianProcessor.class); 
		addMember(this::getAreaDensityVoronoiProcessor, "AreaDensityVoronoiProcessor", "", AreaDensityVoronoiProcessor.class, "UsesMeasurementArea"); 
		addMember(this::getFundamentalDiagramBProcessor, "FundamentalDiagramBProcessor", "", FundamentalDiagramBProcessor.class, "UsesMeasurementArea"); 
	}


	// Getters
	public PedestrianGroupSizeProcessor getPedestrianGroupSizeProcessor(){
		return new PedestrianGroupSizeProcessor();
	}

	public MeshProcessor getMeshProcessor(){
		return new MeshProcessor();
	}

	public PedestrianGroupMaxDistProcessor getPedestrianGroupMaxDistProcessor(){
		return new PedestrianGroupMaxDistProcessor();
	}

	public FundamentalDiagramEProcessor getFundamentalDiagramEProcessor(){
		return new FundamentalDiagramEProcessor();
	}

	public BonnMotionTrajectoryProcessor getBonnMotionTrajectoryProcessor(){
		return new BonnMotionTrajectoryProcessor();
	}

	public PedestrianTargetIdProcessor getPedestrianTargetIdProcessor(){
		return new PedestrianTargetIdProcessor();
	}

	public FlowProcessor getFlowProcessor(){
		return new FlowProcessor();
	}

	public TestNumberOverlapsProcessor getTestNumberOverlapsProcessor(){
		return new TestNumberOverlapsProcessor();
	}

	public PedestrianSourceIdProcessor getPedestrianSourceIdProcessor(){
		return new PedestrianSourceIdProcessor();
	}

	public PedestrianOSMStrideLengthProcessor getPedestrianOSMStrideLengthProcessor(){
		return new PedestrianOSMStrideLengthProcessor();
	}

	public PedestrianWaitingEndTimeProcessor getPedestrianWaitingEndTimeProcessor(){
		return new PedestrianWaitingEndTimeProcessor();
	}

	public TestEvacuationTimeProcessor getTestEvacuationTimeProcessor(){
		return new TestEvacuationTimeProcessor();
	}

	public PedestrianSpeedInAreaProcessorUsingAgentVelocity getPedestrianSpeedInAreaProcessorUsingAgentVelocity(){
		return new PedestrianSpeedInAreaProcessorUsingAgentVelocity();
	}

	public TestOptimizationMetricNelderMeadProcessor getTestOptimizationMetricNelderMeadProcessor(){
		return new TestOptimizationMetricNelderMeadProcessor();
	}

	public PedestrianCrossingTimeProcessor getPedestrianCrossingTimeProcessor(){
		return new PedestrianCrossingTimeProcessor();
	}

	public PedestrianLineCrossProcessor getPedestrianLineCrossProcessor(){
		return new PedestrianLineCrossProcessor();
	}

	public PedestrianPositionProcessor getPedestrianPositionProcessor(){
		return new PedestrianPositionProcessor();
	}

	public FootStepPsychologyStatusProcessor getFootStepPsychologyStatusProcessor(){
		return new FootStepPsychologyStatusProcessor();
	}

	public PedestrianFlowProcessor getPedestrianFlowProcessor(){
		return new PedestrianFlowProcessor();
	}

	public MeshDensityCountingProcessor getMeshDensityCountingProcessor(){
		return new MeshDensityCountingProcessor();
	}

	public PedestrianVelocityDefaultProcessor getPedestrianVelocityDefaultProcessor(){
		return new PedestrianVelocityDefaultProcessor();
	}

	public PedestrianBehaviourProcessor getPedestrianBehaviourProcessor(){
		return new PedestrianBehaviourProcessor();
	}

	public FootStepMostImportantStimulusProcessor getFootStepMostImportantStimulusProcessor(){
		return new FootStepMostImportantStimulusProcessor();
	}

	public PedestrianVelocityProcessor getPedestrianVelocityProcessor(){
		return new PedestrianVelocityProcessor();
	}

	public MaxOverlapProcessor getMaxOverlapProcessor(){
		return new MaxOverlapProcessor();
	}

	public FundamentalDiagramAProcessor getFundamentalDiagramAProcessor(){
		return new FundamentalDiagramAProcessor();
	}

	public FundamentalDiagramDProcessor getFundamentalDiagramDProcessor(){
		return new FundamentalDiagramDProcessor();
	}

	public EvacuationTimeProcessor getEvacuationTimeProcessor(){
		return new EvacuationTimeProcessor();
	}

	public PedestrianEvacuationTimeProcessor getPedestrianEvacuationTimeProcessor(){
		return new PedestrianEvacuationTimeProcessor();
	}

	public AreaDensityGridCountingProcessor getAreaDensityGridCountingProcessor(){
		return new AreaDensityGridCountingProcessor();
	}

	public PedestrianLastPositionProcessor getPedestrianLastPositionProcessor(){
		return new PedestrianLastPositionProcessor();
	}

	public GroupMemberSeparatedByObstacle getGroupMemberSeparatedByObstacle(){
		return new GroupMemberSeparatedByObstacle();
	}

	public PedestrianOffsetPositionProcessor getPedestrianOffsetPositionProcessor(){
		return new PedestrianOffsetPositionProcessor();
	}

	public PedestrianStartTimeProcessor getPedestrianStartTimeProcessor(){
		return new PedestrianStartTimeProcessor();
	}

	public MaxAreaDensityVoronoiProcessor getMaxAreaDensityVoronoiProcessor(){
		return new MaxAreaDensityVoronoiProcessor();
	}

	public PedestrianTrajectoryProcessor getPedestrianTrajectoryProcessor(){
		return new PedestrianTrajectoryProcessor();
	}

	public PedestrianDensityCountingProcessor getPedestrianDensityCountingProcessor(){
		return new PedestrianDensityCountingProcessor();
	}

	public MeanPedestrianEvacuationTimeProcessor getMeanPedestrianEvacuationTimeProcessor(){
		return new MeanPedestrianEvacuationTimeProcessor();
	}

	public TestPedestrianWaitingTimeProcessor getTestPedestrianWaitingTimeProcessor(){
		return new TestPedestrianWaitingTimeProcessor();
	}

	public PedestrianEndTimeProcessor getPedestrianEndTimeProcessor(){
		return new PedestrianEndTimeProcessor();
	}

	public PedestrianOverlapProcessor getPedestrianOverlapProcessor(){
		return new PedestrianOverlapProcessor();
	}

	public AreaSpeedProcessor getAreaSpeedProcessor(){
		return new AreaSpeedProcessor();
	}

	public PedestrianMetricOptimizationProcessor getPedestrianMetricOptimizationProcessor(){
		return new PedestrianMetricOptimizationProcessor();
	}

	public PedStimulusCountingProcessor getPedStimulusCountingProcessor(){
		return new PedStimulusCountingProcessor();
	}

	public PedestrianSpeedInAreaProcessorUsingAgentTrajectory getPedestrianSpeedInAreaProcessorUsingAgentTrajectory(){
		return new PedestrianSpeedInAreaProcessorUsingAgentTrajectory();
	}

	public FootStepGroupIDProcessor getFootStepGroupIDProcessor(){
		return new FootStepGroupIDProcessor();
	}

	public PedestrianWaitingTimeProcessor getPedestrianWaitingTimeProcessor(){
		return new PedestrianWaitingTimeProcessor();
	}

	public PedestrianPotentialProcessor getPedestrianPotentialProcessor(){
		return new PedestrianPotentialProcessor();
	}

	public PedestrianGroupIDProcessor getPedestrianGroupIDProcessor(){
		return new PedestrianGroupIDProcessor();
	}

	public NumberOverlapsProcessor getNumberOverlapsProcessor(){
		return new NumberOverlapsProcessor();
	}

	public FootStepTargetIDProcessor getFootStepTargetIDProcessor(){
		return new FootStepTargetIDProcessor();
	}

	public MeanAreaDensityVoronoiProcessor getMeanAreaDensityVoronoiProcessor(){
		return new MeanAreaDensityVoronoiProcessor();
	}

	public GroupMemberEuclideanDist getGroupMemberEuclideanDist(){
		return new GroupMemberEuclideanDist();
	}

	public GroupMemberPotentialDist getGroupMemberPotentialDist(){
		return new GroupMemberPotentialDist();
	}

	public FootStepGroupSizeProcessor getFootStepGroupSizeProcessor(){
		return new FootStepGroupSizeProcessor();
	}

	public MeshPedStimulusCountingProcessor getMeshPedStimulusCountingProcessor(){
		return new MeshPedStimulusCountingProcessor();
	}

	public TargetFloorFieldGridProcessor getTargetFloorFieldGridProcessor(){
		return new TargetFloorFieldGridProcessor();
	}

	public FundamentalDiagramCProcessor getFundamentalDiagramCProcessor(){
		return new FundamentalDiagramCProcessor();
	}

	public PedestrianVelocityByTrajectoryProcessor getPedestrianVelocityByTrajectoryProcessor(){
		return new PedestrianVelocityByTrajectoryProcessor();
	}

	public PedestrianStateProcessor getPedestrianStateProcessor(){
		return new PedestrianStateProcessor();
	}

	public AreaDensityCountingProcessor getAreaDensityCountingProcessor(){
		return new AreaDensityCountingProcessor();
	}

	public FootStepSelfCategoryProcessor getFootStepSelfCategoryProcessor(){
		return new FootStepSelfCategoryProcessor();
	}

	public TestPedestrianEvacuationTimeProcessor getTestPedestrianEvacuationTimeProcessor(){
		return new TestPedestrianEvacuationTimeProcessor();
	}

	public FootStepProcessor getFootStepProcessor(){
		return new FootStepProcessor();
	}

	public QueueWidthProcessor getQueueWidthProcessor(){
		return new QueueWidthProcessor();
	}

	public MeanFlowProcessor getMeanFlowProcessor(){
		return new MeanFlowProcessor();
	}

	public PedestrianDensityGaussianProcessor getPedestrianDensityGaussianProcessor(){
		return new PedestrianDensityGaussianProcessor();
	}

	public AreaDensityVoronoiProcessor getAreaDensityVoronoiProcessor(){
		return new AreaDensityVoronoiProcessor();
	}

	public FundamentalDiagramBProcessor getFundamentalDiagramBProcessor(){
		return new FundamentalDiagramBProcessor();
	}

	public DataProcessor<?, ?> createDataProcessor(DataProcessorStore dataProcessorStore) throws ClassNotFoundException {
		DataProcessor<?, ?> processor = getInstanceOf(dataProcessorStore.getType());
		processor.setId(dataProcessorStore.getId());
		processor.setAttributes(dataProcessorStore.getAttributes());
		return processor;
	}


	public DataProcessor<?, ?> createDataProcessor(String type) throws ClassNotFoundException {
		DataProcessorStore dataProcessorStore = new DataProcessorStore();
		dataProcessorStore.setType(type);
		DataProcessor<?, ?> processor = getInstanceOf(dataProcessorStore.getType());
		processor.setId(dataProcessorStore.getId());
		processor.setAttributes(dataProcessorStore.getAttributes());
		return processor;
	}

	public DataProcessor<?, ?> createDataProcessor(Class type) throws ClassNotFoundException {
		return createDataProcessor(type.getCanonicalName());
	}

}
