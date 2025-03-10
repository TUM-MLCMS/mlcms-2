package org.vadere.simulator.projects.dataprocessing.outputfile;

import org.vadere.simulator.projects.dataprocessing.outputfile.OutputFile;
import org.vadere.simulator.projects.dataprocessing.datakey.DataKey;
import org.vadere.util.factory.outputfiles.OutputFileBaseFactory;

import org.vadere.simulator.projects.dataprocessing.outputfile.TimestepPositionOutputFile;
import org.vadere.simulator.projects.dataprocessing.outputfile.NoDataKeyOutputFile;
import org.vadere.simulator.projects.dataprocessing.outputfile.BonnMotionTrajectoryFile;
import org.vadere.simulator.projects.dataprocessing.outputfile.TimestepRowOutputFile;
import org.vadere.simulator.projects.dataprocessing.outputfile.TimestepPedestrianIdOutputFile;
import org.vadere.simulator.projects.dataprocessing.outputfile.TimestepPedestrianIdOverlapOutputFile;
import org.vadere.simulator.projects.dataprocessing.outputfile.PedestrianIdOutputFile;
import org.vadere.simulator.projects.dataprocessing.outputfile.TimeGridOutputFile;
import org.vadere.simulator.projects.dataprocessing.outputfile.IdOutputFile;
import org.vadere.simulator.projects.dataprocessing.outputfile.TimestepOutputFile;
import org.vadere.simulator.projects.dataprocessing.outputfile.TimestepKeyIdOutputFile;
import org.vadere.simulator.projects.dataprocessing.outputfile.EventtimePedestrianIdOutputFile;
import org.vadere.simulator.projects.dataprocessing.outputfile.GroupPairOutputFile;

import org.vadere.simulator.projects.dataprocessing.datakey.DataKey;
import org.vadere.simulator.projects.dataprocessing.datakey.OutputFileMap;
import org.vadere.simulator.projects.dataprocessing.store.OutputFileStore;
import java.util.Arrays;
import java.util.HashMap;


public class OutputFileFactory extends OutputFileBaseFactory<OutputFile<? extends DataKey<?>>> {


	private static OutputFileFactory instance;

	//good performance threadsafe Singletone. Sync block will only be used once
	public static OutputFileFactory instance(){
		if(instance ==  null){
			synchronized (OutputFileFactory.class){
				if(instance == null){
					instance = new OutputFileFactory();
				}
			}
		}
		return instance;
	}


	private OutputFileFactory(){

		addMember(TimestepPositionOutputFile.class, this::getTimestepPositionOutputFile, "TimestepPositionOutputFile", "", "TimestepPositionKey");
		addMember(NoDataKeyOutputFile.class, this::getNoDataKeyOutputFile, "NoDataKeyOutputFile", "", "NoDataKey");
		addMember(BonnMotionTrajectoryFile.class, this::getBonnMotionTrajectoryFile, "BonnMotionTrajectoryFile", "", "BonnMotionKey");
		addMember(TimestepRowOutputFile.class, this::getTimestepRowOutputFile, "TimestepRowOutputFile", "", "TimestepRowKey");
		addMember(TimestepPedestrianIdOutputFile.class, this::getTimestepPedestrianIdOutputFile, "TimestepPedestrianIdOutputFile", "", "TimestepPedestrianIdKey");
		addMember(TimestepPedestrianIdOverlapOutputFile.class, this::getTimestepPedestrianIdOverlapOutputFile, "TimestepPedestrianIdOverlapOutputFile", "", "TimestepPedestrianIdOverlapKey");
		addMember(PedestrianIdOutputFile.class, this::getPedestrianIdOutputFile, "PedestrianIdOutputFile", "", "PedestrianIdKey");
		addMember(TimeGridOutputFile.class, this::getTimeGridOutputFile, "TimeGridOutputFile", "", "TimeGridKey");
		addMember(IdOutputFile.class, this::getIdOutputFile, "IdOutputFile", "", "IdDataKey");
		addMember(TimestepOutputFile.class, this::getTimestepOutputFile, "TimestepOutputFile", "", "TimestepKey");
		addMember(TimestepKeyIdOutputFile.class, this::getTimestepKeyIdOutputFile, "TimestepKeyIdOutputFile", "", "TimestepFaceIdKey");
		addMember(EventtimePedestrianIdOutputFile.class, this::getEventtimePedestrianIdOutputFile, "EventtimePedestrianIdOutputFile", "", "EventtimePedestrianIdKey");
		addMember(GroupPairOutputFile.class, this::getGroupPairOutputFile, "GroupPairOutputFile", "", "TimestepGroupPairKey");
	}


	// Getters
	public TimestepPositionOutputFile getTimestepPositionOutputFile(){
		return new TimestepPositionOutputFile();
	}

	public NoDataKeyOutputFile getNoDataKeyOutputFile(){
		return new NoDataKeyOutputFile();
	}

	public BonnMotionTrajectoryFile getBonnMotionTrajectoryFile(){
		return new BonnMotionTrajectoryFile();
	}

	public TimestepRowOutputFile getTimestepRowOutputFile(){
		return new TimestepRowOutputFile();
	}

	public TimestepPedestrianIdOutputFile getTimestepPedestrianIdOutputFile(){
		return new TimestepPedestrianIdOutputFile();
	}

	public TimestepPedestrianIdOverlapOutputFile getTimestepPedestrianIdOverlapOutputFile(){
		return new TimestepPedestrianIdOverlapOutputFile();
	}

	public PedestrianIdOutputFile getPedestrianIdOutputFile(){
		return new PedestrianIdOutputFile();
	}

	public TimeGridOutputFile getTimeGridOutputFile(){
		return new TimeGridOutputFile();
	}

	public IdOutputFile getIdOutputFile(){
		return new IdOutputFile();
	}

	public TimestepOutputFile getTimestepOutputFile(){
		return new TimestepOutputFile();
	}

	public TimestepKeyIdOutputFile getTimestepKeyIdOutputFile(){
		return new TimestepKeyIdOutputFile();
	}

	public EventtimePedestrianIdOutputFile getEventtimePedestrianIdOutputFile(){
		return new EventtimePedestrianIdOutputFile();
	}

	public GroupPairOutputFile getGroupPairOutputFile(){
		return new GroupPairOutputFile();
	}

	public OutputFile<?> createOutputfile(OutputFileStore fileStore) throws ClassNotFoundException {
		OutputFile<?> file = getInstanceOf(fileStore.getType());
		file.setRelativeFileName(fileStore.getFilename());
		file.setProcessorIds(fileStore.getProcessors());
		file.setSeparator(fileStore.getSeparator());
		return file;
	}

	public OutputFile<?> createDefaultOutputfile() throws ClassNotFoundException {
		OutputFileStore fileStore = new OutputFileStore();
		OutputFile<?> file = getInstanceOf(fileStore.getType());
		file.setSeparator(fileStore.getSeparator());
		return file;
	}

	public OutputFile<?> createOutputfile(String type) throws ClassNotFoundException {
		OutputFileStore fileStore = new OutputFileStore();
		fileStore.setType(type);
		OutputFile<?> file = getInstanceOf(fileStore.getType());
		file.setSeparator(fileStore.getSeparator());
		return file;
	}

	public OutputFile<?> createOutputfile(Class type) throws ClassNotFoundException {
		return createOutputfile(type.getCanonicalName());
	}

	public OutputFile<?> createOutputfile(Class type, Integer... processorsIds) throws ClassNotFoundException {
		OutputFile<?> file = createOutputfile(type.getCanonicalName());
		file.setProcessorIds(Arrays.asList(processorsIds));
		return file;
	}

	public OutputFile<?> createDefaultOutputfileByDataKey(Class<? extends DataKey<?>> keyType, Integer... processorsIds) throws ClassNotFoundException {
		OutputFile<?> file = createDefaultOutputfileByDataKey(keyType);
		file.setProcessorIds(Arrays.asList(processorsIds));
		return file;
	}

	public OutputFile<?> createDefaultOutputfileByDataKey(Class<? extends DataKey<?>> keyType) throws ClassNotFoundException {

		OutputFileMap outputFileMap = keyType.getAnnotation(OutputFileMap.class);
		return createOutputfile(outputFileMap.outputFileClass());
	}

}
