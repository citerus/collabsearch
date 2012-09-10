db.users.find().forEach(function(collection) {
	print(collection.username);
});
