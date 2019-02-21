# Autonomous Electrical Grid Manager
This project was created during the Calgary Hacks 2019 competition. We had 24 hours to create this project from scratch and present it to the judges. This project landed us in the top 5 among 45 teams :)

Team Members: 
* Satyaki Ghosh
* Nathaniel Habtegergesa
* Sadat Islam
* James Peralta
* Artin Rezaee

## Problem:
Throughout the day, Energy consumption in a city varies. For example 3AM energy demands are very different then 6PM energy demands. Variables for dictating the energy usage for a given time could be the time of day, temperature (if it’s colder everyone will be blasting heaters), holiday (more people will be home), time of the year (summer, fall, winter) etc. Currently, grid managers are the people anticipating these energy demands.

## Solution:
You can think of energy demands being similar to a stock price, with the value changing constantly throughout the day. The only difference is that energy is a-lot more predictable and less volatile than a stock. Similar to how the largest hedge funds are now using AI to improve operations and maximize profits, we want to use Neural Networks to assist cities in optimizing Energy operations. With a more efficient energy usage predictor, we can reduce costs throughout the whole process and also reduce Environmental Issues that come as a by-product of creating energy.

## How we built it
We built this by first researching which data we would need in order to solve this problem. We trained this Neural Network via Google Colab using a TPU to accelerate training. All of our data and web services are hosted on Google App Engine.

## Challenges we ran into
Our weak computing power of our laptops was causing the training time of our NeuralNetworks to take up most of our time.

## What's next for Autonomous Electrical Grid Manager
With more time we will be able to leverage more data and training time to create an even stronger predictive model.
