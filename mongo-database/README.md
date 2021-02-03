#MongoDB
There really isn't much to say here.  This setup is mainly here to provide a customized container with simple access to the username and password parameters.  This also serves as a decent launching point for anyone who wants to have a containerized and customized version of MongoDB.

This being said, I don't believe this is a recommended method of running MongoDB in production.  That's probably something that would be appropriate for another blog post!

##How to Build
Maven's `install` goal will build an image from the Dockerfile and tag it with both `latest` and the current version of the project.