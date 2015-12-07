/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webdao.client.dao;

import java.util.List;
import webdao.client.PersistenceInterface;
import webdao.entity.Character;

/**
 *
 * @author Chris
 */
public class CharacterDao {

    private final PersistenceInterface pi;
    private final String className;
 
    public CharacterDao(PersistenceInterface pi) {
        this.pi = pi;
        this.className = getClass().getSimpleName();
    }

    
    public void deleteAll() {
        pi.sendRequest(className, "deleteAll");
    }
    
    
    public List<Character> getAll() {
        return pi.sendRequest  (className, "getAll");
    }

    public void create(Character c) {
        pi.sendRequest (className, "create", c);
    }
}
