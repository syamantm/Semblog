package ac.uk.soton.ecs.sw.semblog.tstore.api;

public interface IRdfPersisterController {
	
	public boolean persistRdf(String url, IRdfStore rdfStore);

}
