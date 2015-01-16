/**
 * Created by dimuthuupeksha on 1/15/15.
 */
import corpus.sinhala.crawler.blog.controller.HathmaluwaParser;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;


import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestDocument {
    private JettyServer server;
    @Before
    public void before() throws Exception {
        server = new JettyServer();
        server.start();
        Thread.sleep(500);
        System.out.println("Server started");
    }

    @Test
    public void testDocument() throws IOException {
        HathmaluwaParser parser = new HathmaluwaParser();
        Document doc = parser.getDoc("http://localhost:9880/sample1.html");
        assertNotNull(doc);
    }

    @After
    public void after() throws Exception {
        server.stop();
    }




}
