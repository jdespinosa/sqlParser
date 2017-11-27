/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sqlparser;

/**
 *
 * @author jde
 */
import java.util.*;

public class Database {
    private String name;
    private List<Table> tables;

    public Database() {
    }
    
    public boolean isTableMember(String tableName, boolean caseSensitive)
    {
        boolean retVal = false;
        Iterator<Table> tbls = tables.iterator();
        while(tbls.hasNext() && retVal == false ){
            Table table = tbls.next();
            if(caseSensitive){
                retVal = table.getName().equalsIgnoreCase(tableName);
            } else {
                retVal = table.getName().equals(tableName);
            }
        }
        return retVal;
    }
    
    
}
