package cs455.overlay.routing;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Node;
import cs455.overlay.node.Registry;
import cs455.overlay.transport.TCPNode;
import cs455.overlay.wireformats.Protocol;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RoutingTable {

    private List<Integer> nodesList = null;

    private List<RoutingEntry> routingTable = null;

    private int routingTableSize = -1;

    private int currentNodeId = -1;

    private Node node = null;

    public RoutingTable(List<Integer> list, int size) {
        this.nodesList = list;
        this.routingTableSize = size;
        Collections.sort(nodesList);
        node = new Registry();
    }

    public RoutingTable(int currentNodeId) {
        nodesList = new ArrayList<>();
        routingTable = new ArrayList<>();
        node = new MessagingNode();
        this.currentNodeId = currentNodeId;
    }

    public List<RoutingEntry> createRoutingTable(int nodeId) {
        int startIndex = nodesList.indexOf(nodeId);
        int distance = 1;
        List<RoutingEntry> table = new ArrayList<>();
        for (int i = 1; i <= routingTableSize; i++) {
            int id = ((startIndex + distance) % (nodesList.size()));
            if (nodeId != nodesList.get(id)) {
                TCPNode n = node.getNodeDetails(nodesList.get(id));
                RoutingEntry entry = new RoutingEntry(distance, nodesList.get(id), n.getIpAddress(), n.getListenPortNumber());
                table.add(entry);
                distance *= 2;
            } else {
                System.out.println("Issue with the node connecting to itself.");
            }
        }
        return table;
    }

    public List<Integer> getNodesList() {
        return nodesList;
    }

    public void setNodesList(List<Integer> nodesList) {
        this.nodesList = nodesList;
    }

    public List<RoutingEntry> getRoutingTable() {
        return routingTable;
    }

    public void setRoutingTable(List<RoutingEntry> routingTable) {
        this.routingTable = routingTable;
    }

    public int chooseRandomSinkNodeId() {
        int totalNodes = nodesList.size();
        Random r = new Random();
        int id = 0;
        do {
            id = nodesList.get(r.nextInt((totalNodes - 1 - 0) + 1) + 0);
        }
        while (id == currentNodeId);
        return id;
    }

    public String getNextHop(int sinkNodeId) {

        RoutingEntry finalRoutingEntry;
        //First check if the sinks in the routing table
        for (RoutingEntry r : routingTable) {
            if (r.getNodeID() == sinkNodeId) {
                return r.ipToString() + ":" + r.getListenPortNumber();
            }
        }
        //then iterating the Routing table from the start of the List
        finalRoutingEntry = routingTable.get(0);

        for (int i = routingTable.size() - 1; i >= 0; i--) {
            RoutingEntry current = routingTable.get(i);
            if (sinkNodeId > current.getNodeID() && current.getNodeID() > finalRoutingEntry.getNodeID()) {
                finalRoutingEntry = current;
                break;
            }
        }
        return finalRoutingEntry.ipToString() + ":" + finalRoutingEntry.getListenPortNumber();
    }
}
