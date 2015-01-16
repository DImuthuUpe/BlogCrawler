import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

/**
 * Created by dimuthuupeksha on 1/16/15.
 */
public class JettyServer {
    Server server;

    public void start() throws Exception{
        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setResourceBase("/Users/dimuthuupeksha/Documents/Academic/FYP/crawlers/Blog/BlogCrawler/src/test/resources/hathmaluwa");

        // Add the ResourceHandler to the server.
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });

        server = new Server(9880);
        server.setHandler(handlers);
        server.start();
    }

    public void stop() throws Exception{
            server.stop();

    }
}
