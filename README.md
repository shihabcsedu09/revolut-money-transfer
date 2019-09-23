## What is this repository for? ###

Test task for Revolut Backend Test Engineer 

### Tech stack used behind this test project :
* Dropwizard as web service framework.
* Apache Hibernate for ORM.
* H2 in memory database. 
* Apache Maven as build tool.
* JUnit as a test framework
* Mockito for mocking.
* JaCoCo for code coverage library.

### Requirements: ###
Things you need to have installed to run the test project. 

 - Git
 - Apache Maven
 - JDK

### Running the application: ###

 - Clone the project.

    ```
    git clone https://github.com/shihabcsedu09/revolut-money-transfer
    ```
    
 - Go to the project.
    ```
    cd revolut-money-transfer
    ```
- Run this command
	 ```
    bash scripts/run.sh
  ```
- Alternatively you can run the following commands too
   ```
   mvn clean package  
   java -jar target/money-transfer-1.0-SNAPSHOT.jar server config.yml
   ```
  


