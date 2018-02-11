package cs455.overlay.wireformats;

import cs455.overlay.node.Node;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/*
This class is implemented as a singleton class which shares objects between the TCP
receiver thread and the Messaging Node or the Registry, depending on who Node instance
set in the class.
 */

public class EventFactory {

    private static volatile EventFactory eventFactoryInstance;
    private static Node node = null;

    public EventFactory() {}

    //static method to create single instance of Event Factory
    public static EventFactory getInstance()
    {
        synchronized (EventFactory.class)
        {
            if(eventFactoryInstance == null)
            {
                eventFactoryInstance = new EventFactory();
            }
        }
        return eventFactoryInstance;
    }

    public void setNodeInstance(Node n)
    {
        node = n;
    }

    public void processMessage(byte[] message) throws IOException
    {
        int type = getMessageType(message);
        Event event = null;
        if(type == Protocol.OVERLAY_NODE_SENDS_REGISTRATION)
            event = new OverlayNodeSendsRegistration();
        else if(type == Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS)
            event = new NodeReportOverlaySetupStatus();
        else if(type == Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED)
            event  = new OverlayNodeReportsTaskFinished();
        else if(type == Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY)
            event = new OverlayNodeReportsTrafficSummary();
        else if(type == Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION)
            event = new OverlayNodeSendsDeregistration();
        else if(type == Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS)
            event = new RegistryReportsRegistrationStatus();
        else if(type == Protocol.REGISTRY_SENDS_NODE_MAINFEST)
            event = new RegistrySendsNodeManifest();
        else if(type == Protocol.REGISTRY_REQUESTS_TASK_INITIATE)
            event = new RegistryRequestsTaskInitiate();
        else if(type == Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY)
            event = new RegistryRequestsTrafficSummary();
        else if(type == Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS)
            event = new RegistryReportsDeregistrationStatus();
        else if(type == Protocol.OVERLAY_NODE_SENDS_DATA)
            event = new OverlayNodeSendsData();
        event.getType(message);
        node.onEvent(event);
    }

    public int getMessageType(byte[] message) throws IOException
    {
        int type;
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(message);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        type = din.readInt();
        baInputStream.close();
        din.close();
        return type;
    }

}

