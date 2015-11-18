# webDAO
Thick client Java SE database access via DAOs using JPA running on the Web
==========================================================================
For a thick client Java SE application, webDAO allows you to have all your database access chanelled through to the server using 
DAOs, a persitence interface, a persistenceServlet and database access done via JPA.

This provides advantages of:
    - JPA connection pool and cache sharing amongst all client instances
    - No database login details required in the client
    - Transaction handling available on the client
    - Access to the database using JPA entities facilitating Object Relational mapping
    - Ability to use JPA persistence and the JPQL object-oriented query language to implement DAO class methods
    
webDAO has been developed using tha Netbeans IDE and this repository contains 3 Netbeans projects:



 



