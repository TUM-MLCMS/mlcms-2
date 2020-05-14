package org.vadere.simulator.entrypoints;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

/**
 * Versions in strict order from oldest to newest.
 */
public enum Version {



	UNDEFINED("undefined"),
	NOT_A_RELEASE("not a release"),
	V0_1(0, 1),
	V0_2(0, 2),
	V0_3(0, 3),
	V0_4(0, 4),
	V0_5(0, 5),
	V0_6(0, 6),
	V0_7(0, 7),
	V0_8(0, 8),
	V0_9(0,9),
	V0_10(0,10),
	V1_0(1,0)
	;



	private String label;
	private int major;
	private int minor;


	Version(String label) {
		this.major = -1;
		this.minor = -1;
		this.label = label;
	}

	Version(int major, int minor){
		this.major = major;
		this.minor = minor;
		this.label = major + "." + minor;
	}

	public String label() {
		return label;
	}

	public int major() { return major;}

	public int minor() { return  minor;}

	public String label(char replaceSpaceWith) {
		return label.replace(' ', replaceSpaceWith);
	}

	public static Version fromString(String versionStr) {
		versionStr = versionStr.replace('_', ' ');
		for (Version v : values()) {
			if (v.label.equals(versionStr))
				return v;
		}
		return null;
	}

	private static int versionId(Version curr) {
		Version[] versions = values();
		for (int i = 0; i < versions.length; i++) {
			if (curr.equals(versions[i])) {
				return i;
			}
		}
		throw new IllegalArgumentException("Value not in Version Enumeration " + curr.toString());
	}

	public static String[] stringValues() {
		return Arrays.stream(values()).map(v -> v.label().replace(' ', '_')).toArray(String[]::new);
	}

	public static String[] stringValues(Version startFrom, boolean descending) {
		int min = startFrom.ordinal();
		String[] values = Arrays.stream(values()).filter(v -> v.ordinal() >= min).map(v -> v.label().replace(' ', '_')).toArray(String[]::new);
		if (descending){
 		    int length = values.length;
		    for (int i = 0; i < length/2; i++){
		        int j = length -1 - i;
		        String tmp = values[i];
		        values[i] = values[j];
		        values[j] = tmp;


            }
        }
		return values;
    }



	public Version nextVersion() {
		int nextId = versionId(this) == (values().length - 1) ? versionId(this) : versionId(this) + 1;
		return values()[nextId];
	}

	public Version previousVersion() {
		int nextId = versionId(this) == 0 ? versionId(this) : versionId(this) - 1;
		return values()[nextId];
	}

	public static Version[] listVersionFromTo(Version from, Version to){
		int start = versionId(from) == (values().length - 1) ? versionId(from) : versionId(from) + 1;
		int end =  versionId(to);
		Version[] ret = new Version[(end - start) + 1];
		Version[] values = values();
		System.arraycopy(values, start, ret, 0, (end - start)+1);
		return ret;
	}

	public static Version[] listToLatest(Version v) {
		int start = versionId(v) == (values().length - 1) ? versionId(v) : versionId(v) + 1;
		int end = values().length;
		Version[] ret = new Version[end - start];
		System.arraycopy(values(), start, ret, 0, end - start);
		return ret;

	}

	public boolean equalOrSmaller(Version test) {
		return this.ordinal() <= test.ordinal();
	}

	public boolean equalOrBigger(Version test) {
		return this.ordinal() >= test.ordinal();
	}

	public static Version latest() {
		return values()[values().length - 1];
	}

	public static Optional<Version> getPrevious(@NotNull final Version successorVersion) {
		Version prevVersion = null;

		for (Version version : values()) {
			if (successorVersion.equals(version)) {
				return Optional.ofNullable(prevVersion);
			}
			prevVersion = version;
		}

		return Optional.empty();
	}

	@Override
	public String toString() {
		return label();
	}

}
