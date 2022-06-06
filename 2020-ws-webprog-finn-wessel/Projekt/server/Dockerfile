FROM node:alpine as builder

RUN apk --no-cache add --virtual native-deps \
  g++ gcc libgcc libstdc++ linux-headers autoconf automake make nasm python git && \
  npm install --quiet node-gyp -g

WORKDIR /app

COPY package* /app/

RUN npm install --production
RUN npm install -g pm2
COPY . /app



FROM node:alpine

WORKDIR /app
COPY --from=builder /app/dist ./dist
#COPY --from=builder /app/client/dist ./client/dist
COPY --from=builder /app/node_modules ./node_modules

ENV PORT 3000

EXPOSE 3000

CMD ["node", "dist/main"]