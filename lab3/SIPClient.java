package lab3;
import java.util.Properties;
import javax.sip.*; 
import java.text.ParseException; 
import java.util.ArrayList; 
import java.util.List; 
import javax.sip.address.AddressFactory; 
import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.*; 
import gov.nist.javax.sip.address.AddressFactoryImpl.*;
import gov.nist.javax.sip.SipStackImpl.*;
import javax.sip.message.*;
import java.io.*;
import java.net.InetAddress;
import javazoom.jl.player.*;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Observable;





class SIPClient extends Observable implements SipListener{
	
	private SipFactory sipFactory;
	public boolean isInACall=false;
	public boolean theyReplyed= false;
	Request inviteRequest;
	ServerTransaction inviteTid;
	public boolean theyCancel= false;
	private MessageFactory messageFactory;
	private SipStack sipStack; 
 	private SipProvider sipProvider; 
 	private AddressFactory addressFactory; 
 	private HeaderFactory headerFactory; 
 	private SipURI mySIPURI; 
 	private String myPassword; 
 	private ClientTransaction currentCT;
 	private ServerTransaction currentST;
 	public Boolean replyToCall ;
 	private GstHandler gstreamerManager;
 	private String sipAddress;
 	public String user;
 	public boolean online = false ;
 	public String newmessage = "false" ;
 	HashMap<String, String> hmap = new HashMap<String, String>();
 	HashMap<String, String> contacts = new HashMap<String, String>();
 	FileInputStream fis;
 	Player playMP3;
 	// SIP Object manages SIP communications 
	//sipAddress, address in sip format (sip:user:password@host:port)
	//The host or IP to which the application should listen
	//The GUI object that wants to be notified of callers 
	SIPClient(String username, String listenHost) {
		try{
			user = username;
			System.out.println("je me");
			sipAddress= "sip" + ":" + username + "@" + SIPUtils.getPrivateIp() + ":" + "5070"; 
			//Returns an instance of a SipFactory. This is a singleton class so this method is the global access point for the SipFactory. 
			sipFactory = SipFactory.getInstance();
			sipFactory.setPathName("gov.nist");
			//Creates an instance of the AddressFactory implementation. This method ensures that only one instance of an AddressFactory is returned to the application, no matter how often this method is called.
			addressFactory = sipFactory.createAddressFactory();
			mySIPURI = (SipURI) addressFactory.createURI(sipAddress);
			if (null != mySIPURI.getUserPassword()) { 
 				myPassword = mySIPURI.getUserPassword(); 
				mySIPURI.setUserPassword(null); 
			}else{ 
 			myPassword = null; 
 			}
 			Properties properties = new Properties();
 			properties.setProperty("javax.sip.STACK_NAME", "SIPManager");
 			sipStack = sipFactory.createSipStack(properties);
 			int listenPort; 
 			if (mySIPURI.getPort() > 0) {
 				listenPort = mySIPURI.getPort();
 			}else {
 				listenPort = 5060;
 			}
 			if (null == listenHost) { 
 				listenHost = mySIPURI.getHost();
 				
			}
 				ListeningPoint listeningPointTCP = sipStack.createListeningPoint(listenHost, listenPort, "tcp"); 
 				ListeningPoint listeningPointUDP = sipStack.createListeningPoint(listenHost, listenPort, "udp"); 
 				sipProvider = sipStack.createSipProvider(listeningPointTCP); 
				sipProvider.addListeningPoint(listeningPointUDP); 
				sipProvider.addSipListener(this); 
 				headerFactory = sipFactory.createHeaderFactory(); 
 				messageFactory = sipFactory.createMessageFactory(); 
 				isInACall = false; 
 				//appGUI = caller; 
 			} catch (Exception e) { 
 				e.printStackTrace(); 
 				System.exit(1); 
 			} 
 	}
 	void sendREGISTER(String registrar, int registrarPort,String registrarTransport){
 		try { 
 			CallIdHeader callIdHeader = sipProvider.getNewCallId();
 			//CSEQHeader field  
 			CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(SIPUtils.randomCSeq(), Request.REGISTER);
 			
 			//we register our address 
 			Address addressToRegister = addressFactory.createAddress("sip:lokmane@sip.linphone.org");
 			//toheader field  with our address 
 			ToHeader toHeader = headerFactory.createToHeader(addressToRegister,null);
 			//fromheader field  
 			FromHeader fromHeader = headerFactory.createFromHeader(addressToRegister, SIPUtils.randomTag());
 			//Max number of hops to reach the destination 
 			MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
 			ViaHeader viaHeader = headerFactory.createViaHeader(mySIPURI.getHost(), sipProvider.getListeningPoint(registrarTransport).getPort(),registrarTransport, null); 
 			List<ViaHeader> viaHeaders = new ArrayList<ViaHeader>(); 
 			viaHeaders.add(viaHeader);
 			ContactHeader contactHeader = headerFactory.createContactHeader(addressFactory.createAddress(mySIPURI));
 			//finally we create our regisration request
 			Request request = messageFactory.createRequest(addressFactory.createURI("sip:" + registrar),Request.REGISTER, callIdHeader, cSeqHeader, fromHeader, 
 			toHeader, viaHeaders, maxForwardsHeader);
 			request.addHeader(contactHeader);
 			System.out.println(request);
 			//curent transaction
 			currentCT = sipProvider.getNewClientTransaction(request); 
 			//we send the request
 			currentCT.sendRequest();
 			}catch (Exception e) { 
 				e.printStackTrace(); 
 				System.exit(1); 
 			} 	
 		} 
 		
 	void sendCANCEL() {
 		try {
 			Request request = currentCT.createCancel();
 			sipProvider.sendRequest(request);
 			} catch (SipException e) { 
 				e.printStackTrace();
 				System.exit(1);
 				} 
 	}
 	void acceptINVITE(boolean reply) {
 		replyToCall = reply;
 		
 	}
 	void sendBYE() {
 		try { 
 			if (null != gstreamerManager)
 				gstreamerManager.end();
 			Dialog dialog = null;
 			if (null != currentCT) 
 				dialog = currentCT.getDialog();
 			else if (null != currentST) 
 				dialog = currentST.getDialog(); 
 			Request request = dialog.createRequest(Request.BYE); 
 			CSeqHeader cSeqHeader = (CSeqHeader) request.getHeader("CSeq"); 
 			cSeqHeader.setSeqNumber(SIPUtils.incCSeq(cSeqHeader.getSeqNumber())); 
 			ClientTransaction newTransaction = sipProvider.getNewClientTransaction(request);
 			sipProvider.sendRequest(request);
 			isInACall = false; 
 			} catch (SipException | InvalidArgumentException e) { 
 				e.printStackTrace(); 
 			System.exit(1);
 			}  
 	}
 	
 	//function used for the online status to check if the our contacts are online 
 	void sendHEARTBEAT(String sipAddressTocheck){
 		try{
 		String transport = "udp";
 		Address toAddress = addressFactory.createAddress(sipAddressTocheck);
 		ToHeader toHeader = headerFactory.createToHeader(toAddress, null); 
 		Address fromAddress = addressFactory.createAddress(mySIPURI); 
 		FromHeader fromHeader = headerFactory.createFromHeader(fromAddress,SIPUtils.randomTag());
 		CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(SIPUtils.randomCSeq(), Request.MESSAGE);
 		CallIdHeader callIdHeader = sipProvider.getNewCallId();
 		MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
 		ViaHeader viaHeader = headerFactory.createViaHeader(mySIPURI.getHost(), sipProvider.getListeningPoint(transport).getPort(),
 		 transport, null); 
 		List<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
 		viaHeaders.add(viaHeader);
 		ContactHeader contactHeader = headerFactory.createContactHeader(fromAddress); 
 		Request heartBeat = messageFactory.createRequest(toAddress.getURI(),Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader, 
 		toHeader, viaHeaders, maxForwardsHeader); 
 					String myInfo = "AREYOUALIVE";
 					ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("text", "plain");
 					ContentLengthHeader contentLengthHeader = headerFactory.createContentLengthHeader(myInfo.length());
 					heartBeat.setContent(myInfo, contentTypeHeader);
 					heartBeat.setContentLength(contentLengthHeader);
 					currentCT = sipProvider.getNewClientTransaction(heartBeat); 
 					System.out.println(heartBeat);
 					currentCT.sendRequest();
 					} catch (Exception e) {
 			e.printStackTrace();
 			System.exit(1);
 			}  
 	}
 	//function used to send the instant messages 
 	void sendIM(String sipAddressTocheck,String message){
 		try{
 		String transport = "udp";
 		Address toAddress = addressFactory.createAddress(sipAddressTocheck);
 		ToHeader toHeader = headerFactory.createToHeader(toAddress, null); 
 		Address fromAddress = addressFactory.createAddress(mySIPURI);
 		FromHeader fromHeader = headerFactory.createFromHeader(fromAddress,SIPUtils.randomTag());
 		CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(SIPUtils.randomCSeq(), Request.MESSAGE);
 		CallIdHeader callIdHeader = sipProvider.getNewCallId();
 		MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
 		ViaHeader viaHeader = headerFactory.createViaHeader(mySIPURI.getHost(), sipProvider.getListeningPoint(transport).getPort(),
 		 transport, null); 
 		List<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
 		viaHeaders.add(viaHeader);
 		message = "IMM:" + user + "-->" + message;
 		ContactHeader contactHeader = headerFactory.createContactHeader(fromAddress); 
 		Request msg = messageFactory.createRequest(toAddress.getURI(),Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader, 
 		toHeader, viaHeaders, maxForwardsHeader); 
 					//String myInfo = "AREYOUALIVE";
 					ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("text", "plain");
 					ContentLengthHeader contentLengthHeader = headerFactory.createContentLengthHeader(message.length());
 					msg.setContent(message, contentTypeHeader);
 					msg.setContentLength(contentLengthHeader);
 					currentCT = sipProvider.getNewClientTransaction(msg); 
 					System.out.println(msg);
 					currentCT.sendRequest();
 					} catch (Exception e) {
 			e.printStackTrace();
 			System.exit(1);
 			}  
 	}
 	void sendINVITE(String sipAddressToCall) {
 		try{
 		String transport = "udp";
 		Address toAddress = addressFactory.createAddress(sipAddressToCall);
 		ToHeader toHeader = headerFactory.createToHeader(toAddress, null); 
 		Address fromAddress = addressFactory.createAddress(mySIPURI); 
 		FromHeader fromHeader = headerFactory.createFromHeader(fromAddress,SIPUtils.randomTag());
 		CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(SIPUtils.randomCSeq(), Request.INVITE);
 		CallIdHeader callIdHeader = sipProvider.getNewCallId();
 		MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
 		ViaHeader viaHeader = headerFactory.createViaHeader(mySIPURI.getHost(), sipProvider.getListeningPoint(transport).getPort(),
 		 transport, null); 
 		List<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
 		viaHeaders.add(viaHeader); 
 		ContactHeader contactHeader = headerFactory.createContactHeader(fromAddress); 
 		Request request = messageFactory.createRequest(toAddress.getURI(),Request.INVITE, callIdHeader, cSeqHeader, fromHeader, 
 		toHeader, viaHeaders, maxForwardsHeader); 
 		request.addHeader(contactHeader);
 		currentCT = sipProvider.getNewClientTransaction(request);
 		currentCT.sendRequest();
 		} catch (Exception e) {
 			e.printStackTrace();
 			System.exit(1);
 			} 
 	}


 	@Override
 	public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) { 
 	} 
 	public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) { 
 	} 
 	@Override 
 	public void processIOException(IOExceptionEvent exceptionEvent) { 
	}
	@Override 
	public void processTimeout(TimeoutEvent timeoutEvent) { 
	}
	@Override
	public void processResponse(ResponseEvent responseEvent) { 
	int statusCode = responseEvent.getResponse().getStatusCode();
	System.out.println(statusCode);
	String method = responseEvent.getClientTransaction().getRequest().getMethod(); 
	if (statusCode == Response.UNAUTHORIZED) {
		System.out.println("juste apres");
		Response reponse = responseEvent.getResponse();
		System.out.println(reponse);
		//if (null != responseEvent.getResponse().getHeader("WWW­-Authenticate")) {
		try {
			if (null == currentCT.getDialog()){
				currentCT.terminate();
			}
			else{ 
				currentCT.getDialog().delete();
			}
			Request original = responseEvent.getClientTransaction().getRequest(); 
 			//System.out.println(responseEvent.getResponse());
			WWWAuthenticateHeader authHeader =(WWWAuthenticateHeader) responseEvent.getResponse().getHeader("WWW-Authenticate");
			//System.out.println(authHeader);
			String scheme = authHeader.getScheme();
			//System.out.println(scheme);
			AuthorizationHeader authorizationHeader = headerFactory.createAuthorizationHeader(scheme);
			System.out.println(authorizationHeader);
			authorizationHeader.setUsername(mySIPURI.getUser()); 
 			authorizationHeader.setRealm(authHeader.getRealm()); 
			authorizationHeader.setNonce(authHeader.getNonce()); 
 			authorizationHeader.setAlgorithm("MD5"); 
 			
			
 			authorizationHeader.setURI(addressFactory.createURI("sip:lokmane@sip.linphone.org")); 
 			//System.out.println(authorizationHeader);
 			//mySIPURI= SipURI("sip:lokmane@sip.linphone.org");
 			String hash1 = SIPUtils.md5string(String.format("%s:%s:%s", "lokmane",authHeader.getRealm(),"Lokmane"));
			String hash2 = SIPUtils.md5string(String.format("%s:%s", original.getMethod(), original.getRequestURI().toString())); 
			System.out.println(authHeader.getNonce());
			//System.out.println(hash2);
			String response = null;
			if (null != authHeader.getQop()&& authHeader.getQop().equals("auth")) { 
 				String finalStr = String.format("%s:%s:%s:%s:%s:%s",hash1,authHeader.getNonce(),"nonceCount","cNone",authHeader.getQop(),hash2) ; 
 				System.out.println(finalStr); 
 				response = SIPUtils.md5string(finalStr);
 				//System.exit(1); // TODO: implement this authorization
 			} else {
 				response = SIPUtils.md5string(String.format("%s:%s:%s", hash1,authHeader.getNonce(),hash2)); 
 			}
 			
 			authorizationHeader.setResponse(response); 
 			Request request = (Request) original.clone(); 
			request.addHeader(authorizationHeader); 
 			CSeqHeader cseqHeader = (CSeqHeader) request.getHeader("CSeq"); 
 			cseqHeader.setSeqNumber(SIPUtils.incCSeq(cseqHeader.getSeqNumber())); 
 			ViaHeader viaHeader = (ViaHeader) request.getHeader("Via"); 
 			viaHeader.setBranch(SIPUtils.randomTag());
 			System.out.println(request); 
 			currentCT = sipProvider.getNewClientTransaction(request); 
 			currentCT.sendRequest(); 
 			} catch (Exception e) { 
 			e.printStackTrace(); 
 			System.exit(1); 
 			} 
 	}else if (statusCode==Response.ACCEPTED){
 			online = true ;
 			System.out.println(online);
 			
 	} else if (statusCode==Response.BUSY_HERE){
 		try{
			fis = new FileInputStream("../songs/busy.mp3");
            playMP3 = new Player(fis);

             playMP3.play();

        }  catch(Exception e){
             System.out.println(e);
           }
 	}else if (statusCode == Response.RINGING) {
 			if (method.equals(Request.INVITE)) {
 			System.out.println("ca sonne .....");
 			try{

             fis = new FileInputStream("../songs/wait.mp3");
             Player playMP3 = new Player(fis);

             playMP3.play();

        }  catch(Exception e){
             System.out.println(e);
           }
 			}
 	} else if (statusCode == Response.REQUEST_TERMINATED) {
 		if (method.equals(Request.INVITE)) {
 			currentCT = null;
 		}
 	}else if (statusCode == Response.OK) {
 			if (method.equals(Request.INVITE)) { 
 				try {
 					theyReplyed = true;
 					Dialog dialog = responseEvent.getClientTransaction().getDialog(); 
 					Request acknowledge = dialog.createAck(dialog.getLocalSeqNumber());
 					dialog.sendAck(acknowledge);
 					Request gstreamer = responseEvent.getClientTransaction().getDialog().createRequest(Request.MESSAGE);
 					int myGSPort = mySIPURI.getPort() + 1; 
 					String myGSInfo = mySIPURI.getHost() + ":" + myGSPort;
 					ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("text", "plain");
 					ContentLengthHeader contentLengthHeader = headerFactory.createContentLengthHeader(myGSInfo.length());
 					gstreamer.setContent(myGSInfo, contentTypeHeader);
 					gstreamer.setContentLength(contentLengthHeader);
 					currentCT = sipProvider.getNewClientTransaction(gstreamer); 
 					System.out.println(gstreamer);
 					currentCT.sendRequest(); 
 					} catch (SipException | ParseException | InvalidArgumentException e) {
 					e.printStackTrace();
 					System.exit(1);
 					}  
 			} else if (method.equals(Request.MESSAGE)) {
			System.out.println("je suis la ");
			String theirGSInfo = new String(responseEvent.getResponse().getRawContent());
			String theirGSHost = theirGSInfo.split(":")[0];
			int theirGSPort = Integer.parseInt(theirGSInfo.split(":")[1]);
			int myGSPort = mySIPURI.getPort() + 1;
			gstreamerManager =  new GstHandler();
			gstreamerManager.start(myGSPort,theirGSHost,theirGSPort); 
		}
	}
 		
 
 // scheme... 
 
	//String method = responseEvent.getClientTransaction().getRequest().getMethod();  
	//}
}
	 
 	@Override 
 	public void processRequest(RequestEvent requestEvent) { 
 		try { 
			String method = requestEvent.getRequest().getMethod(); 
			System.out.println(method);
			
			if (method.equals(Request.INVITE)) { 
				inviteRequest = requestEvent.getRequest();
				theyReplyed=false;
				if (isInACall) { 

					// This code is run if a UA sends an INVITE when a call is 
					// already taking place. 
					Response busy = messageFactory.createResponse( Response.BUSY_HERE, requestEvent.getRequest()); 
					ServerTransaction serverTransaction = sipProvider.getNewServerTransaction(requestEvent.getRequest()); 
					serverTransaction.sendResponse(busy); 
						currentST = serverTransaction; 

				} 
				else { 
					

					/* 
					* Answering an INVITE. First respond that it's ringing. 
					* Then wait for a decision. 
					*/ 

					isInACall = true; 
					replyToCall = null;
					currentST = requestEvent.getServerTransaction(); 
					Response ringing = messageFactory.createResponse( 
					Response.RINGING, requestEvent.getRequest()); 
					ServerTransaction serverTransaction = sipProvider.getNewServerTransaction(requestEvent.getRequest()); 

					serverTransaction.sendResponse(ringing); 
					inviteTid = serverTransaction;
					

					
					try{

             		fis = new FileInputStream("/home/lokman/M7017E-withoutCancel/songs/ring.mp3");
             		playMP3 = new Player(fis);

             		playMP3.play();
             		


        			}  catch(Exception e){
            		 System.out.println(e);
           			}	
           			System.out.println(replyToCall);
           			Thread.sleep(5000);
           			//if (theyCancel){
           			//	break;
           			//}				
					while (replyToCall == null) { 
						System.out.print(".");
						Thread.sleep(50); 
					} 

					if (theyCancel==false){
					if (replyToCall) { 

					// Accept the INVITE. 

						Response acceptCall; 
						acceptCall = messageFactory.createResponse(Response.OK, requestEvent.getRequest()); 

						ContactHeader contactHeader = headerFactory.createContactHeader(addressFactory.createAddress(mySIPURI)); 

						acceptCall.addHeader(contactHeader); 

						serverTransaction.sendResponse(acceptCall);  
						currentST = serverTransaction; 

					} else { 

						// Reject the INVITE. 

						Response busy = messageFactory.createResponse( Response.BUSY_HERE, requestEvent.getRequest()); 

						serverTransaction = sipProvider.getNewServerTransaction(requestEvent.getRequest()); 

						serverTransaction.sendResponse(busy); 

						currentST = serverTransaction; 

						isInACall = false; 

					}
					} 

				replyToCall = false; 
				}

			}else if (method.equals(Request.BYE)) {
			System.out.println("je quitte");
				if (null != gstreamerManager)
					gstreamerManager.end();
				Response bye = messageFactory.createResponse(Response.OK,requestEvent.getRequest());
				requestEvent.getServerTransaction().sendResponse(bye);
				try { 
					requestEvent.getServerTransaction().terminate(); 
					} catch (ObjectInUseException e1) { 
						requestEvent.getServerTransaction().getDialog().delete();
					}
					isInACall = false; 

			} else if (method.equals(Request.CANCEL)) { 
				theyCancel=true;
				//messageFactory.createResponse(Response.REQUEST_TERMINATED, requestEvent.getRequest()); 
				//ServerTransaction serverTransaction = sipProvider.getNewServerTransaction(requestEvent.getRequest()); 

					//serverTransaction.sendResponse(ringing);

				currentST=requestEvent.getServerTransaction();
				Response terminateCall = messageFactory.createResponse(Response.OK, requestEvent.getRequest());
				currentST.sendResponse(terminateCall);
				
				Response callTerminated = messageFactory.createResponse(Response.REQUEST_TERMINATED,inviteRequest);
				System.out.println(callTerminated);
				inviteTid.sendResponse(callTerminated); 
				System.out.println(terminateCall);
				//ServerTransaction serverTransaction = sipProvider.getNewServerTransaction(requestEvent.getRequest()); 
				requestEvent.getServerTransaction().terminate();
				//ServerTransaction serverTransaction = sipProvider.getNewServerTransaction(requestEvent.getRequest());
				
				currentST = null;
			}else if (method.equals(Request.MESSAGE)) {
			String raw = new String(requestEvent.getRequest().getRawContent());

			String idMsg = raw.split(":")[0];
			if (idMsg.equals("IMM")){
				newmessage = raw.split(":")[1];
				hmap.put(raw.split(":")[1], raw.split("-->")[0]);
				setChanged();
      			notifyObservers(raw.split(":")[1]);
				System.out.println(raw.split(":")[1]);
				Response ackIMM = messageFactory.createResponse(Response.ACCEPTED,requestEvent.getRequest());
				System.out.println(ackIMM);
				ContactHeader contactHeader = headerFactory.createContactHeader(addressFactory.createAddress(mySIPURI)); 
				ackIMM.addHeader(contactHeader); 
				ServerTransaction serverTransaction = sipProvider.getNewServerTransaction(requestEvent.getRequest()); 
				serverTransaction.sendResponse(ackIMM);	 
				//System.out.println(onlineResponse);
				
			} else if (raw.equals("AREYOUALIVE")){
				Response onlineResponse = messageFactory.createResponse(Response.ACCEPTED,requestEvent.getRequest());
				System.out.println(onlineResponse);
				ContactHeader contactHeader = headerFactory.createContactHeader(addressFactory.createAddress(mySIPURI)); 
				onlineResponse.addHeader(contactHeader); 
				ServerTransaction serverTransaction = sipProvider.getNewServerTransaction(requestEvent.getRequest()); 
				serverTransaction.sendResponse(onlineResponse);	 
				System.out.println(onlineResponse);
				} 
			else {
				String theirGSHost = raw.split(":")[0]; 

				int theirGSPort = Integer.parseInt(raw.split(":")[1]); 

				int myGSPort = mySIPURI.getPort() + 1; 

				GstHandler gstreamerManager = new GstHandler(); 

				gstreamerManager.start(myGSPort, theirGSHost, theirGSPort); 

				String myGSInfo = mySIPURI.getHost() + ":" + myGSPort; 


				Response gstreamer = messageFactory.createResponse(Response.OK,requestEvent.getRequest()); 

				ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("text", "plain"); 

				ContentLengthHeader contentLengthHeader = headerFactory.createContentLengthHeader(myGSInfo.length()); 


				gstreamer.setContent(myGSInfo, contentTypeHeader); 

				gstreamer.setContentLength(contentLengthHeader); 

				requestEvent.getServerTransaction().sendResponse(gstreamer); 
			 
			 	System.out.println(gstreamer); 				
			}


		}
	}
	catch (Exception e) { 
			e.printStackTrace(); 
			System.exit(1); 
	}
 }
 public void changeSomething() {
      // Notify observers of change
      
    }
}



