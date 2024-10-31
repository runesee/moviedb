# Tredje innlevering

## Beskrivelse av prosjektet

Den tredje, og siste, innleveringen er et ferdig produkt. Her har det blitt lagt vekk på å utbedre appen på  
bakgrunn av tilbakemeldinger på innlevering 1 og 2. Dette innebærer både endringer i kode, i form av fiksede warnings fra Checkstyle,  
fikset plugin til Spotbugs og flyttet lokasjon til diagram, og arbeidsvaner, i form av å være bedre på å assigne issues  
til gruppemedlemmer og forbedrede commit-meldinger.

Det er også nye krav til tredje innlevering, noe som naturligvis har vært hovedfokuset ved arbeidet.  
Dette innebærer blant annet å bygget et REST-api.  
En beskrivelse av hvordan dette er bygd opp og fungerer ligger i README.md i modulen ved navn rest.

Det andre kravet til tredje innlevering er videreutvikling av appen.  
Her valgte vi å utvide JavaFX-appen med mer funskjonalitet i henhold til en brukerhistorie framfor å lage en ny klient med annen teknologi.  
Videreutviklingen av appen innebærer flere punkter. Vi har for det første utvidet appen til å også kunne håndtere serier, ikke bare filmer.  
Dette fungerer på samme måte som med filmer, bortsett fra at feltet "runtime" er byttet ut med "seasons".  
Ved å legge til serier ble det også naturlig med andre utvidelser. Dette innebar for det første å legge til flere vinduer,  
som gir økt brukervennlighet, og en mer naturlig oversikt.  
Også selve layouten har blitt utbedret ved å legge alle filmer/serier fra databasen i et  
grid med bilder framfor i en tabell som er brukt ved tidligere innleveringer.

## Begrunnelse av valg

### GitLab

I GitLab har det til tider blitt svært mange commits.  
Dette skyldes at mindre endringer må pushes til repoet for at dette skal fungere for samtlige gruppemedlemmer.  
Vi har derfor valgt å gjøre det på denne måten for å gjøre hele prosessen enklere.

Enkelte ganger under utviklingen har vi vært nødt til å ta et steg tilbake i form av å reverte en commit eller en merge.  
Dette er naturlig, men vi har også ved noen anledninger kommet fram til at vi hadde et bedre utgangspunkt før reverten.  
Derfor har vi også revertet en revert, selv om dette ser rotete ut.  
Det er også lagt inn kommentarer til commits og reverts i ettertid for å kunne gjøre det mer oversiktlig å se gjennom senere.

### Tester
Testdekningsgraden er regnet som god. Vi har valgt de testene vi har valgt på bakgrunn av at dette var de største og  
viktigste metodene i vårt prosjekt. Grunnen til at vi ikke har 100% tesdekningsgrad er for det første at vi vurderte  
det til at vår dekningsgrad er god nok, i tillegg til at alt det mest sentrale er testet, og det er derfor vurdert til  
at resterende tester ville være overflødige.  
Den andre grunnen til dette kommer av at enkelte ting ikke er mulige å teste med den nåværende implementasjonen eller skapte uønsket oppførsel.  
For eksempel testes ikke metodene i MoviedbWeClient, da dette trenger at serveren kjøres,  
som skaper uønskede avhengigheter, siden serveren ikke kan kjøre før de andre modulene har blitt innstallert. Dette gjør at man først må installere  
modulene uten å kjøre testene. I tillegg testes det meste av logikken i MoviedbWebClient i testene for REST-api-et.  
Den samme problemstillingen er relevant når det gjelder testingen av grensesnittet.  
Et eksempel på dette er handleImage() og saveMedia() i MenuController.java. Dette ble vanskelig å teste grunnet at  
det ikke går an å navigere en filechooser med TestFX.