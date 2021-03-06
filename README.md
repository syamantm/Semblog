Semblog 
=================

MSc Dissertaion project
By Syamantak Mukhopadhyay

## Overview
This project demonstrates the capabilities of semantic blogging. With semantic technologies
one can add additional metadata in the blog posts. In particular, a blog post can add metadata about 
what resource it is talking about, what other webpages are referenced etc. This project is an
attempt to harvest these additional metada and expose them as RESTfull resources. One can then use 
Semblog Browser plugin extension to see the related blog posts in the original webpage itself.

##Modules

###semblog.tstore
harvests rdf data from semantic blogs and stores them to a RDF store using Jena RDF API.
Additionally blog tags are indexed  using Apache Lucene to find similar blogs.
		
###semblogsvc
offers to main REST api. Submit URL and Find Related URL. related code can be found in
ac.uk.soton.ecs.sw.semblogsvc.service package.

##Setup Instructions

These are one time actions:


- Install MySql database if you already don't have it (http://www.mysql.com/)
- Create a mysql database named "jenadb"
- Add user "jena1" with password "jena1" to the database created in the previous step ( see MySql
  documentation on how to create database and add users)
- You are ready to go


##To get the code:

Clone the repository:

```
$ git clone git@github.com:syamantm/Semblog.git
```

If this is your first time using Github, review http://help.github.com to learn the basics.

###To run the harvesting module:

From the command line with Maven:

```
$ cd semblog.tstore
$ mvn test
```

###To run the server:

From the command line with Maven:

```
$ cd semblogsvc
$ mvn tomcat:run
```

Access the deployed web application at: http://localhost:8080/semblogsvc/

##Other info:

The initial code for semblogsvc module is a fork from git://github.com/SpringSource/spring-mvc-showcase.git
