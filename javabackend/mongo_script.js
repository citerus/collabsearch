//db.users.find().forEach(function(collection) {
//	print(collection.username);
//});
db.users.insert({"username" : "test", "password" : "test", "email" : "awdwa@awfdf.com", "tele" : "1458373883", "role" : "admin"});
db.users.insert({"username" : "alfa", "email" : "alfa@beta.com", "password" : "beta", "role" : "user", "tele" : "123"});
db.searchmissions.insert({ "description" : "text...", "files" : [ "fil1.pdf", "fil2.png" ], "name" : "Sökuppdrag 1", "prio" : 0, "status" : 1 });
db.searchmissions.insert({ "description" : "text...", "files" : [ "fil1.pdf", "fil2.png" ], "name" : "Sökuppdrag 2", "prio" : 0, "status" : 3 });
db.searchmissions.insert({ "description" : "text...", "files" : [ "fil1.pdf", "fil2.png" ], "name" : "Sökuppdrag 3", "prio" : 0, "status" : 7 });
db.searchops.insert({ "title" : "Sökop 2", "descr" : "blablabla", "date" : NumberLong("1221724810000"), "location" : "platsyx", "status" : "Sökning pågår" });
db.searchops.insert({ "title" : "Sökop 3", "descr" : "blablabla", "date" : NumberLong("1221724820000"), "location" : "plats z", "status" : "Sökning pågår" });
db.searchops.insert({ "date" : NumberLong("1221724800000"), "descr" : "blablabla", "location" : "plats x", "searchers" : [ 	{ 	"name" : "ola", 	"tele" : "123", 	"email" : "bla@bla.se" }, 	{ 	"name" : "ola", 	"tele" : "123", 	"email" : "ola@mail.se" } ], "status" : "Sökning pågår", "title" : "Sökop 1" });
