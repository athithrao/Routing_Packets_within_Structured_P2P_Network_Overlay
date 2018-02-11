package cs455.overlay.wireformats;

import cs455.overlay.routing.RoutingEntry;

import java.io.*;
import java.util.Date;
import java.util.List;

/*
Upon  starting  up, each messaging node should register  its  IP  address, and port  number  with  the
registry. It should be possible to register messaging nodes that are running on the same host but are
listening to communications on different ports. There should be 4 fields in this registration request:

byte: Message Type (OVERLAY_NODE_SENDS_REGISTRATION)
byte: length of following "IP address" field
byte[^^]: IP address; from InetAddress.getAddress()
int: Port number
 */

public class OverlayNodeSendsRegistration implements Event {

    private int type;
    private byte[] ipAddress;
    private int listenPortNumber;
    private int localPortNumber;
    private long timestamp;

    public OverlayNodeSendsRegistration() {
    }

    public OverlayNodeSendsRegistration(byte[] ipAddress, int listenPortNumber, int localPortNumer) {
        this.type = Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
        this.ipAddress = ipAddress;
        this.listenPortNumber = listenPortNumber;
        this.localPortNumber = localPortNumer;
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
        return 0;
    }

    @Override
    public void setNodeId(int id) {

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

        dout.writeInt(type);
        int elementLength = ipAddress.length;
        dout.writeInt(elementLength);
        dout.write(ipAddress);
        dout.writeInt(listenPortNumber);
        dout.writeInt(localPortNumber);
        dout.writeLong(timestamp);
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();

        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }
}
