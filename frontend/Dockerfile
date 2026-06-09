# ---- Stage 1 : build Angular ----
FROM node:22-alpine AS build
WORKDIR /app

# On copie d'abord package*.json pour profiter du cache de couches Docker :
# tant que les dépendances ne changent pas, npm ci n'est pas relancé.
COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

# ---- Stage 2 : runtime nginx (image finale légère) ----
FROM nginx:1.27-alpine AS runtime

# Le builder @angular/build:application sort les fichiers dans dist/frontend/browser
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -qO- http://localhost/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
