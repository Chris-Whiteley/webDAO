/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webdao.client.dao;

import webdao.dao.CharacterDAO;
import java.util.List;
import webdao.client.PersistenceInterface;
import webdao.entity.Character;

/**
 *
 * @author Chris
 */
public class CharacterDao implements CharacterDAO {

    private final PersistenceInterface pi;
    private final String className;
 
    public CharacterDao(PersistenceInterface pi) {
        this.pi = pi;
        this.className = getClass().getSimpleName();
    }

    
    @Override
    public void deleteAll() {
        pi.sendRequest(className, "deleteAll");
    }
    
    
    @Override
    public List<Character> getAll() {
        return pi.sendRequest  (className, "getAll");
    }

    @Override
    public void create(Character c) {
        pi.sendRequest (className, "create", c);
    }
}
