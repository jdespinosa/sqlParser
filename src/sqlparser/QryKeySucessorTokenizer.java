/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sqlparser;

import java.util.*;

/**
 *
 * @author jde
 */
public class QryKeySucessorTokenizer {
    private HashMap<String, String[]> queryTable = new HashMap<>();

    public QryKeySucessorTokenizer(HashMap queryTable) {
        this.queryTable = queryTable;
    }
    
    public String[] getSuccessors(String key)
    {
        String[] retVal = null;
        
        if(queryTable.containsKey(key) == true ){
          retVal = queryTable.get(key);
        } 
        
        return retVal;
    }
}
