package cs455.overlay.wireformats;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.util.StatisticsCollector;

import java.io.*;
import java.util.Date;
import java.util.List;

/*
Upon receipt of the REGISTRY_REQUESTS_TRAFFIC_SUMMARY message from the registry, the messaging
node  will  create  a  response  that  includes  summaries  of  the  traffic  that  it  has  participated  in.  The
summary will include information about messages that were sent,  received, and relayed by the node.
This message will have the following format.

byte: Message type; OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY
int: Assigned node ID
int: Total number of packets sent (only the ones that were started/initiated by the node)
int: Total number of packets relayed (received from a different node and forwarded)
long: Sum of packet data sent (only the ones that were started by the node)
int: Total number of packets received (packets with this node as final destination)
long: Sum of packet data received (only packets that had this node as final destination)

Once the OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY message is sent to the registry, the node must
reset the counters associated with traffic relating to the messages it has sent, relayed, and received
so far viz. the number of messages sent, summation of sent messages, etc.
*/

public class OverlayNodeReportsTrafficSummary implements Event {

    private int type;
    private int nodeId;
    private int totalPacketsSent;
    private int totalPacketsReceived;
    private int totalPacketsRelayed;
    private long sumPacketDataSent;
    private long sumPacketDataReceived;
    private long timestamp;

    public OverlayNodeReportsTrafficSummary() {
    }

    public OverlayNodeReportsTrafficSummary(int nodeId, StatisticsCollector collector) {
        this.type = Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY;
        this.nodeId = nodeId;
        this.totalPacketsSent = collector.getTotalPacketsSent();
        this.totalPacketsReceived = collector.getTotalPacketsReceived();
        this.totalPacketsRelayed = collector.getTotalPacketsRelayed();
        this.sumPacketDataSent = collector.getSumPacketDataSent();
        this.sumPacketDataReceived = collector.getSumPacketDataReceived();

        Date date = new Date();
        this.timestamp = date.getTime();
    }

    @Override
    public int getMessageType() {
        return this.type;
    }

    @Override
    public void setMessageType(int type) {
        this.type = type;
    }

    @Override
    public byte[] getIPAddress() {
        return new byte[0];
    }

    @Override
    public void setIPAddress(byte[] address) {

    }

    @Override
    public int getListenPortNumber() {
        return 0;
    }

    @Override
    public void setListenPortNumber(int port) {

    }

    @Override
    public int getLocalPortNumber() {
        return 0;
    }

    @Override
    public void setLocalPortNumber(int port) {

    }

    @Override
    public String getInformation() {
        return null;
    }

    @Override
    public void setInformation(String info) {

    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public void setStatus(int status) {

    }

    @Override
    public int getNodeId() {
        return this.nodeId;
    }

    @Override
    public void setNodeId(int id) {
        this.nodeId = nodeId;
    }

    @Override
    public List<RoutingEntry> getRoutingTable() {
        return null;
    }

    @Override
    public List<Integer> getNodeIdList() {
        return null;
    }

    @Override
    public void addtoTrace(int nodeId) {

    }

    @Override
    public List<Integer> getTrace() {
        return null;
    }

    @Override
    public int getDestinationId() {
        return 0;
    }

    @Override
    public int getSourceId() {
        return 0;
    }

    @Override
    public int getPayload() {
        return 0;
    }


    public int getTotalPacketsSent() {
        return totalPacketsSent;
    }

    public int getTotalPacketsReceived() {
        return totalPacketsReceived;
    }

    public int getTotalPacketsRelayed() {
        return totalPacketsRelayed;
    }

    public long getSumPacketDataSent() {
        return sumPacketDataSent;
    }

    public long getSumPacketDataReceived() {
        return sumPacketDataReceived;
    }

    @Override
    public void getType(byte[] marshalledBytes) throws IOException {

        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        this.type = din.readInt();

        this.nodeId = din.readInt();
        this.totalPacketsSent = din.readInt();
        this.totalPacketsReceived = din.readInt();
        this.totalPacketsRelayed = din.readInt();
        this.sumPacketDataSent = din.readLong();
        this.sumPacketDataReceived = din.readLong();
        this.timestamp = din.readLong();

        baInputStream.close();
        din.close();

    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = (new DataOutputStream(new BufferedOutputStream(baOutputStream)));

        dout.writeInt(type);
        dout.writeInt(nodeId);
        dout.writeInt(totalPacketsSent);
        dout.writeInt(totalPacketsReceived);
        dout.writeInt(totalPacketsRelayed);
        dout.writeLong(sumPacketDataSent);
        dout.writeLong(sumPacketDataReceived);
        dout.writeLong(timestamp);
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();

        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }
}
