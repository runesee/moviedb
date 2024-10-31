# Dokumentasjon av REST-tjenesten

Denne filen inneholder dokumentasjon for hvordan rest-tjenesten tilknyttet appen fungerer.  
Altså hvordan tjenesten er implementert, hvilke klasser som gjør hva, hvordan man starter  
serveren, samt hvilke HTTP-forespørsler som støttes.

## Oppbygning og kjøring av serveren

Filene for REST-tjenesten ligger hovedsakelig i REST-modulen. Her ligger [MovieDatabaseRepository](moviedb/rest/src/main/java/moviedb/rest/MovieDatabaseRepository.java),  
[SeriesDatabaseRepository](moviedb/rest/src/main/java/moviedb/rest/SeriesDatabaseRepository.java), [MoviedbRestController](moviedb/rest/src/main/java/moviedb/rest/MoviedbRestController.java) og
til slutt [MoviedbServerApplication](moviedb/rest/src/main/java/moviedb/rest/MoviedbServerApplication.java).

Repository-klassene detekteres automatisk av applikasjonen. De har tilhørende MovieDatabase og SeriesDatabase-objekter,  
som lastes inn fra json-filene i persistence-mappen (**{user.home}/moviedbfx/persistence**}). Det er denne delen som inneholder logikken til REST-tjenesten.

Controller-klassen er, som navnet tilsier, kontroller-delen av REST-tjenesten. Den fungerer som en slags   
mellommann mellom logikken og serveren. Kontrolleren bruker Spring-annotasjon over metodene for å definere HTTP-forespørslene.  
Det er implementert støtte for GET- og PUT-forespørsler.

MoviedbServerApplication har bare en enkelt metode, som starter serveren vha. SpringBoot-biblioteket.  
Serveren kan også startes dersom man åpner en terminal i **moviedb/rest**-mappen, og kjører kommandoen `mvn spring-boot:run`.

## Sending av forespørsler

Sendingen av forespørslene til serveren gjøres vha. [MoviedbWebClient](moviedb/core/src/main/java/moviedb/core/MoviedbWebClient.java)-klassen i core-modulen.  
Metodene til denne klassen sender HTTP-forespørseler til serveren, og det er altså denne klassen  
som brukes for å oppdatere/hente innhold fra serveren. Hovesakelig brukes get- og update-metodene.  
GET-metoden kalles i Controller-klassen for appen ([MenuController](moviedb/fxui/src/main/java/moviedb/ui/MenuController.java)), og PUT-metoden kalles i [RemoteDatabaseSaver](moviedb/core/src/main/java/moviedb/core/RemoteDatabaseSaver.java).  
RemoteDatabaseSaver er en observator som varsles hver gang en endring skjer i det tilhørende [AbstractObservableDatabase](moviedb/core/src/main/java/moviedb/core/AbstractObservableDatabase.java)-objektet.  
Den har hovedsakelig bare en metode, databaseChanged() som kaller den rette updateRemote()-metoden i MoviedbWebClient-klassen  
avhengig av hvilken type database den får inn.

Serveren sender responsen på json-format, og klienten bruker en ObjectMapper- med henholdsvis en [MovieModule](moviedb/core/src/main/java/moviedb/json/internal/MovieModule.java) eller  
en [SeriesModule](moviedb/core/src/main/java/moviedb/json/internal/SeriesModule.java), for henholdsvis filmer og serier, for å gjøre om json-strengen til et database-objekt.  
Objectmapper-objektet opprettes vha. de statiske createObjectMapper-metodene i [JsonFileHandler](moviedb/core/src/main/java/moviedb/json/JsonFileHandler.java).