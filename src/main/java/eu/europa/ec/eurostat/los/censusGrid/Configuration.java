package eu.europa.ec.eurostat.los.censusGrid;

import java.util.HashMap;
import java.util.Map;

public class Configuration {

	// CSV file with POLYGON borders in WKT, provided by IGN
	public static String INPUT_FILENAME = "D:\\rco0ck\\Mes Documents\\hackathon\\" + "200m-carreaux-metropole.csv";

	// Output file
	public static final String OUTPUT_FILENAME = "D:\\rco0ck\\Mes Documents\\hackathon\\output"+ ".ttl";


	public static String createUri(String id) {
		return "http://id.los.org/" + id.toLowerCase();
	}
	public static String geometryURI(String id) {
		return createUri(id) + "/geometry";
	}
}
