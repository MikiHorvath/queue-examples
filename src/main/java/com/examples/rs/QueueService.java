package com.examples.rs;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/queue")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface QueueService {

	@GET
	@Path("/ping")
	void ping();
	
	@GET
	@Path("/messages/generate/mdb")
	public void generateMessagesToMDBQueue();
        
        @GET
        @Path("/messages/mdb/consumers/start")
        public void startListenersForMDBQueue();
	
	@GET
	@Path("/messages/generate/selector")
	public void generateMessagesToSelectorQueue();
	
	@GET
	@Path("/messages/generate/selector/special")
	public void generateMessagesToSelectorQueueSpecial();

	@GET
	@Path("/selectors/register")
	public void registerSelectors();
	
	@GET
	@Path("/selectors/start")
	public void startSelectors();
	
	@GET
	@Path("/selectors/pause")
	public void pauseSelectors();
	
	@GET
	@Path("/selectors/stop")
	public void stopSelectors();
	
	@GET
	@Path("/test")
	public void doTest();
}
