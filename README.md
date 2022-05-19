# Query System: data from yelp

## 1. Overview
This is a simple demo for demonstrating the interaction process of **UI - Middleware - Database** model.

To make it work, this project follows:
1. Parse the .json file and do simple ETL work, which further populate the data to the given database (mySql)
2. UI implementation
3. Query construction and business logic design, how to display the result in UI correctly from the DB


<br>

## 2. Configuration
> Environment: macOS Monterey 12.2.1 M1  
> mySql 8.0.27-arm64  
> Java8 17.0.2 2022-01-18 LTS

  
third-party libraries: 
- json-simple-1.1.1.jar 
- mysql-connector-java-8.0.28

<br>

## 3. Structure
```txt
- sql
  - createdb.sql
  - dropdb.sql
- src
  - com
    - scu
      - coen280
        - Main.java
        - Populate.java
        - hw3.form
        - hw3.java
```

> createdb.sql - contains the sql for creating the table and index  
> dropdb.sql - contains the sql for dropping the table and index   
> Main.java - program entrance  
> Populate.java - read .json files and populate the data into DB  
> hw3.form - UI. 
> hw3.java - business logic for interatcion between frontend and DB

<br>

## 4. Reminder
- when running business query, be sure to terminate the program when you want to do a new query
