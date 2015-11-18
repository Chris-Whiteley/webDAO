/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webdao.client.dao;

import java.util.List;
import webdao.PersistenceRequest;
import webdao.client.PersistenceInterface;
import webdao.entity.Character;

/**
 *
 * @author Chris
 */
public class CharacterDao {

    private PersistenceInterface pi;
    private final String className;

    public CharacterDao(PersistenceInterface pi) {
        this.pi = pi;
        className = getClass().getSimpleName();
    }

    
    public void deleteAll() {
        PersistenceRequest pr = new PersistenceRequest();
        pr.setClassName(className);
        pr.setMethodName("deleteAll");
        pi.sendRequest(pr);
    }
    
    
    public List<Character> getAll() {
        PersistenceRequest pr = new PersistenceRequest();
        pr.setClassName(className);
        pr.setMethodName("getAll");
        // send the persistence request and return the results
        return pi.<List<Character>>sendRequest(pr);
    }

    public void create(Character c) {
        PersistenceRequest pr = new PersistenceRequest();
        pr.setClassName(className);
        pr.setMethodName("create");
        Object[] parameters = {c};
        pr.setParameters(parameters);
        pi.sendRequest(pr);
    }
}
