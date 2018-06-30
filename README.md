# VisualFire

Fyre is a Plugin for IntelliJ that allows you to view and update your Firebase Realtime Database. Further you can copy paste your database (your selected node including all its children) via simple ctrl +c (depending on your OS) or paste any JSON and make it part of your Firebase DB (soon).

## Getting Started

Compile the Project ( Gradle - RunIDE ), got Views -> Tools -> VisualFire. Now you'll see on the right side of your IDE a tab "VisualFire". Open it, switch to the tab "Configs" and select your Firebase configs .JSON File. If you have selected a valid JSON File, your database should be loaded now. You can edit your nodes via double clicking on them. Have fun coding  

### Prerequisites

You'll need a Firebase Account and your credentials JSON File.


## Running the tests

As Test-Engine JUnit and Mockito is used.

## Built With

* Firebase Admin SDK Java - That domain code wraps around that SDK. 
* Gradle - Dependency Management
* Mockito - Mocking and Testing
* Swing - GUI

## TO DO before launching first Version

* ~~Write engine for reading and updating data~~
* ~~Create Swing UI~~
* ~~Real time updates~~
* ~~Integrate swing UI into IDE~~
* Copy to JSON
* Import from JSON
* ~~UI Layout~~
* Copy relative path of a node
* Insert / delete a node from tree
* Unit Tests (yeah, I was too fast on this one..)

## Future TODOs
* Add Auto Complete
* Search through tree
* Add more Firebase Services (Storage, Security Rules, Cloud Functions, ...)

## Author

* **Martin Nowosad** - *Creator* - [MrIceman](https://github.com/MrIceman)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.


## Acknowledgments

* Nobody yet :(

![alt text](http://i63.tinypic.com/aypp1y.png)

