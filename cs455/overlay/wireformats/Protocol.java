package cs455.overlay.wireformats;

public class Protocol {

    public static final int MAX_NODES = 128;

    public static final int START_PORT_NO = 2000;

    public static final int DEBUG = 1;

    public static final int OVERLAY_NODE_SENDS_REGISTRATION = 2;
    public static final int REGISTRY_REPORTS_REGISTRATION_STATUS = 3;
    public static final int OVERLAY_NODE_SENDS_DEREGISTRATION = 4;
    public static final int REGISTRY_REPORTS_DEREGISTRATION_STATUS = 5;
    public static final int REGISTRY_SENDS_NODE_MAINFEST = 6;
    public static final int NODE_REPORTS_OVERLAY_SETUP_STATUS = 7;
    public static final int REGISTRY_REQUESTS_TASK_INITIATE = 8;
    public static final int OVERLAY_NODE_SENDS_DATA = 9;
    public static final int OVERLAY_NODE_REPORTS_TASK_FINISHED = 10;
    public static final int REGISTRY_REQUESTS_TRAFFIC_SUMMARY = 11;
    public static final int OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY = 12;

    public static final int REGISTRY = 0;
    public static final int MESSAGINGNODE = 1;

    //public enum programCommands {NULL, LIST_MESSGAGING_NODES, SETUP_OVERLAY, LIST_ROUTING_TABLES, START, PRINT_COUNTERS_AND_DIAGNOSTICS, EXIT_OVERLAY, EXIT_REGISTRY};
}
