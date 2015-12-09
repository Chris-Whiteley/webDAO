/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webdao.server.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import webdao.dao.CharacterDAO;
import webdao.entity.Character;

/**
 *
 * @author Chris
 */
public class CharacterDao implements CharacterDAO {

    private EntityManager em;

    public CharacterDao(EntityManager em) {
        this.em = em;
    }
    
    
    @Override
    public void create(Character c) {
        em.persist(c);
    }

    @Override
    public List<Character> getAll() {
        Query q = em.createQuery("select c from Character c");
        return q.getResultList();
    }
    
    @Override
   public void deleteAll() {
        Query q = em.createQuery("delete from Character");
        q.executeUpdate();
    } 

}
