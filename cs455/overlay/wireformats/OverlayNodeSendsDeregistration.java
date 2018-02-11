package cs455.overlay.wireformats;

import cs455.overlay.routing.RoutingEntry;

import java.io.*;
import java.util.Date;
import java.util.List;

/*
When a messaging  node exits  it  should  deregister  itself.  It  does  so  by  sending  a  message  to  the
registry. This deregistration request includes the following fields

byte: Message Type (OVERLAY_NODE_SENDS_DEREGISTRATION)
byte: length of following "IP address" field
byte[^^]: IP address; from InetAddress.getAddress()
int: Port number
int: assigned Node ID
*/

public class OverlayNodeSendsDeregistration implements Event {

    private int type;
    private byte[] ipAddress;
    private int listenPortNumber;
    private int nodeID;
    private int localPortNumber;
    private long timestamp;

    public OverlayNodeSendsDeregistration() {

    }

    public OverlayNodeSendsDeregistration(byte[] ipAddress, int portNumber, int nodeID, int localPortNumber) {
        this.type = Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION;
        this.ipAddress = ipAddress;
        this.listenPortNumber = portNumber;
        this.nodeID = nodeID;
        this.localPortNumber = localPortNumber;
        Date date = new Date();
        this.timestamp = date.getTime();
    }

    @Override
    public int getMessageType() {
        return type;
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
        return this.localPortNumber;
    }

    @Override
    public void setLocalPortNumber(int port) {
        this.localPortNumber = port;
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
        return this.nodeID;
    }

    @Override
    public void setNodeId(int id) {
        this.nodeID = id;
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
        this.nodeID = din.readInt();
        this.localPortNumber = din.readInt();
        this.timestamp = din.readLong();

        baInputStream.close();
        din.close();

    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = (new DataOutputStream(new BufferedOutputStream(baOutputStream)));

        dout.writeInt(this.type);
        int elementLength = this.ipAddress.length;
        dout.writeInt(elementLength);
        dout.write(this.ipAddress);
        dout.writeInt(this.listenPortNumber);
        dout.writeInt(this.nodeID);
        dout.writeInt(this.localPortNumber);
        dout.writeLong(this.timestamp);
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();

        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }
}
