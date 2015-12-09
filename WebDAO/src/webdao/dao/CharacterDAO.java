/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webdao.dao;

import java.util.List;
import webdao.entity.Character;

/**
 *
 * @author Chris
 */
public interface CharacterDAO {

    void create(Character c);

    void deleteAll();

    List<Character> getAll();
    
}
