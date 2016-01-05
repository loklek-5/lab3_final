package lab3;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.State;


class GstHandler{
	Pipeline callerPipelineSend;
	Pipeline callerPipelineReceive;

	GstHandler(){
		String[] args = {};
		Gst.init("GstHandler",args);
		callerPipelineSend = new Pipeline();
		callerPipelineReceive = new Pipeline();

	}
	/**
	 * This function starts gstreamer to start a audio call with a remote single host
	 * 
	 * @param myPort
	 *            local port to receive UDP connection
	 * @param distantAddress
	 *            address of the remote host
	 * @param distantPort
	 *            port of the remote host
	 */
	void start(int myPort, String distantAddress, int distantPort) { 
 		System.out.println(myPort);
 		System.out.println(distantAddress);
 		System.out.println(distantPort);
 		
 		String pipelineSend = String.format(" alsasrc ! mulawenc ! rtppcmupay ! udpsink host=%s port=%d",distantAddress, distantPort); 
		
		String pipelineReceive = String.format(" udpsrc port=%d caps=\"application/x-rtp\" ! queue ! rtppcmudepay ! mulawdec ! audioconvert ! autoaudiosink",myPort); 
 		

 		//Here we use 2 pipelines for a better communication
 		callerPipelineSend = Pipeline.launch(pipelineSend); 
 		callerPipelineReceive = Pipeline.launch(pipelineReceive); 
 
 		callerPipelineSend.setState(State.PLAYING);
 		callerPipelineReceive.setState(State.PLAYING);
}
	/**
	 * This function ends the gstreamer communication by stopping the two pipelines
	 * and quitting Gst
	 */
	void end() { 
 
 		callerPipelineSend.setState(State.NULL); 
		callerPipelineReceive.setState(State.NULL); 
 
 		Gst.quit(); 
 	} 
 }