# sharks-sandbox-project

## Name

Teemu Viikeri

## Topic

A mobile application that displays timely roster information for San Jose Sharks, an NHL team.
Uses [NHL API](https://gitlab.com/dword4/nhlapi/) in backend to fetch data about the rosters.

## Current features

Here are the features for the application.

### Connection to NHL API

Backend connects to NHL API and deserializes JSON data into objects with Jackson. JSON data includes
deep nesting and therefore app structure includes a lot of objects. No mock data is used.

### UI

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

## Features to be implemented

- Player profile information and statistics (graph?).
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
