# cs122b-fall-team-46
cs122b-fall-team-46 created by GitHub Classroom


# Fabflix - Team 46

### Demo Video URL
[Project 3 Recording](https://www.youtube.com/watch?v=ikR-clvJzSc)

### Deployment Steps
(Class Project Instructions followed, no aberrant frameworks)
Requirements:
* MySQL 8.0.30
* JDK 11.0.16
* Java Servlets 3.1.0
* Tomcat 9
* Maven 3.8.6
* AWS (Ubuntu Server 20.04)

Steps:
1. Clone the repository: `git clone https://github.com/uci-jherold2-teaching/cs122b-fall-team-46.git` into the AWS server.

2. Create and populate a `moviedb.sql` file

3. `mvn package` in the repository folder

4. `cp ./target/*.war /var/lib/tomcat9/webapps/` the .war into tomcat

5. Encrypt existing user and employee password with  `mvn compile`  `mvn exec:java -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="UpdateSecurePassword"`

6. Load the stored-procedure.sql file into database

7. Load pipeline data with `mvn exec:java -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="SAXParser"`

8. Site should now be up. Connect to [AWS public IP]:8080 or :8443

### Substring Matching Design

Substring matching was done in the `MovieListServlet` by constructing queries with the format `[column] LIKE ?`. These placeholders 
throughout the queries are subsequently replaced with strings of the format `%[target]%` so that any database entries that contain the target
will be returned.

### Prepared Statement Queries
- MovieListServlet.java
    - https://github.com/uci-jherold2-teaching/cs122b-fall-team-46/blob/main/src/MovieListServlet.java
- MovieServlet.java
    - https://github.com/uci-jherold2-teaching/cs122b-fall-team-46/blob/main/src/MovieServlet.java
- StarServlet.java
    - https://github.com/uci-jherold2-teaching/cs122b-fall-team-46/blob/main/src/StarServlet.java
- PaymentServlet.java
    - https://github.com/uci-jherold2-teaching/cs122b-fall-team-46/blob/main/src/PaymentServlet.java
- DashboardLoginServlet.java
    - https://github.com/uci-jherold2-teaching/cs122b-fall-team-46/blob/main/src/DashboardLoginServlet.java
- DashboardServlet.java
    - https://github.com/uci-jherold2-teaching/cs122b-fall-team-46/blob/main/src/DashboardServlet.java
- LoginServlet.java
    - https://github.com/uci-jherold2-teaching/cs122b-fall-team-46/blob/main/src/LoginServlet.java

### Parsing Time Optimization Strategies
- Multithreading to query MySQL database for duplicates
- Fulltext Index on stars.name to query stars.id
- Batch Insertion to insert new entries into MySQL database in groups

### Inconsistency Data Report
+----------------------+  
| Inconsistency Report |  
+----------------------+  
Inserted Movies Count: 11830  
Inserted Stars Count: 5752  
Inserted Genres Count: 57  
Inserted Genres In Movies Count: 9608  
Inserted Stars In Movies Count: 32137  
  
Inconsistent Movies Count: 128  
Duplicate Movies Count: 0  
Nonexisting Movies Count: 1225  
Duplicate Stars Count: 923  
Nonexisting Stars Count: 14234  
Linked Genres Count: 34  

### Contributions

Jonathan
- Prepared Statements
- reCAPTCHA
- Employee Dashboard
- XML Pipeline

Sean
- HTTPS
- Password Encryption
- stored-procedure.sql
- Video optimization and quality assurance / PR
