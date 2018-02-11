package cs455.overlay.node;


import cs455.overlay.routing.RoutingEntry;
import cs455.overlay.routing.RoutingTable;
import cs455.overlay.transport.TCPConnection;
import cs455.overlay.transport.TCPNode;
import cs455.overlay.util.InteractiveCommandParser;
import cs455.overlay.util.StatisticsCollector;
import cs455.overlay.util.StatisticsDisplay;
import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Registry implements Node {

    private static TCPConnection connection = null;
    private static HashMap<Integer, TCPNode> registeredNodeMap = null;
    private static HashMap<Integer, List<RoutingEntry>> routingTableMap = null;
    private static boolean simulationLock = false;
    private static boolean printLock = false;
    private static boolean finishLock = false;
    private static RoutingTable registryRoutingTable = null;
    private static int finishCounter = 0;
    private static StatisticsDisplay statisticsDisplay = null;

    private InteractiveCommandParser parser = null;
    private Thread parserThread = null;
    private EventFactory eventFactory = null;

    //private static volatile

    public Registry() {

    }

    public Registry(int port) {
        connection = new TCPConnection(port);
    }


    public void initialize() {

        registeredNodeMap = new HashMap<Integer, TCPNode>();
        statisticsDisplay = new StatisticsDisplay();
        eventFactory = EventFactory.getInstance();
        eventFactory.setNodeInstance(this);

        parser = new InteractiveCommandParser(Protocol.REGISTRY);
        parserThread = new Thread(parser, "InteractiveCommandParser");
        parserThread.start();

        connection.initialize();
    }

    @Override
    public void listMessagingNodes() {
        Iterator<Integer> keySetIterator = registeredNodeMap.keySet().iterator();
        System.out.println("NodeId\t\tIP Address\t\tPort Number");
        while (keySetIterator.hasNext()) {
            int key = keySetIterator.next();
            TCPNode n = registeredNodeMap.get(key);
            System.out.println(key + "\t\t" + n.ipToString() + "\t\t" + n.getListenPortNumber());
        }
    }

    @Override
    public void setupOverlay(int entries) {

        List<Integer> keys = new ArrayList<>(registeredNodeMap.keySet());

        tearDownOverlay();

        if ((keys.size()) > Math.pow(2, (entries - 1)) && !simulationLock) {
            simulationLock = true;

            registryRoutingTable = new RoutingTable(keys, entries);

            routingTableMap = new HashMap<>();

            try {
                for (Integer id : keys) {
                    List<RoutingEntry> table = registryRoutingTable.createRoutingTable(id);
                    routingTableMap.put(id, table);

                    RegistrySendsNodeManifest manifest = new RegistrySendsNodeManifest(table, registryRoutingTable.getNodesList());
                    connection.sendToNode(getNodeDetails(id).getSocketKey(), manifest.getBytes());
                }
            } catch (IOException e) {
                System.out.println("Error in Registry.setupOverlay()");
                e.printStackTrace();
            }
        } else {
            System.out.println("Number of nodes n > routing table entries c + 1.");
        }
        if (simulationLock) {
            System.out.println("Registration request failed. Simulation in progress and simulation lock is acquired. Please exit and restart simulation to setup-overlay again.");
        }

    }

    public void tearDownOverlay() {
        registryRoutingTable = null;
        routingTableMap = null;
    }

    @Override
    public void listRoutingTables() {

        Iterator<Integer> keySetIterator = routingTableMap.keySet().iterator();

        while (keySetIterator.hasNext()) {
            int key = keySetIterator.next();
            System.out.println("For Node ID - " + key);
            System.out.println("Distance\tNodeId\t\tIP Address\t\tPort Number");
            for (RoutingEntry r : routingTableMap.get(key)) {
                System.out.println(r.toString());
            }
        }
    }

    @Override
    public void start(int messages) {

        List<Integer> keys = new ArrayList<>(registeredNodeMap.keySet());
        finishCounter = 0;
        statisticsDisplay.clearStatisticsMap();
        printLock = false;
        finishLock = false;
        try {
            for (Integer id : keys) {
                RegistryRequestsTaskInitiate initiate = new RegistryRequestsTaskInitiate(messages);
                connection.sendToNode(getNodeDetails(id).getSocketKey(), initiate.getBytes());
            }
        } catch (IOException io) {
            System.out.println("Error in Registry.start()");
            io.printStackTrace();
        }
    }

    @Override
    public void printCounterAndDiagnostics() {
        System.out.println("This is not a valid command for a Messaging Node. Please type 'help' for the list of commands. ");
    }

    @Override
    public void exitRegistry() {

        try {
            close();
        } catch (IOException io) {
            System.out.println("Error in Registry:exitRegistry()");
        }

        System.out.println("Registry exit successful. Goodbye!");
    }

    @Override
    public void exitOverlay() {
        System.out.println("This is not a valid command for a Messaging Node. Please type 'help' for the list of commands. ");
    }

    @Override
    public void onEvent(Event e) {

        int messageType = e.getMessageType();

        try {
            if (messageType == Protocol.OVERLAY_NODE_SENDS_REGISTRATION) {
                TCPNode node = new TCPNode(e.getIPAddress(), e.getListenPortNumber(), e.getLocalPortNumber());
                int status = -1;
                String info;
                if (!isAlreadyRegistered(node)) {
                    if (connection.validateSocketAddress(node.getSocketKey())) {
                        if (!simulationLock) {
                            status = createNodeId();
                            info = "Registration request successful with nodeId - " + status + ". The number of messaging nodes currently constituting the overlay is (" + (registeredNodeMap.size() + 1) + ")";
                        } else {
                            info = "Registration request failed. Simulation in progress and simulation lock is acquired. Please exit and restart simulation to setup-overlay again.";
                        }
                    } else {
                        info = "Registration request failed. The IP address in the message did not match the connection socket IP address.";
                    }
                } else {
                    info = "Registration request failed. The node is already registered in the registry.";
                }
                RegistryReportsRegistrationStatus report = new RegistryReportsRegistrationStatus(status, info);
                if (connection.sendToNode(node.getSocketKey(), report.getBytes())) {
                    if (status != -1) {
                        setNodeDetails(status, node);
                    } else {
                        connection.removeFromCache(node.getSocketKey());
                    }

                } else {
                    System.out.println("Registry could not connect to the node. Removing Entry from connection cache.");
                    connection.removeFromCache(node.getSocketKey());
                }
            } else if (messageType == Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS) {
                System.out.println(e.getInformation());
            } else if (messageType == Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED) {
                synchronized (this)
                {
                    finishCounter++;
                    System.out.println("Node - " + e.getNodeId() + " has completed sending all the messages.");
                }

                if (finishCounter == registeredNodeMap.size() && !finishLock) {
                    finishLock = false;
                    System.out.println("All simulations have completed. Requesting Traffic Summary from all nodes after 15 seconds of wait.");
                    TimeUnit.SECONDS.sleep(15);
                    List<Integer> keys = new ArrayList<>(registeredNodeMap.keySet());
                    for (Integer id : keys) {
                        RegistryRequestsTrafficSummary summary = new RegistryRequestsTrafficSummary();
                        connection.sendToNode(getNodeDetails(id).getSocketKey(), summary.getBytes());
                    }

                }

            } else if (messageType == Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY) {

                System.out.println("Node - " + e.getNodeId() + " sent its message statistics.");
                StatisticsCollector collector = new StatisticsCollector(e.getTotalPacketsSent(), e.getTotalPacketsReceived(), e.getTotalPacketsRelayed(), e.getSumPacketDataSent(), e.getSumPacketDataReceived());
                statisticsDisplay.addToStatisticsMap(e.getNodeId(), collector);


                if (statisticsDisplay.size() == registeredNodeMap.size() && !printLock) {
                    printLock = true;
                    statisticsDisplay.calculateValues();
                    statisticsDisplay.displayTable();
                }

            } else if (messageType == Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION) {
                TCPNode node = new TCPNode(e.getIPAddress(), e.getListenPortNumber(), e.getLocalPortNumber());
                int status = -1;
                String info;
                if (isAlreadyRegistered(node)) {
                    if (connection.validateSocketAddress(node.getSocketKey())) {
                        if (simulationLock) {
                            info = "De-registration request failed. Simulation in progress and simulation lock is acquired.";
                        } else {
                            registeredNodeMap.remove(e.getNodeId());
                            status = e.getNodeId();
                            info = "De-registration request successful. The number of messaging nodes currently constituting the overlay is (" + (registeredNodeMap.size()) + ")";
                        }
                    } else {
                        info = "De-registration request failed. The IP address in the message did not match the connection socket IP address.";
                    }
                } else {
                    info = "De-registration request failed. The node is not registered in the registry.";
                }
                RegistryReportsDeregistrationStatus report = new RegistryReportsDeregistrationStatus(status, info);
                connection.sendToNode(node.getSocketKey(), report.getBytes());
                connection.removeFromCache(node.getSocketKey());
            }
        } catch (IOException io) {
            System.out.println("Error in Registry.OnEvent()");
            io.printStackTrace();
        } catch (InterruptedException ie) {
            System.out.println("Error in Registry.OnEvent()");
            ie.printStackTrace();
        }

    }

    private int createNodeId() {
        Random r = new Random();
        int id = -1;
        do {
            id = r.nextInt((Protocol.MAX_NODES - 1 - 0) + 1) + 0;
        }
        while (registeredNodeMap.containsKey(id));
        return id;
    }

    private boolean isAlreadyRegistered(TCPNode node) {
        Iterator<Integer> keySetIterator = registeredNodeMap.keySet().iterator();

        while (keySetIterator.hasNext()) {
            int key = keySetIterator.next();
            TCPNode n = registeredNodeMap.get(key);
            if (n.equals(node))
                return true;
        }
        return false;
    }

    public void close() throws IOException {
        if (parser != null)
            parser.close();
        connection.close();
    }

    @Override
    public TCPNode getNodeDetails(int key) {
        return registeredNodeMap.get(key);
    }

    @Override
    public void setNodeDetails(int key, TCPNode node) {
        if (!registeredNodeMap.containsKey(key))
            registeredNodeMap.put(key, node);
    }

    public static void main(String[] args) {
        System.out.println("Welcome to Registry 1.0");

        if (args.length == 0 || args.length > 1) {
            System.out.println("Syntax to run registry - java cs455.overlay.node.Registry portnum");
        } else {
            Registry registry = new Registry(Integer.parseInt(args[0]));
            registry.initialize();
        }
    }
}
