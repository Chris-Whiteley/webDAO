/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webdao.server.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import webdao.entity.Character;

/**
 *
 * @author Chris
 */
public class CharacterDao {

    private EntityManager em;

    public CharacterDao(EntityManager em) {
        this.em = em;
    }
    
    
    public void create(Character c) {
        em.persist(c);
    }

    public List<Character> getAll() {
        Query q = em.createQuery("select c from Character c");
        return q.getResultList();
    }
    
   public void deleteAll() {
        Query q = em.createQuery("delete from Character");
        q.executeUpdate();
    } 

}
