/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package corpus.sinhala.crawler.blog.controller;


import java.io.BufferedWriter;
import java.io.FileWriter;

import corpus.sinhala.crawler.blog.rss.RssWebDriver;


/**
 *
 * @author dimuthuupeksha
 */
public class BlogCrawler {

    
    
    
    public void generateUrl(){
        HathmaluwaParser parser = new HathmaluwaParser();
        for (int i = 2; i < 328; i++) {
            try{
                if (RssWebDriver.getInstance().getThreads() > 10) {
                    i--;
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        //java.util.logging.Logger.getLogger(WebCrawler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    continue;
                }
                String pageURL = "http://www.hathmaluwa.org/index/index/page/" + i;

                System.out.println("**********Crawling**********" + pageURL);
                parser.parse(pageURL);

                BufferedWriter writer = new BufferedWriter(new FileWriter("./metadata.xml", true));
                writer.write(pageURL + "\n");
                writer.flush();
                writer.close();


                //processPage(pageURL);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
    
    
    
    
    public static void main(String[] args) {
        BlogCrawler crawler = new BlogCrawler();
        crawler.generateUrl();
    }
}
