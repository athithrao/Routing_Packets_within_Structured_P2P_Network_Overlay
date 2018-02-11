package cs455.overlay.wireformats;

import cs455.overlay.routing.RoutingEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
The registry informs each node about the IDs (it should not include IP addresses) of all nodes in
the system. This information is used in the testing part of the overlay to randomly select nodes that
the messages should be sent to.
The registry includes all this information in a REGISTRY_SENDS_NODE_MANIFEST message. The contents
of the manifest message are different  for  each  messaging  node  (since  the  routing  table  at  every
messaging node would be different). The wire format is shown when NR =3,  if NR=4 there will also be
an entry for a node hops away.
byte: Message type; REGISTRY_SENDS_NODE_MANIFEST
byte: routing table size NR
int: Node ID of node 1 hop away
byte: length of following "IP address" field
byte[^^]: IP address of node 1 hop away; from InetAddress.getAddress()
int: Port number of node 1 hop away
int: Node ID of node 2
hops away
byte: length of following "IP address" field
byte[^^]: IP address of node 2 hops away; from InetAddress.getAddress()
int: Port number of node 2 hops away
int: Node ID of node 4 hops away
byte: length of following "IP address" field
byte[^^]: IP a
ddress of node 4 hops away; from InetAddress.getAddress()
int: Port number of node 4 hops away
byte: Number of node IDs in the system
int[^^]: List of all node IDs in the system
 [Note no IPs are included]
 */

public class RegistrySendsNodeManifest implements Event {

    private int type;
    private int routingTableSize;
    private List<RoutingEntry> routingTable = null;
    private int totalNodeId;
    private List<Integer> nodeIdList = null;
    private long timestamp;

    public RegistrySendsNodeManifest() {
        routingTable = new ArrayList<>();
        nodeIdList = new ArrayList<>();
    }

    public RegistrySendsNodeManifest(List<RoutingEntry> routingTable, List<Integer> nodeIdList) {
        this.type = Protocol.REGISTRY_SENDS_NODE_MAINFEST;
        this.routingTable = routingTable;
        this.nodeIdList = nodeIdList;
        this.routingTableSize = this.routingTable.size();
        this.totalNodeId = this.nodeIdList.size();
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

    public List<RoutingEntry> getRoutingTable() {
        return routingTable;
    }

    public List<Integer> getNodeIdList() {
        return nodeIdList;
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


    public void getType(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        int distance = 1;

        this.type = din.readInt();

        this.routingTableSize = din.readInt();

        for (int i = 1; i <= routingTableSize; i++) {
            int nodeid = din.readInt();
            int identifierLength = din.readInt();
            byte[] identifierBytes = new byte[identifierLength];
            din.readFully(identifierBytes);
            int port = din.readInt();
            RoutingEntry route = new RoutingEntry(distance, nodeid, identifierBytes, port);
            routingTable.add(route);
            distance *= 2;
        }

        int totalNodes = din.readInt();

        for (int i = 1; i <= totalNodes; i++) {
            nodeIdList.add(din.readInt());
        }
        this.timestamp = din.readLong();

        baInputStream.close();
        din.close();

    }

    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = (new DataOutputStream(new BufferedOutputStream(baOutputStream)));

        dout.writeInt(this.type);

        dout.writeInt(routingTableSize);

        for (RoutingEntry r : routingTable) {
            dout.writeInt(r.getNodeID());
            int elementLength = r.getIpAddress().length;
            dout.writeInt(elementLength);
            dout.write(r.getIpAddress());
            dout.writeInt(r.getListenPortNumber());
        }

        dout.writeInt(totalNodeId);

        for (Integer i : nodeIdList) {
            dout.writeInt(i);
        }

        dout.writeLong(timestamp);
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }
}
