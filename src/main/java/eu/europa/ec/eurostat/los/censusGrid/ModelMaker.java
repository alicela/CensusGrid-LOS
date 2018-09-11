package eu.europa.ec.eurostat.los.censusGrid;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.DC;
import org.apache.jena.vocabulary.RDFS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModelMaker {

	// Namespaces
	public static final String GEOSPARQL_URI = "http://www.opengis.net/ont/geosparql#";
	public static final String GN_URI = "http://www.geonames.org/ontology#";

	
	// Useful classes and property from the GeoSPARQL ontology
	static Resource feature = ResourceFactory.createResource(GEOSPARQL_URI + "Feature");
	static Resource geometry = ResourceFactory.createResource(GEOSPARQL_URI + "Geometry");
	static Property hasGeometry = ResourceFactory.createProperty(GEOSPARQL_URI + "hasGeometry");
	static Property asWKT = ResourceFactory.createProperty(GEOSPARQL_URI + "asWKT");
	static Property hasPop = ResourceFactory.createProperty(GN_URI + "population");
	static String wktDatatypeURI = GEOSPARQL_URI + "wktLiteral";

	private static Logger logger = LogManager.getLogger(ModelMaker.class);

	/**
	 * Creates the GeoSPARQL model and saves it as a Turtle file.
	 * 
	 * @param args Not used.
	 * @throws Exception In case of problem.
	 */
	public static void main(String[] args) throws Exception {
		logger.info("Creating a RDF model from polygons in csv");

		FileOutputStream fos = new FileOutputStream(Configuration.OUTPUT_FILENAME);
		
		Model model = ModelFactory.createDefaultModel();
		model.setNsPrefix("rdfs", RDFS.getURI());
		model.setNsPrefix("geo", GEOSPARQL_URI);
		model.setNsPrefix("dc", DC.getURI());

		// Now read the file to get the geographic data
		logger.info("Reading input geographic data from file " + Configuration.INPUT_FILENAME);
		Reader in = new FileReader(Configuration.INPUT_FILENAME);
		Iterable<CSVRecord> records = CSVFormat.newFormat(';').withQuote(null).withHeader().parse(in);
		Integer nb = 0;
		for (CSVRecord record : records) {
			if (nb % 100 == 0) {logger.debug(nb);}
			String id = record.get("idINSPIRE");
		    Resource gridResource = model.createResource(Configuration.createUri(id), feature);
		    gridResource.addProperty(DC.identifier, id);
		    Resource geometryResource = model.createResource(Configuration.geometryURI(id), geometry);
		    geometryResource.addProperty(RDFS.label,model.createLiteral("Geometry for polygon " + id, "en"));
		    gridResource.addProperty(hasGeometry, geometryResource);
		    gridResource.addProperty(hasPop, record.get("ind_c"));
		    geometryResource.addProperty(asWKT, model.createTypedLiteral(record.get("WKT").replace("\"", ""), wktDatatypeURI));
		    RDFDataMgr.write(fos, model, Lang.TURTLE);
		    model.removeAll().removeNsPrefix("rdfs").removeNsPrefix("geo").removeNsPrefix("dc");
		    nb++;
			
		}
		logger.info("model saved under " + Configuration.OUTPUT_FILENAME);
	}
}