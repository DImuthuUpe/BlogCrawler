/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package corpus.sinhala.crawler.blog.rss;

import java.net.URL;
import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author dimuthuupeksha
 */
public class NewRssFeedParser {
    public Document parse(URL url) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(url);
        iterateThrough(document);
        return document;
    }
    
    public void iterateThrough(Document document){
        Element root = document.getRootElement();

        // iterate through child elements of root
        for ( Iterator i = root.elementIterator("title"); i.hasNext(); ) {
            Element element = (Element) i.next();
            System.out.println("k" +element.getStringValue());
            // do something
        }
    }
}
