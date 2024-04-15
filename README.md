# Unit 1 task: ProfITsoft 
---
## Description of the main entities

This project describes the entities "Artist" and "Song," which are used for storing and organizing information about musical artists and their songs.

### Artist

The "Artist" entity is used to store information about musical performers.

#### Attributes

- **Name**: The name of the artist.
- **Birthdate** The date of birth of the artist.
- **Nationality** The nationality of the artist.
- **Songs**: A list of songs belonging to this artist.

### Song

The "Song" entity is used to store information about individual musical compositions.

#### Attributes

- **Title**: The title of the song.
- **Year Released**: The year when the song was released.
- **Artist**: The artist who performed this song.
- **Genre** A list of genres associated with this song.

### Json object example
```
{
  "title": "Billie Jean",
  "artist": "Michael Jackson",
  "year_released": 1983,
  "genre": "Pop, Funk"
}
```
---
## [Examples of input and output files](https://github.com/B1lok/json-parsing-app/tree/main/src/main/resources/data)
---
## Multithreading test
#### Using the [DataGenerator](https://github.com/B1lok/json-parsing-app/blob/main/src/main/java/org/example/generator/DataGenerator.java) class, I created 10 files containing a total of 10_000_000 elements. Below are the results of running the program with different number of threads
| Number of threads | Execution time, ms |
| ----------- | ----------- |
| 1 | 4931 |
| 2 | 2734 |
| 4 | 1801 |
| 8 | 1354 |
---
## Running
To run this application execute:
```bash 
mvn clean package
```
Then inside project directory execute:
```bash 
.\json-parser.bat -a <your_attribute> -d <The directory path on your machine containing the JSON files>
```
After successful execution you should see the message that .xml file with statistic was created at the same directory where your json files are located
