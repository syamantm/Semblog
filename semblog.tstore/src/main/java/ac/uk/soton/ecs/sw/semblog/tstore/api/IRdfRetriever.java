package ac.uk.soton.ecs.sw.semblog.tstore.api;


public interface IRdfRetriever {
	
	public java.sql.Date getCreationDate(ILink blog);
	
	public String getPredicate(ILink blog, ILink webpage);

}
