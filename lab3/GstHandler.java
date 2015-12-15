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
	void start(int myPort, String distantAddress, int distantPort) { 
 		//distantPort= 5072;
 		System.out.println(myPort);
 		System.out.println(distantAddress);
 		System.out.println(distantPort);
 		
 		String pipelineSend = String.format(" alsasrc ! mulawenc ! rtppcmupay ! udpsink host=%s port=%d",distantAddress, distantPort); 
		
		String pipelineReceive = String.format(" udpsrc port=%d caps=\"application/x-rtp\" ! queue ! rtppcmudepay ! mulawdec ! audioconvert ! autoaudiosink",myPort); 
 		
 		callerPipelineSend = Pipeline.launch(pipelineSend); 
 		callerPipelineReceive = Pipeline.launch(pipelineReceive); 
 
 		callerPipelineSend.setState(State.PLAYING);
 		callerPipelineReceive.setState(State.PLAYING);
}
	void end() { 
 
 	callerPipelineSend.setState(State.NULL); 
 	callerPipelineReceive.setState(State.NULL); 
 
 	Gst.quit(); 
 	} 
 }