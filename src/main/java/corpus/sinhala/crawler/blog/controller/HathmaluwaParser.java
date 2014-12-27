/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package corpus.sinhala.crawler.blog.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import corpus.sinhala.crawler.blog.rss.RssSearcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author dimuthuupeksha
 */
public class HathmaluwaParser {
    Document doc;
    HashSet<String> urls = new HashSet<>();
    public void parse(){
        try {
            doc = Jsoup.connect("http://www.hathmaluwa.org/index/index/page/315").get();
            Elements divs = doc.getElementsByTag("div");
            Iterator<Element> it = divs.iterator();
            
            while(it.hasNext()){
                Element div = it.next();
                if(div.attr("class").trim().equals("post-wrapper row-fluid")){
                    Element a = div.getElementsByTag("a").first();
                    String url =(a.attr("href"));
                    if(!urls.contains(url)){
                        (new RssSearcher(url)).start();
                        urls.add(url);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
