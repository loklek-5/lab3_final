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

	private static SIPClient sipclient ;
	private static GUISIP gui ;

	public static void main(String[] args) {
		SIPClient sipclient = new SIPClient("bernard", null); 
		System.out.println(sipclient);
		System.out.println("sipclient");

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


	    // Event listener for call


        sipclient.addObserver((Observable obj, Object arg) -> { 
            System.out.println("\nReceived notfication: " + arg);
            if ( arg == "callIn")
        		HandleCallIn();
        	else
	            System.out.println("Non matching");

        });

        //new Thread(sipclient).start();

	    /* Create and display the form */
	    java.awt.EventQueue.invokeLater(new Runnable() {
	        public void run() {
	            new GUISIP().setVisible(true);
	        }
	    });
    }


	static void SendInvite(String sipAddress) {
        sipclient.sendINVITE(sipAddress);
	}	
	
	static void HandleCallIn() {
        gui.HandleCallIn();
	}	
}
/*package lab3;
import java.io.IOException;
import javax.swing.SwingUtilities; 
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


class main {
public static void main (String[] args){
	//SIPGUI sipGUI = new SIPGUI(); 
 	//SwingUtilities.invokeLater(sipGUI); 
 	SIPClient sipclient = new SIPClient("lokmane",null);
 	
 	Set set = sipclient.hmap.entrySet();
      Iterator iterator = set.iterator();
      while(iterator.hasNext()) {
         Map.Entry mentry = (Map.Entry)iterator.next();
         System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
         System.out.println(mentry.getValue());
      } 
	//sipclient.sendINVITE("sip:bimbo@10.42.0.60:5070");
	//System.out.println(SIPUtils.getPrivateIp());
	/*try{
	SIPUtils test = new SIPUtils();
	System.out.println(test.getIp());
	}catch (Exception e){
		System.out.println("stack");
	}*/
	//sipclient.sendREGISTER("sip.linphone.org",5060,"UDP");
	//sipclient.sendINVITE("sip:bimbo@10.42.0.60:5070");
	//boolean x = false ;
	//System.out.println("Do you want to stop?");

	//sipclient.sendIM("sip:bimbo@10.42.0.60:5070","AREYOUALIVE");
	    /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
       /* try {
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
        //</editor-fold>

        /* Create and display the form 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUISIP().setVisible(true);
            }
        });*/
    
	}
}