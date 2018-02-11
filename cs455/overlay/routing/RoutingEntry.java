package cs455.overlay.routing;

import java.util.Arrays;
import java.util.Objects;

public class RoutingEntry {

    private int distance;
    private int nodeID;
    private byte[] ipAddress;
    private int listenPortNumber;

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getNodeID() {
        return nodeID;
    }

    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }

    public byte[] getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(byte[] ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getListenPortNumber() {
        return listenPortNumber;
    }

    public void setListenPortNumber(int listenPortNumber) {
        this.listenPortNumber = listenPortNumber;
    }

    public RoutingEntry(int distance, int nodeID, byte[] ipAddress, int listenPortNumber) {
        this.distance = distance;
        this.nodeID = nodeID;
        this.ipAddress = ipAddress;
        this.listenPortNumber = listenPortNumber;
    }

    public String getSocketKey() {
        return this.ipToString() + ":" + listenPortNumber;
    }

    public String ipToString() {
        int i = 4;
        String ipAddress = "";
        for (byte raw : this.ipAddress) {
            ipAddress += (raw & 0xFF);
            if (--i > 0) {
                ipAddress += ".";
            }
        }
        return ipAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoutingEntry that = (RoutingEntry) o;
        return distance == that.distance &&
                nodeID == that.nodeID &&
                listenPortNumber == that.listenPortNumber &&
                Arrays.equals(ipAddress, that.ipAddress);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(distance, nodeID, listenPortNumber);
        result = 31 * result + Arrays.hashCode(ipAddress);
        return result;
    }

    @Override
    public String toString() {
        return distance+"\t\t"+nodeID+"\t\t"+this.ipToString()+"\t\t"+listenPortNumber+"\n";
    }
}
