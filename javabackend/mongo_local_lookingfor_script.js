// Insert a standard admin user with password 'test'
db.users.insert({username : "admin", password : "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08", email : "test@test.se", tele : "12345", role : "admin"});

db.opstatuses.insert({statusid : 0, name : "Sökning avslutad", descr : "Sökoperationen har avslutats."});
db.opstatuses.insert({statusid : 1, name : "Sökning inledd", descr : "Sökoperationen har inletts."});
db.opstatuses.insert({statusid : 2, name : "Ej inledd", descr : "Sökoperationen har inte ännu inletts."});

db.missionstatuses.insert({statusid : 0, name : "Avslutat uppdrag", descr : "Sökninguppdraget avslutat"});
db.missionstatuses.insert({statusid : 1, name : "1.", descr : "Anmälan om försvinnande ankommer"});
db.missionstatuses.insert({statusid : 2, name : "2.", descr : "Anhörigkontakt tas per telefon av X för att kontrollera om uppgifterna är korrekta"});
db.missionstatuses.insert({statusid : 3, name : "3.A", descr : "OM NEJ - Anmälan avskrivs"});
db.missionstatuses.insert({statusid : 4, name : "3.B", descr : "OM JA - Kontakt tas med polis av X för att kontrollera om en polisanmälan är gjord"});
db.missionstatuses.insert({statusid : 5, name : "4.A", descr : "OM NEJ - Anmälan avskrivs"});
db.missionstatuses.insert({statusid : 6, name : "4.B", descr : "OM JA - Möte med anhöriga och rapportering till organisationen"});
db.missionstatuses.insert({statusid : 7, name : "5.", descr : "Analys av information. Finns tillräckligt med underlag för att påbörja sök?"});
db.missionstatuses.insert({statusid : 8, name : "6.A", descr : "OM NEJ - Avvakta tills mer information inkommer innan fortsättning av process sker"});
db.missionstatuses.insert({statusid : 9, name : "6.B", descr : "OM JA - Akut eftersök påbörjas"});
db.missionstatuses.insert({statusid : 10, name : "7.", descr : "Y informerar organisation genom hemsida och telefonkejda angående tid och plats. Q informerar media. W tar fram material så som kartor, västar och patrullistor m.m"});
db.missionstatuses.insert({statusid : 11, name : "8.", descr : "Samling - Information, gruppindelning - Sök påbörjas - Gav eftersök resultat?"});
db.missionstatuses.insert({statusid : 12, name : "9.A", descr : "OM JA - Kontakta polisen. Sök avslutas"});
db.missionstatuses.insert({statusid : 13, name : "9.B", descr : "OM NEJ - Skall nytt sök göras?"});
db.missionstatuses.insert({statusid : 14, name : "10.A", descr : "OM NEJ - Anmälan avskrivs"});
db.missionstatuses.insert({statusid : 15, name : "10.B", descr : "OM JA - Gå till punkt 7"});
db.missionstatuses.insert({statusid : 16, name : "Okänd status", descr : "Okänd status/processfas"});
