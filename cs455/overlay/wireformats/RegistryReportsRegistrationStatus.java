package cs455.overlay.wireformats;

import cs455.overlay.routing.RoutingEntry;

import java.io.*;
import java.util.Date;
import java.util.List;

/*
The contents of the response message generated  by  the  registry are depicted below. The success or
failure of the registration request should be indicated in the status field of the response message.

byte: Message type (REGISTRY_REPORTS_REGISTRATION_STATUS)
int: Success status; Assigned ID if successful, -1 in case of a failure
byte: Length of following "Information string" field
byte[^^]: Information string; ASCII charset
 */

public class RegistryReportsRegistrationStatus implements Event {

    private int type;
    private int status;
    private String information;
    private long timestamp;

    public RegistryReportsRegistrationStatus() {
    }

    public RegistryReportsRegistrationStatus(int status, String information) {
        this.type = Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS;
        this.status = status;
        this.information = information;
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
        return null;
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
        return this.information;
    }

    @Override
    public void setInformation(String info) {
        this.information = info;
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int getNodeId() {
        return this.status;
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

        this.status = din.readInt();
        int identifierLength = din.readInt();
        byte[] identifierBytes = new byte[identifierLength];
        din.readFully(identifierBytes);
        this.information = new String(identifierBytes);
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
        dout.writeInt(status);
        int elementLength = information.length();
        dout.writeInt(elementLength);
        dout.write(information.getBytes());
        dout.writeLong(timestamp);
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();

        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }
}
