# Informasjon om prosjektet

**Dette prosjektet representerer en database for filmer og tv-serier.**

Når prosjektet er ferdigstilt vil det gi brukeren mulighet for å legge inn filmer og TV-serier i en database, der personen kan legge inn informasjon om filmen/serien, samt gi vurderinger og holde styr på filmer han/hun har sett.

## Brukerhistorier

### Oversikt (us-1)

Som privatperson ønsker jeg et program som kan hjelpe meg med å holde styr på filmer jeg har sett/ønsker å se.

Brukeren har behov for å kunne lagre filmer som han/hun har sett på en ryddig og god måte, slik at det er lett å finne
frem.
Kanskje ønsker brukeren også mulighet for å sortere på sjanger, spilletid, utgivelsesår osv.

**Viktig å kunne se**

- En god oversikt over filmer med mulighet for sortering

**Viktig å kunne gjøre**

- Legge inn nye filmer, bestemme sjangere, utgivelsesår, spilletid osv.
- Mulighet for å lagre og importere, slik at man kan komme tilbake til lista senere, og legge inn nye filmer.

### Støtte for serier (us-2)

Som privatperson ønsker jeg å kunne legge inn serier jeg har sett/ønsker å se, i tillegg til filmer.

Brukeren har behov for å kunne lagre serier som han/hun har sett i en separat "database" innen den samme appen.  
Det må være mulig å velge om man skal legge inn en film eller en serie, og databasen for serier må ha de samme funksjonalitetene som for filmer.

**Viktig å kunne se**

- Et grensesnitt som lar deg velge om du vil se databasen med filmer eller serier
- En god oversikt over serier med mulighet for sortering

**Viktig å kunne gjøre**

- Legge inn nye serier, bestemme sjangere, utgivelsesår, antall sesonger osv.
- Mulighet for å sortere serier basert på utgivelsesår, navn, antall sesonger, vurdering osv.
- Mulighet for implisitt lagring, slik at databasen automatisk lagres når brukeren legger inn/fjerner en serie  
  og at databasen importeres når brukeren åpner appen igjen.

## Skjermbilder av grensesnitt

![Add Movies/Series ui](/moviedb/core/src/main/resources/moviedb/core/AddMovieSeries.png)
![Series ui](/moviedb/core/src/main/resources/moviedb/core/Series.png)
![Movies ui](/moviedb/core/src/main/resources/moviedb/core/Movies.png)
![Movie ui](/moviedb/core/src/main/resources/moviedb/core/Movie.png)