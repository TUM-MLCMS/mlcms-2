package org.vadere.simulator.projects.io;

import org.vadere.simulator.projects.ProjectOutput;
import org.vadere.simulator.projects.Scenario;
import org.vadere.simulator.projects.VadereProject;
import org.vadere.simulator.projects.migration.MigrationAssistant;
import org.vadere.simulator.projects.migration.MigrationOptions;
import org.vadere.simulator.projects.migration.MigrationResult;
import org.vadere.util.io.IOUtils;
import org.vadere.util.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IOVadere {

	private static Logger logger = Logger.getLogger(IOVadere.class);

	public static Scenario fromJson(final String json) throws IOException, IllegalArgumentException {
		try {
			return JsonConverter.deserializeScenarioRunManager(json);
		}
		catch (Exception e) {
			logger.warn("could not deserialize " + json);
			throw e;
		}
	}

	public static VadereProject readProjectJson(final String filepath) throws IOException {
		return readProjectJson(filepath, MigrationOptions.defaultOptions());
	}

	public static VadereProject readProjectJson(final String filepath, final MigrationOptions options)
			throws IOException {

		Path p = Paths.get(filepath);
		if (!Files.isDirectory(p))
			p = p.getParent();

		return IOVadere.readProject(p.toString(), options);
	}

	public static VadereProject readProject(final String folderpath) throws IOException {
		return readProject(folderpath, MigrationOptions.defaultOptions());
	}

	public static VadereProject readProject(final String folderpath, final MigrationOptions options) throws IOException {
		String name = IOUtils.readTextFile(Paths.get(folderpath, IOUtils.VADERE_PROJECT_FILENAME).toString());
		logger.info("read .project file");

		List<Scenario> scenarios = new ArrayList<>();
		Set<String> scenarioNames = new HashSet<>();
		Path p = Paths.get(folderpath, IOUtils.SCENARIO_DIR);
		MigrationResult migrationStats = new MigrationResult();
		if (Files.isDirectory(p)) {

			MigrationAssistant migrationAssistant = MigrationAssistant.getNewInstance(options);
			migrationStats = migrationAssistant.analyzeProject(folderpath);
			logger.info("analysed .scenario files");
			for (File file : IOUtils.getFilesInScenarioDirectory(p)) {
				try {
					Scenario scenario =
							JsonConverter.deserializeScenarioRunManager(IOUtils.readTextFile(file.getAbsolutePath()));
					if (!scenarioNames.add(scenario.getName())) {
						logger.error("there are two scenarios with the same name!");
						throw new IOException("Found two scenarios with the same name.");
					}
					scenarios.add(scenario);
				} catch (Exception e) {
					logger.error("could not read " + file.getAbsolutePath());
					throw e;
				}
			}
		}

		VadereProject project = new VadereProject(name, scenarios);
		logger.info(migrationStats.toString());
		project.setMigrationStats(migrationStats); // TODO [priority=low] [task=refactoring] better way to tunnel those results to the GUI?
		project.setOutputDir(Paths.get(folderpath, IOUtils.OUTPUT_DIR));
		ProjectOutput projectOutput = new ProjectOutput(project);
		project.setProjectOutput(projectOutput);
		logger.info("project loaded: " + project.getName());
		return project;
	}

}
