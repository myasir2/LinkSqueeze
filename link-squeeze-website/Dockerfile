# Use a Node image to run the Angular CLI
FROM node:22

# Set the working directory
WORKDIR /app

# Copy package.json and package-lock.json for installing dependencies
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the entire Angular app source code to the container
COPY . .

# Expose port 4200 for the Angular development server
EXPOSE 4200

# Start the Angular development server
CMD ["npm", "run", "docker-start", "--", "--host", "0.0.0.0"]
