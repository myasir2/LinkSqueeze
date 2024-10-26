# Build the project
FROM node:22
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .

# Start the container
EXPOSE 4200
CMD ["npm", "run", "docker-start", "--", "--host", "0.0.0.0"]
