# CollabSearch - Sökstöd för Missing People Sweden

CollabSearch är framtaget av Citerus AB för använding av Missing People Sweden.

Systemet är tänkt att användas som ett administrativt stöd för att organisera
skallgångskedjor över hela landet på ett enkelt sätt.

## Teknisk översikt

CollabSearch består av en webbapplikation skriven i Java som använder MongoDB
som databas. Förutom själva sökstödet finns det prototyper framtagna för att
integrera sökstödet med en Android-applikation. Tanken var att exempelvis samla
in GPS-data från en patrull och kunna presentera den direkt i sökstödet.

Följande komponenter finns i källkodsträdet:

    android-client - prototyp till en Android-klient
    backend - backend till Android-klienten
    javabackend - sökstödet
    
Sökstödet består av fem moduler och tre av dem är
webbtjänster. _collabsearch-api_ exponerar ett API mot
_collabsearch-publicwebsite_. Tanken är att man kan separera den publika
webbplatsen där man kan anmäla sig till sökoperationer från den plats där
databasen återfinns. _collabsearch-adminui_ innehåller
administratörsgränssnittet.

## Lokal installation

MongoDB behöver vara installerat. Systemet körs i Tomcat, men Jetty bör även
fungera.  Du behöver inte sätta upp några databaser eller användare i MongoDB då
detta hanteras direkt av systemet.

1. Tillse att MongoDB är installerat.
2. Starta MongoDB och Tomcat.
3. Gå in i javabackend/ och kör `sh loadtestdata.sh` i terminalen.
4. Bygg .war-filerna genom att köra 'mvn install'.
5. Deploya .war-filerna från `collabsearch-api`, `collabsearch-adminui`,
   `collabsearch-publicwebsite` till Tomcats /webapp-katalog.
6. Gå till http://localhost:8080/collabsearch-adminui i din webbläsare.
7. Du ska nu kunna logga in som 'admin' med lösenordet 'test'.

Notera att collabsearch-publicwebsite har ett testberoende på
collabsearch-api. För att förenkla byggandet av .war-filerna är dessa tester för
närvarande avstängda. Man kan slå på dem för ett enskilt bygge genom att köra
`mvn install -DskipTests=false`.

Värt att nämna är att namnet _lookingfor_ återfinns här och var i koden och i
databaserna. Detta var ett tidigare arbetsnamn för systemet.

## Milstenar och bugghantering

All planering av utveckling och bugghantering sker i GitHubs bugghanteringsverktyg.

## Kontakt

För övriga frågor är du välkommen att kontakta collabsearch [snabela] citerus.se.

## Licens

CollabSearch är fri programvara släppt under den fria programvarulicensen GPL version 3
eller senare. För mer information, se filen COPYING.
