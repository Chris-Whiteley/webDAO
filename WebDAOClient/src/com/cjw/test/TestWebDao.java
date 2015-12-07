/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cjw.test;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import webdao.client.PersistenceInterface;
import webdao.client.ThreadCookieManager;
import webdao.client.dao.CharacterDao;
import webdao.entity.Character;

/**
 *
 * @author Chris
 */
public class TestWebDao {
    public static void main(String[] args) throws MalformedURLException {
        
        // For multithreaded applications the following line allows for each thread to
        // have its own cookie jar.  When used with PersistenceInterface this allows threads to participate in their own
        // transactions.
      CookieHandler.setDefault(new ThreadCookieManager());
       
        PersistenceInterface pi = new PersistenceInterface (new URL("http://localhost:8084"));
        CharacterDao characterDao = new CharacterDao(pi);
        
        characterDao.deleteAll();
        System.out.println("1");
        printAllCharacters(characterDao); // should be none
        
        // create two characters        
        Character c = new Character();
        c.setId(1);
        c.setName("Homer Simpson");
        c.setTown("Springfield");
        c.setCountry("USA");
  
        Character c2 = new Character();
        c2.setId(2);
        c2.setName("Princess Twilight Sparkle");
        c2.setTown("Ponyville");
        c2.setCountry("Equestria");
               
        pi.beginTransaction();
        characterDao.create(c);
        characterDao.create(c2);
        pi.rollbackTransaction();  
        System.out.println("2");
        printAllCharacters(characterDao); // should be none

        pi.beginTransaction();
        characterDao.create(c);
        characterDao.create(c2);
        pi.commitTransaction();
        System.out.println("3");
        printAllCharacters(characterDao); // should be two
        
        System.out.println("Done");       
    }
          
    private static void printAllCharacters(CharacterDao dao) {
        List <Character> characters = dao.getAll();
        
        if (characters.isEmpty()) {
            System.out.println("There are no characters in the database");
        } else {
            System.out.println ("There are characters in the database:");
            for (Character c:characters){
                System.out.println(c);
            }
        }
    }
    
}
