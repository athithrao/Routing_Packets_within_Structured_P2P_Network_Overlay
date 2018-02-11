package cs455.overlay.util;

import java.net.Socket;
import java.util.Scanner;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Node;
import cs455.overlay.node.Registry;
import cs455.overlay.wireformats.Protocol;

public class InteractiveCommandParser implements Runnable {

    private Scanner input = null;
    private Node node = null;
    private int programNumber = -1;
    private volatile boolean exitFlag = false;

    public InteractiveCommandParser(int program) {
        this.programNumber = program;
        this.input = new Scanner(System.in);

        if (this.programNumber == Protocol.REGISTRY)
            node = new Registry();

        if (this.programNumber == Protocol.MESSAGINGNODE)
            node = new MessagingNode();
    }

    void printHelp() {
        if (this.programNumber == Protocol.REGISTRY) {
            System.out.println("Registry Version 1.0");
            System.out.println("Following commands are provided to the registry.");
            System.out.println("1. list-messaging-nodes: This command provides the hostname, IP Address, " +
                    "port-number and the node-ID of the messaging nodes taking part in the simulation.");
            System.out.println("2. setup-overlay {number-of-routing-table-entries}: This command sets up the overlay.");
            System.out.println("3. list-routing-tables: This command lists information about the computed routing tables" +
                    " for each node in the overlay.");
            System.out.println("4. start {number-of-messages}: This command starts the simulation.");
            System.out.println("5. exit-registry: This command end the registry execution.");
        }
        if (this.programNumber == Protocol.MESSAGINGNODE) {
            System.out.println("Messaging Node Version 1.0");
            System.out.println("Following commands are provided to the messaging node.");
            System.out.println("1. print-counters-and-diagnostics: this prints the information about the number of " +
                    "messages that have been sent, received, and relayed along with the sums of the messages that been" +
                    " sent and received at the node.");
            System.out.println("2. exit-overlay: This command exits the overlay and end the program.");
        }
    }

    public void close() {
        exitFlag = true;
    }

    public void run() {
        String command;
        while (!exitFlag) {
            command = this.input.nextLine();
            if (command.equals("help")) {
                printHelp();
            } else if (command.equals("list-messaging-nodes")) {
                node.listMessagingNodes();
            } else if (command.equals("list-routing-tables")) {
                node.listRoutingTables();
            } else if (command.contains("setup-overlay")) {
                String info[] = command.split(" ");
                if (info.length == 2)
                    node.setupOverlay(Integer.parseInt(info[1]));
                else
                    System.out.println("Error! Command - setup-overlay {number-of-routing-table-entries}");
            } else if (command.contains("start")) {
                String info[] = command.split(" ");
                node.start(Integer.parseInt(info[1]));

            } else if (command.equals("exit-registry")) {
                exitFlag = true;
                node.exitRegistry();
            } else if (command.equals("print-counters-and-diagnostics")) {
                node.printCounterAndDiagnostics();
            } else if (command.equals("exit-overlay")) {
                exitFlag = true;
                node.exitOverlay();

            } else {
                System.out.println("Invalid command! Please type \"help\" for help. Please type \"exit\" to exit.");
            }
        }
    }

}
