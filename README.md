# Time tracking dashboard backend

The current application is a standalone Java Spring-Boot application that responds to specific end-points to provide and store 
time tracking data, it stores all the data in local JSON files and it's not meant to be efficient, but function.

## How to use it

Build from the sources using the command:

```
$> ./mvnw clean package
```

Execute it with the following command to execute it locally in port 8080:

```
$> java jar target/ttbd.jar
```

## Provided endpoints

```
	GET 	 /activities
		Description: Returns a list of activities and their registered time according to the defined structure (see Activity structure)		
		Url-Parameters: none
		Request-Body: none
		Response:
			- 200 on normal behavior, this endpoint will return empty lists instead of invalid values
		
	POST /activities/{title}
		Description: Creates a new activity and registers the given time into the specified date
		Url-Parameters: string(required) describing the activity identifier
		Request-Body: 
			- date: string(required), describing the date to register hours to, e.g. 2021-12-27
			- time: number(required), describing the hours to register on the given date, e.g. 5
		Response:
			- 200 If the activity is successfully created, the response body contains the created entry
			- 409 If an activity with the same ident already exists
			- 400 If the request is malformed or not containing the required data
		
	PUT  /activities/{title}
		Description: Updates an existing activity, registering the given time into the specified date
		Url-Parameters: string(required) describing the activity identifier
		Request-Body: 
			- date: string(required), describing the date to register hours to, e.g. 2021-12-27
			- time: number(required), describing the hours to register on the given date, e.g. 5
		Response:
			- 200 If the activity is successfully updated, the response body contains the updated entry
			- 404 If no activity is found with the given ident
			- 400 If the request is malformed or not containing the required data
		
```