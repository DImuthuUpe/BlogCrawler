/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package corpus.sinhala.crawler.blog.rss;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 *
 * @author pancha
 */
public class RssWebDriver extends HtmlUnitDriver {

    private static RssWebDriver instance = null;
    private int threads =0;
    
    public synchronized void increase(){
        threads++;
    }
    
    public synchronized void decrease(){
        threads--;
    }

    public int getThreads() {
        return threads;
    }
    
    
    
    private RssWebDriver() {
    }
    
    private boolean acquire =false;
    public synchronized boolean acquired(){
        return acquire;
    }
    
    public synchronized boolean acquire(){
        if(acquire){
            return false;
        }else{
            acquire =true;
            return true;
        }
    }
    
    public void release(){
        acquire =false;
    }

    public static RssWebDriver getInstance() {
        if (instance != null) {
            return instance;
        } else {
            instance = new RssWebDriver();

            instance.get("http://feedburner.google.com/fb/a/myfeeds");
            WebElement element = instance.findElement(By.name("Email"));
            element.sendKeys("dimuthu.upeksha3");
            element = instance.findElement(By.name("Passwd"));
            element.sendKeys("Upek@1234");
            element.submit();
            return instance;
        }
    }
}
