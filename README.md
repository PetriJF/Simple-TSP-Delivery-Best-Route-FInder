# Simple TSP Solution for a pizza delivery app

## Challenge Description

As part of the examination for the Computer Science "Algorithm and Data Structures II" Module, a TSP like problem was given.

A pizza delivery service at a restaurant (Apache Pizza was given as an example) has __n__ orders that need to be delivered. Each order has a reference number (the order number), address, time already waited (in minutes) and GPS coordinates.

Some assumptions were also made. The scooter used by the delivery person is a "magic scooter" than can travel in a straight line between any two locations at a constant velocity of 60km/h.

The aim of the program was to deliver all the orders while minimizing the __angry minutes__. __Angry minutes__ represent the minutes a customer waits over the 30 minute wait threshold.

One restriction was to have all the code in one file, reason for which it is all condensed into *__TSP.java__*. Image files could be used for the graphical interface.
Another restriction was that the code could not run for more than 10 seconds to avoid brute forcing.

The marking for the project was 50% for the algorithm efficiency and 50% for the user experience and interface beauty. I received a 100% for the project, ranking 1st for the 40 point route and 4th for the 100 (unknown) point route. Ranking was based on comparing the results of the whole class. 

## Interface Description

The interface for the program can be seen below.

![Alt text](screenshots/interface.png?raw=true "Clean Interface")

A map with the given coordinate range was used to provide a visualization of all the points. A progress bar was designed to show the progress in finding the best route for minimizing the angry minutes, which are displayed together with the total distance.

The route taken is displayed on the right (corresponding on the map as well) with the more detailed variant being under the *Show address order* button.

Some settings are also provided for debugging settings, such as getting the distance matrix and program paramters, such as the starting point or the scooter the scooter speed (under *Additional Settings*).

There are two running modes: An __Instant Compute__ mode, used to determine the route as fast as possible, and a __Visualized Compute__ mode, used to see the algorithm's progress and process in finding the best route. For the instant variant.

In order to run the code, the list of addresses need to be inputted in this format:<br/>
orderNumber,address,timeWaited,gpsNorth,gpsWest<br/>
E.g. 1,Engineering Building Maynooth University,4,53.3577, -6.6103

A list of 40 orders can be found in the *__Sample Data for TSP Project.xlsx__* file.

## Algorithm Description

1. Form the distance matrix for all the points.
2. Form an initial route using the Nearest Neighbour Algorithm
3. Optimizing algorithms. 2-point varying Nearest Neighbour + 2-Opt

The __Optimizing Algorithm__ works by forcing as many combinations as possible for the first two elements of the route (the first two addresses for the delivery) in order to force variation in the algorithm. The Neirest Neighbour is implemented on the new potential route (this represents the potential count) and three 2-Opt variations are implemented in order to find the best result for each iteration. 
* The 2-Opt algorithm on angry minutes minimizing
* The 2-Opt algorithm on distance minimizing
* The 2-Opt algorithm on angry minutes over the result of the 2-Opt on distance

By using this method, the best angry minutes and route are memorized and given at the end. The result for the 40 samples given in the excel file of the repo can be seen below.

![Alt text](screenshots/40PointSolution.png?raw=true "Clean Interface")

