# hetels-test
Project to manage hotels

We need to develop a RESTful API application for working with hotels with the following methods:
1)
GET /hotels - get a list of all hotels with their brief information
Example response:
[
{
"id": 1,
"name": "DoubleTree by Hilton Minsk",
"description": "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms in the Belarusian capital and stunning views of Minsk city from the hotel's 20th floor..."
"address": "9 Pobediteley Avenue, Minsk, 220004, Belarus",
"phone": "+375 17 309-80-00"
}
]
2)
GET /hotels/{id} - get detailed information about a specific hotel
Example response:
{
"id": 1,
"name": "DoubleTree by Hilton Minsk",
"description": "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms in the Belarusian capital and stunning views of Minsk city from the hotel's 20th floor..."
"brand" "Hilton"
"address":
{
"houseNumber": 9
"street": "Pobediteley Avenue",
"city": "Minsk",
"country": "Belarus",
"postCode": "220004"
}
"contacts":
{
"phone": "+375 17 309-80-00",
"email": "doubletreeminsk.info@hilton.com"
},
"arrivalTime:
{
"checkIn": "14:00",
"checkOut": "12:00"
},
"amenities":
[
"Free parking"
"Free WiFi"
"Non-smoking rooms"
"Concierge"
"On-site restaurant"
"Fitness center"
"Pet-friendly rooms",
"Room service",
"Business center",
"Meeting rooms"
]
}
3)
GET /search - retrieve a list of all hotels with brief information based on the following parameters: name, brand, city, country, amenities. For example: /search?city=minsk
Example response:
See GET /hotels
4)
POST /hotels - create a new hotel
Example request:
{
"name": "DoubleTree by Hilton Minsk",
"description": "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms in the Belarusian capital and stunning views of Minsk city from the hotel's 20th floor...", - (optional)
"brand" "Hilton",
"address":
{
"houseNumber": 9
"street": "Pobediteley Avenue",
"city": "Minsk",
"country": "Belarus",
"postCode": "220004"
}
"contacts":
{
"phone": "+375 17 309-80-00",
"email": "doubletreeminsk.info@hilton.com"
},
"arrivalTime:
{
"checkIn": "14:00",
"checkOut": "12:00" - (optional)
}
}
Sample answer:
{
"id": 1,
"name": "DoubleTree by Hilton Minsk",
"description": "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms in the Belarusian capital and stunning views of Minsk city from the hotel's 20th floor...",
"address": "9 Pobediteley Avenue, Minsk, 220004, Belarus",
"phone": "+375 17 309-80-00"
}
5)
POST /hotels/{id}/amenities - adding a list of hotel amenities
Request example:
[
"Free parking",
"Free WiFi",
"Non-smoking rooms",
"Concierge",
"On-site restaurant",
"Fitness center",
"Pet-friendly rooms",
"Room service",
"Business center",
"Meeting rooms"
]
6)
GET /histogram/{param} - getting the number of hotels grouped by each value of the specified parameter. Parameter: brand, city, country, amenities.
For example: /histogram/city should return:
{
"Minsk": 1,
"Moskow: 2,
"Mogilev: 1,
etc.
}
and /histogram/amenities should return:
{
"Free parking": 1,
"Free WiFi: 20,
"Non-smoking rooms": 5,
"Fitness center": 1,
etc.
}

The application should be launched from the console using the command mvn spring-boot:run
Port to launch: 8092
All methods must have the same prefix "property-view". For example: GET /property-view/hotels, GET /property-view/search

Technologies used:
Maven, Java 17+, Spring Boot, Spring JPA, Liquibase
Database:
H2

Not required, but would be an advantage if:
Tests
Swagger documentation
Use of design patterns
Layering
Ability to quickly switch from H2 to another database (MySQL, PostgreSQL, Mongo, etc.)

Please send the results as a link to GitHub.

IMPORTANT! Complete the task using Java version 21. The application's compliance will be verified in an automated environment.