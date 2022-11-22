# cs122b-fall-team-46
cs122b-fall-team-46 created by GitHub Classroom


# Fabflix - Team 46

### Demo Video URL
[Project 4 Recording](https://youtu.be/ryW8n04WB7g)

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

6. Site should now be up. Connect to [AWS public IP]:8080 or :8443



### Contributions

Jonathan
- Android

Sean
- Autocomplete
