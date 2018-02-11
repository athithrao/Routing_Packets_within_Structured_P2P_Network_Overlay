package cs455.overlay.wireformats;

import cs455.overlay.routing.RoutingEntry;

import java.io.*;
import java.util.Date;
import java.util.List;

/*
Once a node has completed its task of sending a certain number of messages ,it  informs the registry of
its  task  completion  using  the OVERLAY_NODE_REPORTS_TASK_FINISHED message. This message should
have the following format:

byte: Message type; OVERLAY_NODE_REPORTS_TASK_FINISHED
byte: length of following "IP address" field
byte[^^]: Node IP address:
int:  Node
Port number:
int:  nodeID
 */

public class OverlayNodeReportsTaskFinished implements Event {

    private int type;
    private byte[] ipAddress;
    private int listenPortNumber;
    private int nodeId;
    private long timestamp;

    public OverlayNodeReportsTaskFinished() {
    }

    public OverlayNodeReportsTaskFinished(byte[] ipAddress, int listenPortNumber, int nodeId) {
        this.type = Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED;
        this.ipAddress = ipAddress;
        this.listenPortNumber = listenPortNumber;
        this.nodeId = nodeId;
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
        return this.ipAddress;
    }

    @Override
    public void setIPAddress(byte[] address) {
        this.ipAddress = address;
    }

    @Override
    public int getListenPortNumber() {
        return this.listenPortNumber;
    }

    @Override
    public void setListenPortNumber(int port) {
        this.listenPortNumber = port;
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
        this.nodeId = id;
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

    @Override
    public int getTotalPacketsSent() {
        return 0;
    }

    @Override
    public int getTotalPacketsReceived() {
        return 0;
    }

    @Override
    public int getTotalPacketsRelayed() {
        return 0;
    }

    @Override
    public long getSumPacketDataSent() {
        return 0;
    }

    @Override
    public long getSumPacketDataReceived() {
        return 0;
    }

    @Override
    public void getType(byte[] marshalledBytes) throws IOException {

        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        this.type = din.readInt();

        int identifierLength = din.readInt();
        byte[] identifierBytes = new byte[identifierLength];
        din.readFully(identifierBytes);
        this.ipAddress = identifierBytes;
        this.listenPortNumber = din.readInt();
        this.nodeId = din.readInt();
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
        int elementLength = ipAddress.length;
        dout.writeInt(elementLength);
        dout.write(ipAddress);
        dout.writeInt(listenPortNumber);
        dout.writeInt(nodeId);
        dout.writeLong(timestamp);
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();

        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }
}
