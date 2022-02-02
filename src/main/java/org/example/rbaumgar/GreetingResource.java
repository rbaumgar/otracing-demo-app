package org.example.rbaumgar;

import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.rest.client.RestClientBuilder;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

import io.quarkus.logging.Log;

import org.eclipse.microprofile.opentracing.Traced;

@Path("/")
//@Traced
public class GreetingResource {

    @GET
    @Path("hello")
    @Produces(MediaType.TEXT_PLAIN)
    @Traced
    public String hello() {
        Log.info("hello");
        return "hello";
    }

    @GET
    @Path("hello/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@PathParam("name") String name) {
        Span span = tracer.activeSpan();
        Log.info("hello: " + name);
        span.setTag("name", name);
        return "hello: " + name;
    }

    @Context
    private UriInfo uriInfo;

    private Tracer tracer = GlobalTracer.get();

    @GET
    @Path("sayHello/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String sayHello(@PathParam("name") String name) {

            Span span = tracer.activeSpan();

            span.setTag("event", "sayHello/{name}");
            span.log("this is a log message for name " + name);

            String response = formatGreeting(name);
            span.setTag("response", response);
            
            return response;
    }


    @GET
    @Path("sayRemote/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String sayRemote(@PathParam("name") String name) {

            Span span = tracer.activeSpan();

            span.setTag("event", "sayRemote/{name}");
            span.log("this is a log message for name " + name);

            String serviceName = System.getenv("SERVICE_NAME");
            if (serviceName == null) {
                serviceName = uriInfo.getBaseUri().toString();
            }

            URL myURL = null;
            try {
                myURL = new URL(serviceName);
            } catch (MalformedURLException e) {
                // Auto-generated catch block
                e.printStackTrace();
            }
            ResourceClient resourceClient = RestClientBuilder.newBuilder()
                    .baseUrl(myURL)
                    .build(ResourceClient.class);
            String response = resourceClient.hello(name) + " from " + serviceName;
            span.setTag("response", response);
            
            return response;
    }

    private String formatGreeting(String name) {
        Span span = tracer.buildSpan("format-greeting").start();
        try (Scope scope = tracer.scopeManager().activate(span)) {
    
            span.log("formatting message locally for name " + name);
            String response = "hello: " + name;
            return response;

        } finally {
            // Optionally finish the Span if the operation it represents
            // is logically completed at this point.
            span.finish();
        }
    }

    @GET
    @Path("chain")
    @Produces(MediaType.TEXT_PLAIN)
    public String chain() {
        Log.info("Uri: " + uriInfo.getBaseUri());
        ResourceClient resourceClient = RestClientBuilder.newBuilder()
                .baseUri(uriInfo.getBaseUri())
                .build(ResourceClient.class);
        return "chain -> " + resourceClient.hello("test");
    }

}