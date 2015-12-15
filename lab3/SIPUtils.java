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


class SIPUtils{
	public static HashMap addContact(HashMap contacts,String username,String sipAddress){
	contacts.put(username,sipAddress);
	return contacts;	
	}	
	

	public static String setSipAddress(String username,String ipAddress){
		String sipAddress= "sip" + ":" + username + "@" + ipAddress + ":" + "5070";
		return sipAddress;
	}
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
    }catch (SocketException e) {
     throw new RuntimeException(e);
    }

	}
	// get the IP address of the machine 
	public static String getIp() throws IOException{
		URL whatismyip = new URL("http://checkip.amazonaws.com"); 
 		BufferedReader in = null; 
 		try { 
 			in = new BufferedReader(new InputStreamReader( 
			whatismyip.openStream())); 
 			String ip = in.readLine(); 
				return ip; 
			} finally { 
 						if (in != null) { 
 							try { 
 								in.close(); 
								} catch (IOException e) { 
								e.printStackTrace(); 
								} 
						} 
 
 			} 
 	}
 	//return A long random string
	public static String randomTag() { 
 		return java.util.UUID.randomUUID().toString(); 	
 	}
 	public static Long randomCSeq() { 
 		int cseq = (new Random()).nextInt(); 
 		if (cseq < 0) 
 			cseq =-cseq; 
		return new Long(cseq); 
 	}
 	public static long incCSeq(long cseq) { 
 		if (++cseq > 2147483648L) 
 		cseq = 0L; 
 		return cseq; 
 	}
 	
	
	/*HashMap FormatMyMessage (HashMap [] currentMessages, String inComingMessage){
	//1 : Adds "me : " at the beginning of the message 
	//2 : Adds the modified message at the end of the hashmap 
	}*/
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

	


