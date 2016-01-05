package lab3;
import java.io.BufferedReader; 
import java.io.IOException; 
import java.io.InputStreamReader; 
import java.math.BigInteger; 
import java.net.URL; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 
import java.util.Random; 
import java.net.*;
import java.util.*; 
/**
 *SIPUtils contains SIP-related functions that support the main functionnalities
 **/

class SIPUtils{


	/**
	 * This function adds a contact to the hashmap structure that manages them
	 * 
	 * @param contacts
	 *            Hashmap structure
	 * @param username
	 *            username (used as a key)
	 * @param sipAddress
	 *            SIP address string
	 **/
	public static HashMap addContact(HashMap contacts,String username,String sipAddress){
		contacts.put(username,sipAddress);
		return contacts;	
	}	
	
	/**
	 * Creates a SIP address string from the username and IP address
	 * 
	 * @param username
	 *            username (used as a key)
	 * @param ipAddress
	 *            IP address string
	 **/
	public static String setSipAddress(String username,String ipAddress){
		String sipAddress= "sip" + ":" + username + "@" + ipAddress + ":" + "5070";
		return sipAddress;
	}

	/**
	 * Return the machine's interface wlan0 IP address
	 * 
	 **/
	public static String getPrivateIp(){
		String ip;
		String toBereturned = null;
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
			NetworkInterface iface = interfaces.nextElement();

			// filters out 127.0.0.1 and inactive interfaces
			if (iface.isLoopback() || !iface.isUp())
				continue;

			Enumeration<InetAddress> addresses = iface.getInetAddresses();
			while(addresses.hasMoreElements()) {
				InetAddress addr = addresses.nextElement();
				ip = addr.getHostAddress();
				if (((iface.getDisplayName()).equals("wlan0")) && ip.length() <20){
					toBereturned = ip;
				}
			}
		}

		return toBereturned; 
		}

		catch (SocketException e) {
			throw new RuntimeException(e);
		}

	}

/**
	 * Checks public IP address from Amazon's API
	 * 
	 **/
	public static String getIp() throws IOException{
		URL whatismyip = new URL("http://checkip.amazonaws.com"); 
 		BufferedReader in = null; 
 		try { 
 			in = new BufferedReader(new InputStreamReader( 
			whatismyip.openStream())); 
 			String ip = in.readLine(); 
				return ip; 
			} 
			finally { 
				if (in != null) { 
					try { 
						in.close(); 
					} catch (IOException e) { 
						e.printStackTrace(); 
					} 
				} 
 
 			} 
 	}

 	/**
	 * Generates a random UUID String
	 * 
	 **/
 	//return A long random string
	public static String randomTag() { 
 		return java.util.UUID.randomUUID().toString(); 	
 	}

 	/**
	 * Generates a random long number
	 **/
 	public static Long randomCSeq() { 
 		int cseq = (new Random()).nextInt(); 
 		if (cseq < 0) 
 			cseq =-cseq; 
		return new Long(cseq); 
 	}

 	/**
	 * Increments a long number and if it goes past the limit, sets it to 0
	 * @param cseq
	 *            number to increment
	 **/
 	public static long incCSeq(long cseq) { 
 		if (++cseq > 2147483648L) 
 			cseq = 0L; 
 		return cseq; 
 	}
 	
	
/**
	 * Encodes a String to MD5
	 * 
	 * @param plaintext
	 *            String to encode
	 **/
 	public static String md5string(String plaintext) { 
		MessageDigest m;
		try { 
			m = MessageDigest.getInstance("MD5"); 
			m.reset(); 
			m.update(plaintext.getBytes()); 
			byte[] digest = m.digest(); 
			BigInteger bigInt = new BigInteger(1, digest); 
			String hashtext = bigInt.toString(16); 
			
			// Now we need to zero pad it if you actually want the full 32 
			// chars. 
			while (hashtext.length() < 32) { 
				hashtext = "0" + hashtext; 
			} 
			
			return hashtext; 
		} catch (NoSuchAlgorithmException e) { 
			e.printStackTrace(); 
		} 
		return null; 
				 
 	}   
 } 

	


