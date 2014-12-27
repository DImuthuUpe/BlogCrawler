package corpus.sinhala.crawler.blog.controller;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by dimuthuupeksha on 12/27/14.
 */
public class CacheManager {

    private static CacheManager instance=null;

    private CacheManager(){
        deserializeCache();
    }

    public static CacheManager getInstance(){
        if(instance==null){
            instance=new CacheManager();
        }
        return instance;
    }

    public Map<String, Set<String>> postCache=null;

    public synchronized void serializeCache(){
        try
        {

            FileOutputStream fileOut = new FileOutputStream("cache.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(postCache);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in cache.ser");
        }catch(IOException i)
        {
            i.printStackTrace();
        }
    }

    public synchronized void deserializeCache(){
        try
        {
            FileInputStream fileIn = new FileInputStream("cache.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            postCache = (Map) in.readObject();
            in.close();
            fileIn.close();
        }catch(IOException i)
        {
            i.printStackTrace();
        }catch(ClassNotFoundException c)
        {
            //logger.error(c);
            c.printStackTrace();
        }
        if(postCache==null){
            postCache = new HashMap<>();
        }
    }
}
