package pl.caltha.cyklotron.upgrader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

/**
 * CyklotronVerision enum
 * 
 * @author lukasz
 *
 */
public enum CyklotronVerision {

	UNDEFINED(""), CYKLOTRON_2_8("2.8"), CYKLOTRON_2_9_4("2.9.4"), CYKLOTRON_2_10_1(
			"2.10.1"), CYKLOTRON_2_11("2.11"), CYKLOTRON_2_12("2.12"), CYKLOTRON_2_13_4(
			"2.13.4"), CYKLOTRON_2_14("2.14"), CYKLOTRON_2_15_2("2.15.2"), CYKLOTRON_2_16_5(
			"2.16.5"), CYKLOTRON_2_17_2("2.17.2"), CYKLOTRON_2_18_1("2.18.1"), CYKLOTRON_2_19_7(
			"2.19.7"), CYKLOTRON_2_21_2("2.21.2"), CYKLOTRON_2_22_5("2.22.5"), CYKLOTRON_2_23_0(
			"2.23.0"), CYKLOTRON_2_24_16("2.24.16"), CYKLOTRON_2_25_0("2.25.0"), CYKLOTRON_2_26_6(
			"2.26.6"), CYKLOTRON_2_27_6("2.27.6"), CYKLOTRON_2_28_0("2.28.0"), CYKLOTRON_2_28_4(
			"2.28.4");

	/**
	 * All versions enum's map
	 */
	private static Map<DefaultArtifactVersion, CyklotronVerision> versionsMap = new HashMap<DefaultArtifactVersion, CyklotronVerision>();
	static {
		CyklotronVerision[] v = CyklotronVerision.values();
		for (int i = 0; i < v.length; i++) {
			versionsMap.put(v[i].version, v[i]);
		}
	}

	private DefaultArtifactVersion version;

	private CyklotronVerision(String version_name) {
		this.version = new DefaultArtifactVersion(version_name);
	}

	/**
	 * Check if this CyklotronVerision is lower than v
	 * 
	 * @param v
	 *            CyklotronVerision
	 * @return boolean
	 */
	public boolean lower(CyklotronVerision v) {
		return this.version.compareTo(v.version) < 0;
	}

	/**
	 * Get CyklotronVerision enum from version_name
	 * 
	 * @param version_name
	 * @return CyklotronVerision
	 */
	public static CyklotronVerision fromVersionName(String version_name) {
		DefaultArtifactVersion v = new DefaultArtifactVersion(version_name);
		return versionsMap.containsKey(v) ? versionsMap.get(v)
				: CyklotronVerision.UNDEFINED;
	}

	/**
	 * List version_name's
	 * 
	 * @param version_name
	 * @return CyklotronVerision
	 */
	public static String listVersionNames() {
		List<DefaultArtifactVersion> versions = new ArrayList<DefaultArtifactVersion>(
				versionsMap.keySet());
		versions.remove(new DefaultArtifactVersion(""));
		Collections.sort(versions);
		return versions.toString();
	}

}
