# sqlParser
a Java Library to parse SQL

# This library will basically does the following:
- Checks if an SQL query string is valid.
- If SQL query is valid, note various components of the query for integrity checking
  (e.i. check if a column list really belongs to the table in a SELECT query)
- A flexible/extendable syntax checking to accomodate various SQL "flavors"
- The main purpose of the library is to act as a "framework" for other libraries and application 
  that might need to implement their own database functionality rather using available database engines.


