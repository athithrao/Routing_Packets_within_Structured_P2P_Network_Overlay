package cs455.overlay.wireformats;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.util.StatisticsCollector;

import java.io.IOException;
import java.util.List;
/*
the interface Event is implemented by all the messaging wireformats in this project.
 */

public interface Event {

 void getType(byte[] marshalledBytes) throws IOException;
 byte[] getBytes() throws IOException;

 int getMessageType();
 void setMessageType(int type);

 byte[] getIPAddress();
 void setIPAddress(byte[] address);

 int getListenPortNumber();
 void setListenPortNumber(int port);

 int getLocalPortNumber();
 void setLocalPortNumber(int port);

 String getInformation();
 void setInformation(String info);

 int getStatus();
 void setStatus(int status);

 int getNodeId();
 void setNodeId(int id);

 List<RoutingEntry> getRoutingTable();
 List<Integer> getNodeIdList();

 void addtoTrace(int nodeId);
 List<Integer> getTrace();

 int getDestinationId();
 int getSourceId();
 int getPayload();


 int getTotalPacketsSent();
 int getTotalPacketsReceived();
 int getTotalPacketsRelayed();
 long getSumPacketDataSent();
 long getSumPacketDataReceived();

}
