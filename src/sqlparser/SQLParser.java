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

import java.io.File;
import java.util.*;
import java.util.Map.Entry;
import static sqlparser.QryContext.*;   /* import constants */

public class SQLParser {

    /**
     * @param args the command line arguments
     */
    private String queryBuff;
    private Database database;
    private static SQLParser _instance;
    private HashMap<
            String,     /* corresponds to actual or symbolical query token/component (FROM, *, or tablename) */
            String[]    /* array of possible tokens that must follow the token with corresponding conditions (if any) to meet */
            > SELTable = new HashMap<>();
    private HashMap<String, String> SELTableConds = new HashMap<>();
    private HashMap<String, String> sqlSymbols = new HashMap<>();
    
    // possible query components
    private String tableList;
    private String columnList;
    private String conditionList;
    private String columnName;
    private String tableName;
    
    
    /* create instance at first run (SINGLETON requirement) */
    static {
        _instance = new SQLParser();
    }
    
    private SQLParser() {
        // build SQL symbol table
        buildSymbolTable();

        // build up SELECT query look-up table
        buildSELECTTable();
    }
    
    public static SQLParser getInstance()
    {
        return SQLParser._instance;
    }
    
    public boolean evalQuery(String query)
    {
        boolean retVal = true;
        
        // flatten query strint to easily extract token
        query = flattenString(query);
        String[] qTokens = query.split(" ");
        
        System.out.println("FLATTENED: " + query);
        
        // variables for query components
        String columnList;
        String tablelist;
        String conditionList;
        
        if( qTokens.length == 0 ){
            System.out.println("ERROR: Empty query string.");
            retVal = false;
        } else {
            // check query type
            switch(qTokens[0].toUpperCase()){
                case "SELECT":
                    // System.out.println("In SELECT");
                    String tokenKey = qTokens[0].toUpperCase(); // start comparing using the query type
                    boolean validSoFar = true;
                    int tokenIndex = 1;
                    String[] keySuccessors;
                    int s;                      // lcv for search loop
                    while(validSoFar && tokenIndex < qTokens.length){
                        
                        // get key token successors
                        keySuccessors = this.SELTable.get(tokenKey);
                    
                        System.out.print("Token: " + tokenKey + ", Successors: ");
                        for(int i=0; i<keySuccessors.length; i++){
                            System.out.print(keySuccessors[i] + " ");
                        }
                        System.out.println("");
                        
                        // check if current token is part of successors
                        boolean tokenValid = false;
                        for(s=0; s<keySuccessors.length && tokenValid == false; s++ ){
                            if( qTokens[tokenIndex].toUpperCase().equals(keySuccessors[s].toUpperCase()) ){
                                tokenValid = true;
                                tokenKey = keySuccessors[s];
                            } else {
                                // check if successor element is symbolic
                                if(keySuccessors[s].indexOf("SYM:") == 0){
                                    tokenValid = true;
                                    // consider the current token as the component of the query
                                    // TO-DO: replace this with a loop where component is considered based on the number of valid symbols in SQLSYM.txt
                                    switch(keySuccessors[s].split(":")[1]){
                                        case "collist":
                                            columnList = qTokens[tokenIndex];
                                            break;
                                        case "tbllist":
                                            tablelist = qTokens[tokenIndex];
                                            break;
                                        default:
                                            throw new Error("Invalid query key successor symbol (" + keySuccessors[s].split(":")[1]  +  ") for key (" + tokenKey + ")");
                                    }
                                    
                                    tokenKey = qTokens[tokenIndex];
                                }
                            }
                        }
                        
                        // NOTE: validSoFar represents the validity of the WHOLE query string as a whole,
                        // while tokenValid represents the validity of ONE query token
                        validSoFar = tokenValid;
                        
                        // consider next token
                        tokenIndex++;
                    }
                    
                    
                    
                    if( validSoFar == true ){
                       // TO-DO: validate query components 
                    }
                    
                    // note validity of query string
                    retVal = validSoFar;
                    
                     break;
                case "INSERT":
                case "UPDATE":
                case "DELETE":
                case "TRUNCATE":
                case "DROP":
                    System.out.println("ERROR: " + qTokens[0].toUpperCase() + " query has no implementation yet! ");
                    retVal = false;
                    break;
                default:
                    System.out.println("ERROR: Invalid query type.");
                    retVal = false;
            }
        }
    return retVal;
    }
    
    public void printSQLSymbolTable()
    {
        printHashMap(sqlSymbols);
    }
    
    public void printSELTableConds()
    {
        printHashMap(this.SELTableConds);
    }
    
    public void printSELTable()
    {
        Iterator<String> contents =  this.SELTable.keySet().iterator();
        System.out.println("\n==========================================================================");
        while( contents.hasNext() ){
            String content = contents.next();
            String[] contentsBuff = this.SELTable.get(content);
            
            System.out.print(content + " = ");
            String[] aContents = (String[])contentsBuff;
            for(int i=0; i<aContents.length; i++){
                System.out.print(aContents[i] +  " ");
            }
            System.out.println("");
        }
        System.out.println("==========================================================================\n");
        
    }
    
    public void printHashMap(HashMap map)
    {
        Iterator<String> contents =  map.keySet().iterator();
        System.out.println("\n==========================================================================");
        while( contents.hasNext() ){
            String content = contents.next();
            Object contentsBuff = map.get(content);
            
            System.out.println(content + " = " + contentsBuff );
        }
        System.out.println("==========================================================================\n");
    }
    
    
    private void buildSELECTTable()
    {
        String filePath = "C:\\Users\\jde\\Documents\\NetBeansProjects\\SQLParser\\src\\sqlparser\\SELECT.txt";
        File file = new File(filePath);
        try{
            Scanner io = new Scanner(file);
            String mode = "";
            while(io.hasNext()){
                String token = io.nextLine();
                if( mode.length() == 0 && token.equals("RULES:") ){   // check if parse mode change
                    mode = "RULES";
                } else if(mode.equals("RULES") && token.equals("CONDS:")) {
                    mode = "CONDS";
                } else if(token.length() > 0){  // skip empty lines
                    String[] comps = token.split("=");
                    if( comps.length == 2 && isValidToken(comps[0], SELECT ) && isValidToken(comps[1], SELECT ) ){
                        // trim both to remove unnecessary spaces
                        comps[0] = comps[0].trim();
                        comps[1] = comps[1].trim();

                        // push data to respective tables depending on type
                        switch(mode){
                            case "RULES":
                                this.SELTable.put(
                                    comps[0],                               /* token */
                                    flattenString(comps[1]).split(" ")      /* token predecessors with conditions (if any) */
                                );
                                break;
                            case "CONDS":
                                this.SELTableConds.put(
                                    comps[0],                               /* token */
                                    flattenString(comps[1])                 /* place raw condition; need to parse and interpret later */
                                );
                                break;
                        }
                    } else {
                        System.out.println("Invalid token value: " + token);
                    }
                }
            }
        } catch(Exception ex){
            throw new Error("Error in parsing SELECT file");
        }
    }
    
    private boolean isValidToken(String token, QryContext context)
    {
        /* 
        STEPS:
        1. determine if a keyword or a symbol
        2. validate 
        */
        System.out.println("ADD isValidToken() code");
        return true;
    }
    
    private void buildSymbolTable()
    {
        String filePath = "C:\\Users\\jde\\Documents\\NetBeansProjects\\SQLParser\\src\\sqlparser\\SQLSYM.txt";
        File file = new File(filePath);
        try{
            Scanner io = new Scanner(file);
            while(io.hasNext()){
                String token = io.nextLine();
                if( token.length() > 0  ){  // skip empty lines
                    String[] comps = token.split("=");
                    if( comps.length == 2 && isValidSymbolName(comps[0]) && isValidSymbolValue(comps[1]) ){
                        this.sqlSymbols.put(comps[0], comps[1]);
                    }
                } else {
                    System.out.println("Skipped blank line");
                }
                
            }
        }catch(Exception ex){
            throw new Error("Cannot find file for SQL symbol list");
        }   
        
        
    }
    
    private boolean isValidSymbolName(String name)
    {
        System.out.println("ADD isValidSymbolName() code");
        return true;
    }
    
    private boolean isValidSymbolValue(String value)
    {
        System.out.println("ADD isVlaidSymbolValue() code");
        return true;
    }
    
    private String  flattenString(String s)
    {
        /* 
        purpose: makes sure that space between string components is at most one
        */
        String buff="";
        boolean hasSpace = false;
        boolean insideString = false;
        for(int x=0; x<s.length(); x++){
           String c = s.charAt(x) + "";
           if( c.equals(" ") && hasSpace == false ){
              buff += c;
              hasSpace = true;
           } else if( c.equals(" ") == false) {
              buff += c;
              hasSpace = false;
           } else {                                 // still add if inside string
               if( insideString == true ){
                   buff += c;
               }
           }
           
           // toggle inside string indicator
           if( c.equals("\"") ){
               insideString = !insideString;
           }
        }
        
        buff = compressCommaString(buff);
        
        return buff.trim(); // remove trailing and leading spaces before returning
    }
    
    private String compressCommaString(String s)
    {
        /* 
        purpose: remove space(s) after a comma
        prereq: must perform flatten string first (called inside flatten string)
        benifits: groups together a list such as table list or column list
        */
        
        // s = flattenString(s);   // performed since prereq for optimize process
        System.out.println(s);
        String buff="";
        boolean hasComma = false;
        boolean insideString = false;
        // boolean prevLetter = false;
        int consSpaceCnt = 0;
        for(int x=0; x<s.length(); x++){
            String c = s.charAt(x) + "";
            if( c.equals(" ")  ){    // if space
                if(  (insideString == true || hasComma == false)){ 
                    buff += c;
                    if( insideString == true ){
                        consSpaceCnt = 0;
                    } else {
                        consSpaceCnt++;
                    }
                } else {
                    consSpaceCnt = 0;
                }
                
            } else {  // if not space
                
                // toggle inside string indicator
                if( c.equals("\"") ){
                    insideString = !insideString;
                }
                
                // assume no comma so far
                hasComma = false;
                
                // set comma indicator if found and not inside string
                if( c.equals(",") && insideString == false ){
                    hasComma = true;
                    
                    // detect if there were previous space before this comma
                    if( consSpaceCnt > 0  ){
                        buff = buff.substring(0, buff.length()-consSpaceCnt);
                    }
                    consSpaceCnt = 0;
                }
                
                buff += c;
            }
        }
        return buff;
    }
    
    public static void main(String[] args) {
        SQLParser sqlParser = SQLParser.getInstance();
        
        //sqlParser.printSQLSymbolTable();                // test column names
        //sqlParser.printSELTable();
        //sqlParser.printSELTableConds();
        
        // System.out.println(sqlParser.flattenString("one            \"two,           three\", four,   fi,ve ,   six             ,    seven   eight"));
        String query = "SELECT FROM * tablename";
        System.out.println(sqlParser.evalQuery(query));
        
    }
    
}
