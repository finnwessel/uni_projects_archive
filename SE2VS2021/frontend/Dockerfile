FROM node:lts-alpine AS frontend
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM caddy:2.4.6-alpine
COPY Caddyfile /etc/caddy/Caddyfile
COPY --from=frontend /app/dist /srv
