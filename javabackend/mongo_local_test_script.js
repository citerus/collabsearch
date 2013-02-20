//db.users.find().forEach(function(collection) {
//	print(collection.username);
//});

//insert users
db.users.update({username : "admin"}, {"$set" : {username : "test", password : "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08", email : "test@test.se", tele : "1458373883", role : "admin"}}, true, false);
//db.users.update({username : "testuser"}, {username : "alfa", password : "beta", email : "alfa@beta.com", tele : "123", role : "user"}, true, false);

//insert searchmissions
/*db.searchmissions.update({name : "Sökuppdrag 1"},{ descr : "text...", name : "Sökuppdrag 1", prio : 0, status : 1 }, true, false);
db.searchmissions.update({name : "Sökuppdrag 2"},{ descr : "text...", name : "Sökuppdrag 2", prio : 0, status : 3 }, true, false);
db.searchmissions.update({name : "Sökuppdrag 3"},{ descr : "text...", name : "Sökuppdrag 3", prio : 0, status : 7 }, true, false);*/

// db.searchmissions.find({},{"_id" : 1}).forEach(function(doc) {
//     path = "/tmp/uploads/" + doc._id;
//     db.searchmissions.update({"_id" : doc._id}, {"$set" : {files : [{filename : "fil1.pdf", mimetype : "application/pdf", filepath : path}, {filename : "fil2.png", mimetype : "image/png", filepath : path}]}});
// });

//insert searchoperations
// db.searchops.update({title : "Sökop 2"}, { title : "Sökop 2", descr : "Skallgång i skogdunge 3", date : NumberLong("1221724810000"), location : "plats x", status : 0 }, true, false);
// db.searchops.update({title : "Sökop 3"}, { title : "Sökop 3", descr : "Skallgång på kulle 104", date : NumberLong("1221724820000"), location : "plats y", status : 1 }, true, false);
// db.searchops.update({title : "Sökop 1"}, { title : "Sökop 1", descr : "Skallgång på holme 15", date : NumberLong("1221724800000"), location : "plats z", status : 2, "searchers" : [ { name : "ola", tele : "123", email : "bla@bla.se" }, { name : "ola", tele : "123", email : "ola@mail.se" } ]}, true, false);

//assign parent mission to searchops
//result = db.searchmissions.findOne({name : "Sökuppdrag 1"});
//db.searchops.update({},{"$set" : {mission : result._id}}, false, true);

//insert op statuses
db.opstatuses.update({statusid : 2}, {"$set" : {statusid : 2, name : "Ej inledd", descr : "Sökoperationen har inte ännu inletts."}}, true, false);
db.opstatuses.update({statusid : 1}, {"$set" : {statusid : 1, name : "Sökning inledd", descr : "Sökoperationen har inletts."}}, true, false);
db.opstatuses.update({statusid : 0}, {"$set" : {statusid : 0, name : "Sökning avslutad", descr : "Sökoperationen har avslutats."}}, true, false);

//insert mission statuses
db.missionstatuses.update({statusid : 0}, {"$set" : {statusid : 0, name : "Avslutat uppdrag", descr : "Sökninguppdraget avslutat"}}, true, false);
db.missionstatuses.update({statusid : 1}, {"$set" : {statusid : 1, name : "1.", descr : "Anmälan om försvinnande ankommer"}}, true, false);
db.missionstatuses.update({statusid : 2}, {"$set" : {statusid : 2, name : "2.", descr : "Anhörigkontakt tas per telefon av X för att kontrollera om uppgifterna är korrekta"}}, true, false);
db.missionstatuses.update({statusid : 3}, {"$set" : {statusid : 3, name : "3.A", descr : "OM NEJ - Anmälan avskrivs"}}, true, false);
db.missionstatuses.update({statusid : 4}, {"$set" : {statusid : 4, name : "3.B", descr : "OM JA - Kontakt tas med polis av X för att kontrollera om en polisanmälan är gjord"}}, true, false);
db.missionstatuses.update({statusid : 5}, {"$set" : {statusid : 5, name : "4.A", descr : "OM NEJ - Anmälan avskrivs"}}, true, false);
db.missionstatuses.update({statusid : 6}, {"$set" : {statusid : 6, name : "4.B", descr : "OM JA - Möte med anhöriga och rapportering till organisationen"}}, true, false);
db.missionstatuses.update({statusid : 7}, {"$set" : {statusid : 7, name : "5.", descr : "Analys av information. Finns tillräckligt med underlag för att påbörja sök?"}}, true, false);
db.missionstatuses.update({statusid : 8}, {"$set" : {statusid : 8, name : "6.A", descr : "OM NEJ - Avvakta tills mer information inkommer innan fortsättning av process sker"}}, true, false);
db.missionstatuses.update({statusid : 9}, {"$set" : {statusid : 9, name : "6.B", descr : "OM JA - Akut eftersök påbörjas"}}, true, false);
db.missionstatuses.update({statusid : 10}, {"$set" : {statusid : 10, name : "7.", descr : "Y informerar organisation genom hemsida och telefonkejda angående tid och plats. Q informerar media. W tar fram material så som kartor, västar och patrullistor m.m"}}, true, false);
db.missionstatuses.update({statusid : 11}, {"$set" : {statusid : 11, name : "8.", descr : "Samling - Information, gruppindelning - Sök påbörjas - Gav eftersök resultat?"}}, true, false);
db.missionstatuses.update({statusid : 12}, {"$set" : {statusid : 12, name : "9.A", descr : "OM JA - Kontakta polisen. Sök avslutas"}}, true, false);
db.missionstatuses.update({statusid : 13}, {"$set" : {statusid : 13, name : "9.B", descr : "OM NEJ - Skall nytt sök göras?"}}, true, false);
db.missionstatuses.update({statusid : 14}, {"$set" : {statusid : 14, name : "10.A", descr : "OM NEJ - Anmälan avskrivs"}}, true, false);
db.missionstatuses.update({statusid : 15}, {"$set" : {statusid : 15, name : "10.B", descr : "OM JA - Gå till punkt 7"}}, true, false);
db.missionstatuses.update({statusid : 16}, {"$set" : {statusid : 16, name : "Okänd status", descr : "Okänd status/processfas"}}, true, false);
