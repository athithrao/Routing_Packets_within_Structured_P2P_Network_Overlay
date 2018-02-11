package cs455.overlay.wireformats;

import cs455.overlay.routing.RoutingEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
Data packets can be fed into the overlay from any messaging node within the system. Packets are
sent from a source to a sink; it is possible that there might be zero or more intermediate nodes in the
system that relay packets en route to the sink.
Every node tracks the number of messages that it has relayed during communications within the overlay.
When a packet is ready to be sent from a source to the sink, the source node consults its routing table
to identify the best node that it should send the packet to. There are two situations: (1) there is an
entry for the sink in the routing  table, or (2)the  sink  does  not  exist  in  the  routing table and  the
messaging node must relay the packet to the closest node.
During  routing,  care  must  be  taken  to  ensure  that  you  don’t  change  directionality  i.e.  your  routing
decisions should target only nodes that are clockwise successors. You must also ensure that you do not
overshoot the sink-node you are trying to reach. Routing errors will result in a packet continuously
looping through the overlay and consuming bandwidth.
A key requirement for  the  dissemination  of packets within the overlay is that no messaging node
should  receive  the  same  packet  more  than  once.  This  should  be  achieved  without  having  to  rely  on
duplicate detection and suppression.

byte: Message type;OVERLAY_NODE_SENDS_DATA
int: Destination ID
int: Source ID
int:
Payload
int: Dissemination trace field length (number of hops)
int[^^]:Dissemination trace comprising nodeIDs that the packet traversed through
 */

public class OverlayNodeSendsData implements Event {

    private int type;
    private int destinationId;
    private int sourceId;
    private int payload;
    private List<Integer> trace = null;
    private long timestamp;

    public OverlayNodeSendsData() {
    }

    public OverlayNodeSendsData(int sourceId, int destinationId, int payload) {
        this.type = Protocol.OVERLAY_NODE_SENDS_DATA;
        this.sourceId = sourceId;
        this.destinationId = destinationId;
        this.payload = payload;
        this.trace = new ArrayList<>();
        Date date = new Date();
        this.timestamp = date.getTime();

    }

    public void addtoTrace(int nodeId) {
        this.trace.add(nodeId);
    }

    public List<Integer> getTrace() {
        return this.trace;
    }

    public int getDestinationId() {
        return destinationId;
    }

    public int getSourceId() {
        return sourceId;
    }

    public int getPayload() {
        return payload;
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

    @Override
    public List<RoutingEntry> getRoutingTable() {
        return null;
    }

    @Override
    public List<Integer> getNodeIdList() {
        return null;
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
        this.destinationId = din.readInt();
        this.sourceId = din.readInt();
        this.payload = din.readInt();
        this.trace = new ArrayList<>();
        int traceSize = din.readInt();

        for (int i = 1; i <= traceSize; i++) {
            this.trace.add(din.readInt());
        }
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
        dout.writeInt(this.destinationId);
        dout.writeInt(this.sourceId);
        dout.writeInt(this.payload);
        dout.writeInt(this.trace.size());

        for (Integer i : trace) {
            dout.writeInt(i);
        }
        dout.writeLong(this.timestamp);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }
}
