FROM mongo:4.4

# There are other ways to set the password, but this just ends up being convenient. Ultimately, never do this in
# production!  That would be dangerous.
ENV MONGO_INITDB_ROOT_USERNAME username
ENV MONGO_INITDB_ROOT_PASSWORD password

EXPOSE 27017

CMD ["mongod"]