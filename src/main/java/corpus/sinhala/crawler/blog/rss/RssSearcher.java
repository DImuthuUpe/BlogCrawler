/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package corpus.sinhala.crawler.blog.rss;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import corpus.sinhala.crawler.blog.controller.CacheManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author pancha
 */
public class RssSearcher extends Thread {

    String url;
    private HashSet<String> ignoringChars;
    final double ACCEPTANCE_RATIO;
    //boolean debug = true;



    public RssSearcher(String url) {
        ACCEPTANCE_RATIO = 0.5;
        ignoringChars = new HashSet<String>();

        // check http://unicode-table.com/en/#control-character
        ignoringChars.add("\u0020"); // space
        ignoringChars.add("\u002C"); // ,

        ignoringChars.add("\u007b"); // {
        ignoringChars.add("\u007c"); // |
        ignoringChars.add("\u007d"); // }
        ignoringChars.add("\u007e"); // ~
        //System.out.println("Goto url " + url);
        this.url = url;
        RssWebDriver driver = RssWebDriver.getInstance();
        driver.increase();

    }

    @Override
    public void run() {
        RssWebDriver driver = RssWebDriver.getInstance();
        try {
            while (true) {

                if (!driver.acquired()) {
                    if (driver.acquire()) {
                        break;
                    }
                }
                Thread.sleep(10);

            }
            driver.get("http://feedburner.google.com/fb/a/myfeeds");
            WebElement element = driver.findElement(By.name("sourceUrl"));
            element.sendKeys(url);
            element.submit();

            List<WebElement> elements = driver.findElements(By.className("checkboxLabel"));
            for (int i = 0; i < elements.size(); i++) {

                try {
                    if (elements.get(i).getText().contains("RSS")) {
                        String[] s = elements.get(i).getText().split(" ");
                        //System.out.println(s[s.length - 1]);
                        RSSFeedParser parser = new RSSFeedParser(s[s.length - 1]);
                        String blogId = parser.getBlogId();
                        //System.out.println(blogId);
                        Feed feed = parser.getFeed(blogId);
                        for (FeedMessage message : feed.getMessages()) {


                            boolean contains = false;
                            String postId = message.getId();
                            if (CacheManager.getInstance().postCache.containsKey(blogId)) {
                                if (CacheManager.getInstance().postCache.get(blogId).contains(postId)) {
                                    contains = true;
                                    //System.out.println("Contain post id " +postId);
                                } else {
                                    CacheManager.getInstance().postCache.get(blogId).add(postId);
                                    CacheManager.getInstance().serializeCache();
                                    //System.out.println("Not Contain post id " +postId);
                                }
                            } else {
                                Set<String> postIds = new HashSet<>();
                                postIds.add(postId.trim());
                                CacheManager.getInstance().postCache.put(blogId, postIds);
                                CacheManager.getInstance().serializeCache();

                            }
                            if (!contains) {
                                BufferedWriter writer = null;
                                try {
                                    writer = new BufferedWriter(new FileWriter("./Blog.xml", true));
                                    writer.write("<post>\n");
                                    writer.write("<link>");
                                    writer.write(this.url);
                                    //System.out.println(this.url);
                                    writer.write("</link>\n");
                                    writer.write("<topic>");
                                    writer.write(message.getTitle());
                                    writer.write("</topic>\n");
                                    writer.write("<date>");
                                    writer.write(message.getDate());
                                    writer.write("</date>\n");
                                    writer.write("<author>");
                                    writer.write(message.getAuthor());
                                    writer.write("</author>\n");
                                    writer.write("<content>");
                                    writer.write(getAcceptedSentences(message.getDescription()));
                                    writer.write("</content>\n");
                                    writer.write("</post>\n");

                                    writer.flush();
                                    writer.close();

                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }


                                try {
                                    writer = new BufferedWriter(new FileWriter("./BlogRaw.xml", true));
                                    writer.write("<post>\n");
                                    writer.write("<link>");
                                    writer.write(this.url);
                                    writer.write("</link>\n");
                                    writer.write("<topic>");
                                    writer.write(message.getRawTitle());
                                    writer.write("</topic>\n");
                                    writer.write("<date>");
                                    writer.write(message.getDate());
                                    writer.write("</date>\n");
                                    writer.write("<author>");
                                    writer.write(message.getAuthor());
                                    writer.write("</author>\n");
                                    writer.write("<content>");
                                    writer.write(message.getRawDes());
                                    writer.write("</content>\n");
                                    writer.write("</post>\n");

                                    writer.flush();
                                    writer.close();

                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Thread.sleep(1000);

        } catch (Exception ex) {
            ex.printStackTrace();
            // Logger.getLogger(RssSearcher.class.getName()).log(Level.SEVERE, null, ex);
        }

        driver.release();
        driver.decrease();
    }

    public String getAcceptedSentences(String doc) {
        String sentences[] = doc.split("[\u002E\u003F\u0021]");
        String acceptedSentences = "";
        String rejectedSentences = "";
        for (String sentence : sentences) {
            double ratio = checkString(sentence);
            //System.out.println("ratio of sentence: " + ratio);
            if (ratio >= ACCEPTANCE_RATIO) {
                acceptedSentences += sentence + ".";
            } else {
                rejectedSentences += sentence + ".";
            }
        }

        //System.out.println("accepted : -------------------------");
        //System.out.println(acceptedSentences);
        //System.out.println("rejected : -------------------------");
        //System.out.println(rejectedSentences);
        //System.out.println("------------------------------------");


        return acceptedSentences;
    }

    public double checkString(String str) {
        // sinhala unicode range is 0D80–0DFF. (from http://ucsc.cmb.ac.lk/ltrl/publications/uni_sin.pdf )
        int sinhalaLowerBound = 3456;
        int sinhalaUpperBound = 3583;
        int sinhalaCharCount = 0;
        int nonSinhalaCharCount = 0;

        for (int i = 0; i < str.length(); i++) {
            int cp = str.codePointAt(i);
            if (isIgnoringChar(str.charAt(i) + "")) {
                // ignoring chars
                //System.out.println("ignoring char: " + str.charAt(i));
                continue;
            } else if ((cp >= 0 && cp <= 31)) {
                // commands
                continue;
            } else if (cp >= 48 && cp <= 57) {
                // numbers (0 - 9)
                sinhalaCharCount++;
            } else if (cp >= 33 && cp <= 64) {
                // other symbols - do this check after checking for numbers
                continue;
            } else if (cp >= sinhalaLowerBound && cp <= sinhalaUpperBound) {
                // sinhala
                //if(debug) System.out.println("sinhala character: " + str.charAt(i));
                sinhalaCharCount++;
            } else {
                // other
                //if(debug) System.out.println("non sinhala character: " + str.charAt(i));
                nonSinhalaCharCount++;
            }
        }
        if (nonSinhalaCharCount == 0) {
            return 1;
        }

        return (1.0 * sinhalaCharCount / nonSinhalaCharCount);
    }

    boolean isIgnoringChar(String character) {
        if (ignoringChars.contains(character)) {
            return true;
        }
        return false;
    }


    private String[] splitToSentences(String article) {
        return article.split("[\u002E]");
    }
}
