/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package corpus.sinhala.crawler.blog.rss;

/**
 *
 * @author pancha
 */
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import corpus.sinhala.crawler.blog.rss.beans.Feed;
import corpus.sinhala.crawler.blog.rss.beans.FeedMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class RSSFeedParser {

    static final String TITLE = "title";
    static final String DESCRIPTION = "description";
    static final String CHANNEL = "channel";
    static final String LANGUAGE = "language";
    static final String COPYRIGHT = "copyright";
    static final String LINK = "link";
    static final String AUTHOR = "author";
    static final String ITEM = "item";
    static final String PUB_DATE = "pubDate";
    static final String GUID = "guid";
    static final String ATOMID = "atom:id";
    final URL url;
    private String link;
    public RSSFeedParser(String feedUrl) {
        link = feedUrl;
        System.out.println(feedUrl);
        try {
            this.url = new URL(feedUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBlogId() {
        Feed feed = null;
        String atomId = "";
        try {
            
            

            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            InputStream in = read();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            // Read the XML document
            int location = -1;
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    String localPart = event.asStartElement().getName()
                            .getLocalPart();
                    //System.out.println(localPart);
                    switch (localPart) {
                        
                        case CHANNEL:
                            event = eventReader.nextEvent();
                            atomId = getCharacterData(event, eventReader);
                            //System.out.println("Atom Id "+atomId );
                            return atomId.split("-")[1];
                    }
                }
            }
        } catch (XMLStreamException e) {

            throw new RuntimeException(e);
        }

        return null;
    }
    
    
    public Feed getFeed(String blogId){  //test here
        List<FeedMessage> feeds = new ArrayList<>();
        try{
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet("https://www.googleapis.com/blogger/v3/blogs/"+blogId+"/posts?key=AIzaSyAKoNUtkiL1Xd_XQryUnqNgDEPhUEgQnRo");
            //System.out.println("Get Url ");
            //System.out.println("https://www.googleapis.com/blogger/v3/blogs/"+blogId+"/posts?key=AIzaSyAKoNUtkiL1Xd_XQryUnqNgDEPhUEgQnRo");
            get.setHeader("Accept", "*/*");
            HttpResponse response = client.execute(get);
            String json = EntityUtils.toString(response.getEntity());
            
            //System.out.println(json);
            JSONObject obj = new JSONObject(json);
            JSONArray mainContent = obj.getJSONArray("items");
            for(int i=0;i<mainContent.length();i++){
                JSONObject blogPost = mainContent.getJSONObject(i);
                String link = this.link; 
                String rawTitle = blogPost.getString("title");
                String rawDes =blogPost.getString("content");
                String author=blogPost.getJSONObject("author").getString("displayName");
                String date =blogPost.getString("published");
                String id = blogPost.getString("id");
                String description=filter(rawDes);
                String title=filter(rawTitle);
                FeedMessage message = new FeedMessage();
                message.setAuthor(author);
                message.setDate(date);
                message.setDescription(description);
                message.setLink(link);
                message.setRawDes(rawDes);
                message.setRawTitle(rawTitle);
                message.setTitle(title);
                message.setId(id);
                feeds.add(message);
                //System.out.println(title);
            }
            
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
        Feed feed= new Feed(null, null, null, null, null, null);
        feed.setEntries(feeds);
        return feed;
    }
    
    private String[] restricted = {"a", "br", "img", "div", "/", "<", ">", "nbsp", "&", "span"};

    private String filter(String s) {
        boolean open =false;
        int start =0;
        int end=0;
        for (int i = 0; i < s.length(); i++) {
            if(s.charAt(i)=='<'){
                start=i;
            }else if(s.charAt(i)=='>'){
                end=i;
                if(start<end){
                    
                    s=s.substring(0, start)+s.substring(end+1, s.length());
                    i=0;
                }
            }
            
            
        }
        s=s.replace("&nbsp;","");
        s=s.replace("\n", "");
//        String temp = "";
//        for (int i = 0; i < s.length(); i++) {
//            if ((int) s.charAt(i) > 500 || s.charAt(i) == ' '|| s.charAt(i) == '.'|| s.charAt(i) == ','|| s.charAt(i) == '?') {
//                if (s.charAt(i) == ' '||s.charAt(i) == '.'||s.charAt(i) == ','||s.charAt(i) == '?') {
//                    if(i!=0){
//                        if(s.charAt(i-1)>500){
//                            temp = temp + s.charAt(i);
//                        }
//                    }
//                } else {
//                    temp = temp + s.charAt(i);
//                }
//            }
//        }
        return s.trim();
    }

    private String getCharacterData(XMLEvent event, XMLEventReader eventReader)
            throws XMLStreamException {
        String result = "";
        event = eventReader.nextEvent();
        if (event instanceof Characters) {
            result = event.asCharacters().getData();
        }
        return result;
    }

    private InputStream read() {
        try {
            return url.openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
