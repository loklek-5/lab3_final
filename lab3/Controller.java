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
import java.util.Observable;


/**
 * This class is our controller for the view GUI
 * according to MVC design pattern
 */

class Controller {

	private static GUISIP gui ;
	static SIPClient mySIP;
	
	public static void main(String[] args) {
		
		
		String user = args[0];
		System.out.println(user);
		mySIP = new SIPClient(args[0],null);
		GUISIP gui = new GUISIP();
		
		

	    try {
	        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
	            if ("Nimbus".equals(info.getName())) {
	                javax.swing.UIManager.setLookAndFeel(info.getClassName());
	                break;
	            }
	        }
	    } catch (ClassNotFoundException ex) {
	        java.util.logging.Logger.getLogger(GUISIP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	    } catch (InstantiationException ex) {
	        java.util.logging.Logger.getLogger(GUISIP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	    } catch (IllegalAccessException ex) {
	        java.util.logging.Logger.getLogger(GUISIP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
	        java.util.logging.Logger.getLogger(GUISIP.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	    }

	    /* Create and display the form */
	    java.awt.EventQueue.invokeLater(new Runnable() {
	        public void run() {
	        	
	            new GUISIP().setVisible(true);
	           
	        }
	    });
    }

     
	
	}	
