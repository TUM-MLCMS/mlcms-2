# TUM | Machine Learning in Crowd Modelling & Simulation
## Exercise #2
_for Summer Semester 2020_

To work with **Vadere Crowd Simulation Software**, we cloned the stable branch into our repository.

In order to compile & run, an IDE is required.

Clone the repository, then;
```java
mvn clean
mvn -Dmaven.test.skip=true package
```
_Same commands taken from the original repository._

Follow the steps below after building the necessary files.

***
### GUI
***
Navigate to directory below;
```java
cd VadereGui/target
```
Then;
```java
vadere-gui.jar
```
_If that doesn't work, do;_
```java
<PATH TO JDK> -jar vadere-gui.jar
```

***
### CONSOLE
***
Navigate to directory below;
```java
cd VadereSimulator/target
```
Then;
```java
vadere-console.jar
```
_If that doesn't work, do;_
```java
<PATH TO JDK> -jar vadere-console.jar
```
