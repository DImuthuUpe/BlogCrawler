package corpus.sinhala.crawler.blog.controller;

import corpus.sinhala.crawler.blog.rss.beans.Post;
import javanet.staxutils.IndentingXMLStreamWriter;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.util.StAXUtils;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by dimuthuupeksha on 12/27/14.
 */
public class XMLFileWriter {

    public static XMLFileWriter instance=null;
    private OMFactory factory;

    private OMElement root;
    private QName rootName;
    private QName linkName;
    private QName topicName;
    private QName authorName;
    private QName contentName;
    private QName postName;
    private QName dateName;
    private QName yearName;
    private QName monthName;
    private QName dayName;
    private QName categoryName;

    ArrayList<OMElement> docs;

    private XMLFileWriter(){
        init();
    }

    public static XMLFileWriter getInstance(){
        if(instance==null){
            instance = new XMLFileWriter();
        }
        return  instance;
    }

    private void init() {


        factory = OMAbstractFactory.getOMFactory();

        rootName = new QName("root");
        linkName = new QName("link");
        topicName = new QName("topic");
        authorName = new QName("author");
        contentName = new QName("content");
        postName = new QName("post");
        dateName = new QName("date");
        yearName = new QName("year");
        monthName = new QName("month");
        dayName = new QName("day");
        categoryName = new QName("category");

        docs = new ArrayList<>();
    }

    private void writeToFile(){
        OMElement root = factory.createOMElement(rootName);
        for(int i=0;i<docs.size();i++){
            root.addChild(docs.get(i));
        }
        docs = new ArrayList<>();
        Integer fileId = CacheManager.getInstance().deserializeFileID();
        String path = "data/"+fileId+".xml";

        try{
            OutputStream out = new FileOutputStream(path);
            XMLStreamWriter writer = StAXUtils.createXMLStreamWriter(out);
            writer = new IndentingXMLStreamWriter(writer);
            root.serialize(writer);
            writer.flush();
            fileId++;
            CacheManager.getInstance().serializeFileId(fileId);
        }catch(XMLStreamException ex){
            ex.printStackTrace();
        }catch (IOException ex){
            ex.printStackTrace();
        }


    }


    public synchronized void addPost(Post post){
        OMElement ePost = createPostElement(post);
        docs.add(ePost);
        if(docs.size()>200){
            writeToFile();
        }
    }

    private OMElement createPostElement(Post elem){
        OMElement doc = factory.createOMElement(postName);

        OMElement category = factory.createOMElement(categoryName);
        category.setText(elem.getCategory());
        doc.addChild(category);

        OMElement date = factory.createOMElement(dateName);
        OMElement year = factory.createOMElement(yearName);
        year.setText(elem.getYear());
        date.addChild(year);

        OMElement month = factory.createOMElement(monthName);
        month.setText(elem.getMonth());
        date.addChild(month);

        OMElement day = factory.createOMElement(dayName);
        day.setText(elem.getDay());
        date.addChild(day);

        doc.addChild(date);

        OMElement link = factory.createOMElement(linkName);
        link.setText(elem.getLink());
        doc.addChild(link);

        OMElement topic = factory.createOMElement(topicName);
        topic.setText(elem.getTopic());
        doc.addChild(topic);

        OMElement author = factory.createOMElement(authorName);
        author.setText(elem.getAuthor());

        doc.addChild(author);

        OMElement content = factory.createOMElement(contentName);
        content.setText(elem.getContent());
        doc.addChild(content);

        return doc;
    }
}
