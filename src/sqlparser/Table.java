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

public class Table {
    private String name;
    private List<Column> columns;
    
    
    public Table(String name, List<Column> columns) {
        this.name = name;
        this.columns = columns;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public boolean isColMember(String colname, boolean caseSensitive)
    {
        boolean retVal =  false;
        Iterator<Column> cols = columns.iterator();     /* generate static iterator out of list */
        while( cols.hasNext() && retVal == false ){
            Column col = cols.next();
            if( caseSensitive ){
                retVal =  col.getName().equalsIgnoreCase(colname);
            } else {
                retVal = col.getName().equals(colname);
            }
        }
        
        return retVal;
    }
    
}
