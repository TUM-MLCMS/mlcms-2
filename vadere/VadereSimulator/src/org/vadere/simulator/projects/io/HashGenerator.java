package org.vadere.simulator.projects.io;

import org.vadere.simulator.entrypoints.Version;
import org.vadere.util.logging.Logger;

import java.io.InputStream;
import java.util.Scanner;

public class HashGenerator {

	private static Logger logger = Logger.getLogger(HashGenerator.class);

	private static final String CURRENT_COMMIT_HASH_RESOURCE = "/current_commit_hash.txt";

	public static String commitHash() {
		String commitHash = getFirstStringTokenFromResource(CURRENT_COMMIT_HASH_RESOURCE);

		if (commitHash == null) {
			commitHash = "topographyWarning: no commit hash";
			logger.warn("No commit hash found. The project will not contain a hash of the software source code.");
		}

		return commitHash;
	}

	public static String releaseNumber() {
		return Version.latest().label();
	}

	private static String getFirstStringTokenFromResource(String resource) {
		final InputStream in = HashGenerator.class.getResourceAsStream(resource);
		if (in != null) {
			try (final Scanner scanner = new Scanner(in)) {
				if (scanner.hasNext()) {
					return scanner.next();
				}
			}
		}
		return null;
	}
}