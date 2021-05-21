# sharks-sandbox-project

## Name

Teemu Viikeri

## Topic

A mobile application that displays timely roster and player information for San Jose Sharks, an NHL team.
Uses [NHL API](https://gitlab.com/dword4/nhlapi/) in backend to fetch the data used in this application.

## Release 1 features

### Connection to NHL API

Backend connects to NHL API and deserializes JSON data into objects with Jackson. JSON data includes
deep nesting and therefore app structure includes a lot of objects. No mock data is used.

### UI -- Main activity

The application starts from an activity which display roster data, next match data and previous match data
from the API.

#### Roster

A list of players in current roster of the San Jose Sharks. Every name is clickable, on click event opens a
player profile activity. Roster is divided into three columns: player's jersey number, name and position.
Every column can be sorted through column headers above the roster listing. Column headers are buttons.

Player profile activity includes basic information at the moment. The activity will include
current season's statistics in both text and graphs formats. 

#### Next match

Next match area displays information of the next game in schedule for the San Jose Sharks.

#### Previous match

Previous match area display information and highlights video of the previous game in schedule for the San Jose Sharks.
Highlights video continues even if orientation of the screen is changed. Video also retains played playback position
through lifecycle and app exits so the user can continue the video from the time position it was left in.

## Release 2 features

### UI - Player 

#### Basic statistics

Player profile activity now includes the player's basic statistic of the season: games played, goals, assists and total points.

#### Points per month graph

The application uses a 3rd party module to show graph data, [MPAndroidChart](https://weeklycoding.com/mpandroidchart/). A bar chart
is shown in the player profile under the previously mentioned basic statistics and it shows points per month for the player. The application
fetches game log data of each game the player has played this season and then uses that data to show total amount of points for each month.
Month labels are dynamically set to the chart depending on in which months the player has played this season.

### App icon and name of the app

Application now has a dedicated icon and a name for the app.

## Known bugs

There are two known bugs in the application at the moment:

1. Even though a `MediaController` is anchored via `setAnchorView` to a `VideoView`, the `MediaController` still appears on screen outside of video
when the application starts. The `MediaController` also appears out of its meant position when orientation is changed.

2. Sometimes (not always) during landscape orientation, even though the `MediaController` is anchored to the `VideoView`, the `MediaController` is off-position to right a bit like it has some left margins even though there aren't any.

## Screencast

Watch the screencast [here on Youtube](https://www.youtube.com/watch?v=fw8Mkz5ixyo).

## Features to be implemented in the future

- The option to choose between current roster listing or the most recent, previous or projected, match lineup (might not be possible due to soem of the needed properties in the NHL API's JSON data are random generated that would be used for this functionality which means I might not be able to deserialize those properties). If the feature is possible, there would be buttons which toggle between roster and lineup.
- Custom MediaController (especially to have the ability to watch the video in fullscreen) for highlights video.
- Listing of all NHL teams as the starting activity for the application.
- Option to choose any NHL team to look at that team's data.
- Individual color themes for every team.
- Links to each team's website.

## Target

Android/Kotlin

## Google Play link

Not currently available.
