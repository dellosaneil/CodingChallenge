# CodingChallenge
A coding challenge for Appetiser.au

### Libraries Used:
* Glide
* Navigation Component with SafeArgs
* Material Design
* DataStore
* Room
* Retrofit
* Dagger Hilt
* ViewModel with LiveData
* Coroutines

## Persistence used:
The persistence mechanism being used in the application is Room in order to save the data needed for the application.
The application uses DataStore as well, to save the current status of the application which will be used when the application is reopened.

## Architecture used:
The application uses MVVM pattern to separate the UI related component with the business related components. 
I chose MVVM because it would make debugging and scaling the application easier. I also used Observer pattern in order to easily update the UI when there is a new value for a
particular view. 

## Screenshots
![chipSelected](https://user-images.githubusercontent.com/49714687/109740914-08d93500-7c07-11eb-9f6d-642557fd1eea.jpg)
![detailView](https://user-images.githubusercontent.com/49714687/109740917-0aa2f880-7c07-11eb-8bb5-ffec4c4cb9e4.jpg)
![homePage](https://user-images.githubusercontent.com/49714687/109740918-0aa2f880-7c07-11eb-8cc4-3bb65ef98102.jpg)
![searchView](https://user-images.githubusercontent.com/49714687/109740921-0b3b8f00-7c07-11eb-8e4e-18d7129de97a.jpg)

