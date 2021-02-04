# this will be executed by Maven as if the context were the root of this module
FROM node:alpine3.12 AS builder

WORKDIR /app

# set path so build works properly
ENV PATH /app/node_modules/.bin:$PATH

COPY package.json ./
COPY package-lock.json ./

# install dependencies
RUN npm install
RUN npm install react-scripts -g

# copy everything (this will respect the restrictions set by .dockerignore
COPY . ./

# this port will be needed for accessing the proxy for our web services
EXPOSE 3000

# starts the development server
CMD ["npm", "start"]