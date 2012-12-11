package se.citerus.collabsearch.adminui.logic;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.Validate;
import org.springframework.stereotype.Service;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.resource.instance.Sms;

import se.citerus.collabsearch.model.SMSMessage;
import se.citerus.collabsearch.model.SearcherInfo;
import se.citerus.collabsearch.store.mongodb.SearchMissionDAOMongoDB;

@Service
public class SMSService {

	private static String ACCOUNT_SID;
	private static String AUTH_TOKEN;
	private static String CALLER_NUMBER;
	private TwilioRestClient client;
	
	@PostConstruct
	public void init() {
		Properties prop = new Properties();
		try {
			InputStream stream = SearchMissionDAOMongoDB.class.getResourceAsStream(
					"/sms-config.properties");
			if (stream != null) {
				prop.load(stream);
				ACCOUNT_SID = prop.getProperty("ACCOUNT_SID");
				AUTH_TOKEN = prop.getProperty("AUTH_TOKEN");
				CALLER_NUMBER = prop.getProperty("CALLER_NUMBER"); 
			}
			
			if ((ACCOUNT_SID == null || ACCOUNT_SID.length() == 0) 
					|| (AUTH_TOKEN == null || AUTH_TOKEN.length() == 0)) {
				System.err.println("No Twilio account SID or authentication token found!");
			} else {
				client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends the included message to the listed phone numbers using the Twilio REST API. 
	 * Failed deliveries are returned as a list of phone numbers.
	 * @param searcherList the list of SearcherInfo objects containing phone numbers for searchers to be contacted.
	 * @param message The message to be sent out. Must be less than or equal to 140 characters.
	 * @return an List containing the numbers to which the SMS was undeliverable.
	 * @throws Exception
	 */
	public List<String> sendSMSToSearchers(List<SearcherInfo> searcherList,
			SMSMessage message) throws Exception {
		Validate.notEmpty(searcherList);
		Validate.notNull(message);
		String messageString = message.toString();
		Validate.notEmpty(messageString);
		
		final List<String> undeliverableNumbers = new ArrayList<String>(0);
		
		if (client == null) {
			throw new Exception("SMS kan ej skickas eftersom SMS-tjänsten ej är konfigurerad");
		}
		final Account mainAccount = client.getAccount();
		final SmsFactory smsFactory = mainAccount.getSmsFactory();
		final Map<String, String> smsParams = new HashMap<String, String>();
		for (SearcherInfo searcherInfo : searcherList) {
			String toNumber = searcherInfo.getTele();
			toNumber = toNumber.replace("-", "").replace(" ", "");
			toNumber = toNumber.startsWith("+46") ? toNumber : "+46" + toNumber;
			smsParams.put("To", toNumber);
			smsParams.put("From", CALLER_NUMBER); //TODO Replace with a valid phone number in config-file
			smsParams.put("Body", messageString);
			try {
				Sms sms = smsFactory.create(smsParams);
//				System.out.println("Sending SMS to +46" + toNumber);
			} catch (TwilioRestException e) {
				undeliverableNumbers.add(searcherInfo.getName() + " : " + searcherInfo.getTele());
				e.printStackTrace();
			}
		}
		
		return (List<String>) (undeliverableNumbers.isEmpty() ? undeliverableNumbers : Collections.emptyList());
	}
}
