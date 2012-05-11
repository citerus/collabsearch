package se.citerus.lookingfor.android.rest;

public class LookingForRest extends GsonRestClient {

	public LookingForRest(String baseUrl) {
		super(baseUrl);
	}

	public Response<VoidData> postFootprint(String object, String user, double lat, double lon, float accuracy, String verificationHash) {
		StringBuilder url = new StringBuilder().append(object).append("/").append(user).append("/footprints");
		return executePost(url.toString(), VoidData.class, 
				param("lat", String.valueOf(lat)),
				param("lon", String.valueOf(lon)),
				param("accuracy", String.valueOf(accuracy)),
				param("verhash", verificationHash)
				);

	}

}
