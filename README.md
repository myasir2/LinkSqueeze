# LinkSqueeze
This is a test application consisting of a SpringBoot based Kotlin backend API,
and Angular based frontend UI. It uses an Auth0 tenant for authentication to utilize the authenticated-only features.

## Features
1. Generate shortened URL with (optional) expiry date
2. Redirect to original URL from shortened URL
3. View history of generated URLs for a user
4. View metrics of user-generated URL
5. Delete user-generated URL
6. Authentication with Auth0 for all "user" functionalities

## Running all of it
To run all the applications (frontend, backend, and database), ensure you have Docker installed.
Then, from the root directory of this project, run `docker-compose up --build`. 
This will spin up the MySQL DB, backend SpringBoot API, and the frontend Angular app.

## Individually running it
### Backend
To run the backend, you must have MySQL installed locally on your machine running on
port 3306. The API uses the "root" user with password: "password" for the MySQL DB.
After that, from the root directory of this project, run `./gradlw build && ./gradlw bootRun`.

## Frontend
To run the frontend, you must have NodeJS v22 installed. After that, from the frontend's directory (`cd ./link-squeeze-website`),
run `npm install && npm run start`

## Project Structure
### Backend
The backend is split into the following directories:
1. `/bo` - this package contains the classes that house the "core logic" of the system. I.e. the "business layer"
2. `/config` - this package contains configuration Beans for Spring
3. `/context` - this package contains the AuthFilter and the Context class used by the Controller or any other classes that need to get metadata for the request
4. `/controller` - this package contains the Controller classes
5. `/dao` - this package contains the DAOs that are used to interact with the DB
6. `/exception` - this package contains the Exception classes used by the API
7. `/model` - this package contains the request, response, and other model classes used by the system
8. `/service` - this package contains the utility service classes that perform a set of functions
9. `/util` - this package contains general utility classes

### Frontend
The frontend is split into the following directories:
1. `/app` - this directory contains the Angular components
2. `/environments` - this directory contains the environment configuration files
3. `/model` - this directory contains the model files housing the request/response and other model files representing the API responses
4. `/service` - this directory contains classes that are responsible for different functions such as: calling the backend, or showing Snackbar for alerting
5. `/util` - this directory contains utility classes used by the frontend
