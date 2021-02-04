# Full Stack Reactive Demo Application
This repo contains a Full Stack reactive application built using a Spring Boot Webflux back end, and a React.js front end.  All data is saved to a MongoDB instance.  This whole infrastructure runs in Docker.

In order to facilitate exploring this demo and learning from it, the entire application can be stood up by following the instructions in the "[Getting Started](#getting-started)" section of this `README.md` file.

This content is intended to be used as teaching material in conjunction with slides which will Soon™ be included in this repo.  As such, some portions of the design are not production-ready to provide easier learning points.

## What Does it Do??
The application in its current state is a simple Guest Book application.  Visitors are able to see a guestbook populated from calls to the back end API and updated via a simple WebSocket implementation.  In further blog posts, we'll replace this functionality with something unique... something less of a typical textbook example.

This entire setup is built for easy development of the front end when deployed to Docker including live code updates.  A later post will explore productionizing this and proxying API calls to the back end through `Nginx` as would happen in production.

More than anything, this simple application demonstrates some key design features which can be beneficial to application development.

## Getting Started
You're going to need a few things in order to get started.  All of my demos and teaching materials assume that IntelliJ IDEA (Ultimate where appropriate) is being used.  In addition, you're going to need a few extra things:
- **MongoDB installed locally** - this is only important if you want to run the back end as a standalone application for easy development
- **Docker** - this whole stack runs with a `docker-compose.yml` file
- **A Docker `registry` container listening on port 5000** - this is required by the build process
- **Node.js** - This project requires **node v15.7.0** at the time of writing
- **NPM** - This project requires **NPM 7.4.3** at the time of writing

This application is built to be started in a few configurations:
1) **Standalone for local development** - With this mode, you'll need to be running a local MongoDB instance.  It allows for live reloading of resources, allowing you a lot of flexibility when developing the front end and back end but requires more work starting everything.  This configuration requires the following steps:
    - Start your local MongoDB instance (if not already started)
    - Start the Spring Boot back end (in the `webflux-back-end` module of this project) - this can be done by either starting the Spring Boot application as a service, or by using the `spring-boot:start` Maven goal
    - Start the React.js front-end (in the `react-front-end` module of this project) by navigating to the module's directory in your command prompt and entering the following commands (in order):
      - `npm install`
      - `npm start`
   
2) **Dockerized for local development** - This mode is great if you just want to get up and running when working on the front end or if you're purely a front end developer and depend on someone else's changes to the back end.  You don't need a Java IDE (although you will still need Maven and Java installed), nor do you need MongoDB installed.  All you need is the Node/React prerequisites, Docker, and an IDE.  In order to use this configuration, run the `docker-compose.yml` file found at `docker-compose/dev` from the root of the project.  This will make the front end available on `localhost:3000` and the back end API available on `localhost:8080`.  The MongoDB instance will also be available at `localhost:27017`.  It's important to make sure those ports are not otherwise occupied or to change the `docker-compose.yml` accordingly.

3) **Dockerized for "Production"** - Coming in the next post.  Typical to my posts, this would be a weak example of productionization.  Especially here, I wouldn't want to risk giving away the "secret sauce" from previous workplaces.

## Further Reading
Each module contains a more detailed README.md fully explaining what is going on under the hood, testing strategies and any relevant deployment information.  All documentation will continue to be updated with each blog post.

- [WebFlux Back End](webflux-back-end/README.md) - This `README.md` provides a more detailed description of the setup and operations regarding the back end.
- [React Front End](react-front-end/README.md) - This `README.md` provides a more detailed description of the setup and how to properly communicate with the back end APIs.
- [MongoDB](mongo-database/README.md) - This `README.md` provides some basic information on the MongoDB setup.  There's really not a lot to see here.

## Related Blog Post
The blog post which will be written in relation to this content will be available Soon™.

## Teaching Materials
At a later date, teaching materials in the form of a Keynote slide deck will be made available and linked in the root of this project.  The aim is to write one presentation roughly equivalent to one class of approximately three hours' length (including related exercises).