FROM node:10

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm install

COPY index.js index.js

EXPOSE 4000
CMD [ "node", "index.js" ]
