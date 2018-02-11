package cs455.overlay.node;

import cs455.overlay.transport.TCPNode;
import cs455.overlay.wireformats.Event;

public interface Node {

    void onEvent(Event e);
    void listMessagingNodes();
    void setupOverlay(int entries);
    void listRoutingTables();
    void start(int messages);
    void printCounterAndDiagnostics();
    void exitRegistry();
    void exitOverlay();
    TCPNode getNodeDetails(int key);
    void setNodeDetails(int key, TCPNode node);
}

