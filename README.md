# sqlParser
a Java Library to parse SQL

# disclaimer
This is not the best and most efficient one. I know there might be a lot of better libraries out there, sure. I am doing just for fun and personal development. Doesn't hurt to re-invent something for the sake of improvement.

# This library will basically do the following:
- Checks if an SQL query string is valid.
- If SQL query is valid, note various components of the query for integrity checking
  (e.i. check if a column list really belongs to the table in a SELECT query)
- A flexible/extendable syntax checking to accomodate various SQL "flavors"
- The main purpose of the library is to act as a "framework" for other libraries and application 
  that might need to implement their own database functionality rather using available database engines.


