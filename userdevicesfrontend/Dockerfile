FROM node:14 AS BUILD

WORKDIR /react_app

COPY ./package*.json /react_app

RUN npm install

COPY . .

RUN npm run build

EXPOSE 3000

CMD ["npm", "start"]