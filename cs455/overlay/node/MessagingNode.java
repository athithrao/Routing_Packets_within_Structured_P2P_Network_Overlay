package cs455.overlay.node;

import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPNode;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.util.StatisticsCollector;
import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;

public class MessagingNode implements Node {


    private static TCPConnection connection = null;
    private static RoutingTable routingTable = null;
    private static volatile StatisticsCollector collector = null;
    private static int nodeID;
    //private static HashMap<Integer, TCPNode> NodeMap = null;
    private InteractiveCommandParser parser = null;
    private Thread parserThread = null;
    private EventFactory eventFactory = null;

    public MessagingNode() {

    }

    public MessagingNode(String registryIP, int registryPort) {

        nodeID = -1;
        InetAddress ip = null;
        byte[] ipbytes = null;
        try {
            ip = InetAddress.getByName(registryIP);
            ipbytes = ip.getAddress();
        } catch (UnknownHostException e) {
            System.out.println("Error in MessagingNode.MessagingNode(String,int)");
            e.printStackTrace();
        }

        connection = new TCPConnection(ipbytes, registryPort);
        collector = new StatisticsCollector();

    }

    public static void main(String[] args) {

        System.out.println("Welcome to Messaging Node 1.0.");

        if (args.length == 0 || args.length == 1 || args.length > 2) {
            System.out.println("Syntax to run registry - java cs455.overlay.node.MessagingNode registry-host registry-port");
        } else {
            MessagingNode messagingNode = new MessagingNode(args[0], Integer.parseInt(args[1]));
            messagingNode.initialize();
        }
    }

    public void initialize() {

        eventFactory = EventFactory.getInstance();
        eventFactory.setNodeInstance(this);

        parser = new InteractiveCommandParser(Protocol.MESSAGINGNODE);
        parserThread = new Thread(parser, "parser");
        parserThread.start();

        try {
            connection.initialize();
            //connect to registry and receive the message
            int localPort = connection.openRegistryConnection();
            OverlayNodeSendsRegistration register = new OverlayNodeSendsRegistration(connection.getMyIPAddress(), connection.getMyListenPortNumber(), localPort);
            connection.sendToRegistry(register.getBytes());
        } catch (IOException e) {
            System.out.println("Error in MessagingNode.initialize() - Registry connection refused. Please check if the registry program is running.");
        }


    }

    public void close() throws IOException {
        connection.closeMessgagingNodeConnections();

        if (parser != null)
            parser.close();
        connection.close();

    }

    @Override
    public void listMessagingNodes() {
        System.out.println("This is not a valid command for a Messaging Node. Please type 'help' for the list of commands. ");
    }

    @Override
    public void setupOverlay(int entries) {
        System.out.println("This is not a valid command for a Messaging Node. Please type 'help' for the list of commands. ");
    }

    @Override
    public void listRoutingTables() {
        System.out.println("This is not a valid command for a Messaging Node. Please type 'help' for the list of commands. ");
    }

    @Override
    public void start(int messages) {
        System.out.println("This is not a valid command for a Messaging Node. Please type 'help' for the list of commands. ");
    }

    @Override
    public void printCounterAndDiagnostics() {

        //synchronized (collector)
        {
            collector.printStatistics();
        }

    }

    @Override
    public void exitRegistry() {
        System.out.println("This is not a valid command for a Messaging Node. Please type 'help' for the list of commands. ");
    }

    @Override
    public void exitOverlay() {

        try {
            int localPort = connection.getRegistrySocketLocalPortNumber();
            OverlayNodeSendsDeregistration deregister = new OverlayNodeSendsDeregistration(connection.getMyIPAddress(), connection.getMyListenPortNumber(), nodeID, localPort);
            connection.sendToRegistry(deregister.getBytes());
        } catch (IOException io) {
            System.out.println("Error in Messagingnode:exitOverlay()");
            io.printStackTrace();
        }
    }

    @Override
    public void onEvent(Event e) {

        try {
            int messageType = e.getMessageType();

            if (messageType == Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS) {
                System.out.println(e.getInformation());
                if (e.getStatus() != -1)
                    nodeID = e.getStatus();
                else {
                    System.out.println("Messaging Node execution is terminated. Press Ctrl + C to exit.");
                    close();
                }

            } else if (messageType == Protocol.REGISTRY_SENDS_NODE_MAINFEST) {
                String info = "";
                int status = -1;
                try {
                    routingTable = null;
                    routingTable = new RoutingTable(nodeID);
                    routingTable.setNodesList(e.getNodeIdList());
                    routingTable.setRoutingTable(e.getRoutingTable());
                    for (RoutingEntry r : routingTable.getRoutingTable()) {
                        //I have simplified this step as the Node to Node is uni-directional.
                        TCPNode n = new TCPNode(r.getIpAddress(), r.getListenPortNumber());
                        connection.openNodeConnection(n);
                    }
                    info = "Routing table Update at Node with ID - " + nodeID + " is received and updated.";
                    status = nodeID;
                } catch (Exception ex) {
                    info = "Routing table update at Node with ID - " + nodeID + " failed.";
                    status = -1;
                }
                NodeReportOverlaySetupStatus report = new NodeReportOverlaySetupStatus(status, info);
                connection.sendToRegistry(report.getBytes());
            } else if (messageType == Protocol.REGISTRY_REQUESTS_TASK_INITIATE) {
                System.out.println("Starting the simulation with " + e.getStatus() + " rounds.");
                collector.resetStatistics();
                startSimulation(e.getStatus());
                OverlayNodeReportsTaskFinished finished = new OverlayNodeReportsTaskFinished(connection.getMyIPAddress(), connection.getMyListenPortNumber(), nodeID);
                connection.sendToRegistry(finished.getBytes());

            } else if (messageType == Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY) {
                System.out.println("Sending message statistics to Registry.");
                OverlayNodeReportsTrafficSummary summary = new OverlayNodeReportsTrafficSummary(nodeID, collector);
                connection.sendToRegistry(summary.getBytes());
            } else if (messageType == Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS) {
                System.out.println(e.getInformation());
                if (e.getStatus() != -1)
                    close();
            } else if (messageType == Protocol.OVERLAY_NODE_SENDS_DATA) {
                if (e.getDestinationId() == nodeID) {
                    //synchronized (collector)
                    {
                        collector.incrementTotalPacketsReceived();
                        collector.updateSumPacketDataReceived(e.getPayload());
                    }
                } else {
                    OverlayNodeSendsData data = new OverlayNodeSendsData(e.getSourceId(), e.getDestinationId(), e.getPayload());
                    if (e.getTrace().size() == 0) {
                        data.addtoTrace(nodeID);
                    } else {
                        List<Integer> trace = e.getTrace();
                        for (Integer i : trace) {
                            data.addtoTrace(i);
                        }
                        data.addtoTrace(nodeID);
                    }
                    connection.sendToNode(routingTable.getNextHop(data.getDestinationId()), data.getBytes());
                    //synchronized (collector)
                    {
                        collector.incrementTotalPacketsRelayed();
                    }
                }
            }
        } catch (IOException io) {
            System.out.println("Error in MessagingNode.OnEvent()");
            io.printStackTrace();
        }

    }

    @Override
    public TCPNode getNodeDetails(int key) {
        return null;
    }

    @Override
    public void setNodeDetails(int key, TCPNode node) {
    }

    private void startSimulation(int rounds) {
        int nextSink = -1;
        Random r = new Random();
        OverlayNodeSendsData data = null;
        try {
            for (int i = 1; i <= rounds; i++) {
                nextSink = routingTable.chooseRandomSinkNodeId();
                data = new OverlayNodeSendsData(nodeID, nextSink, r.nextInt());
                //synchronized (connection)
                {
                    connection.sendToNode(routingTable.getNextHop(nextSink), data.getBytes());
                }
                //synchronized (collector)
                {
                    collector.incrementTotalPacketsSent();
                    collector.updateSumPacketDataSent(data.getPayload());
                }
                //TimeUnit.MILLISECONDS.sleep(10);
            }
        } catch (IOException io) {
            System.out.println("Error in startSimulation()");
            io.printStackTrace();
        }
//        catch (InterruptedException ie)
//        {
//            System.out.println("Error in startSimulation()");
//            ie.printStackTrace();
//        }
    }
}
