package cs455.overlay.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/*
This class is used in the registry to collect all the data sent by the messaging nodes, calculate the
total packets sent, received, relayed and the total payload received and sent.
This class would then help print all the data as a table on the output console.
 */

public class StatisticsDisplay {

    private static HashMap<Integer, StatisticsCollector> statisticsCollectorHashMap = null;

    private int sumPacketsSent;
    private int sumPacketsReceived;
    private int sumPacketsRelayed;
    private long sumValuesSent;
    private long sumValuesReceived;

    public StatisticsDisplay() {
        statisticsCollectorHashMap = new HashMap<>();
        this.sumPacketsSent = 0;
        this.sumPacketsReceived = 0;
        this.sumPacketsRelayed = 0;
        this.sumValuesSent = 0;
        this.sumValuesReceived = 0;
    }

    public synchronized void addToStatisticsMap(int nodeId, StatisticsCollector collector) {
        if (!statisticsCollectorHashMap.containsKey(nodeId)) {
            statisticsCollectorHashMap.put(nodeId, collector);
        }
    }

    public synchronized void clearStatisticsMap() {
        if (!statisticsCollectorHashMap.isEmpty())
            statisticsCollectorHashMap.clear();
        this.sumPacketsSent = 0;
        this.sumPacketsReceived = 0;
        this.sumPacketsRelayed = 0;
        this.sumValuesSent = 0;
        this.sumValuesReceived = 0;
    }

    public void calculateValues() {
        List<Integer> keys = new ArrayList<>(statisticsCollectorHashMap.keySet());

        for (Integer n : keys) {
            StatisticsCollector collector = statisticsCollectorHashMap.get(n);
            sumPacketsSent += collector.getTotalPacketsSent();
            sumPacketsReceived += collector.getTotalPacketsReceived();
            sumPacketsRelayed += collector.getTotalPacketsRelayed();
            sumValuesSent += collector.getSumPacketDataSent();
            sumValuesReceived += collector.getSumPacketDataReceived();
        }

    }

    public void displayTable() {
        List<Integer> keys = new ArrayList<>(statisticsCollectorHashMap.keySet());
        Collections.sort(keys);
        System.out.println("Node-Id\t\tPackets Sent\t\tPackets Received\t\tPackets Relayed\t\tSum Values Sent\t\tSum Values Received");
        for (Integer n : keys) {
            StatisticsCollector collector = statisticsCollectorHashMap.get(n);
            System.out.println(n + "\t\t" + collector.getTotalPacketsSent() + "\t\t\t" + collector.getTotalPacketsReceived() + "\t\t\t\t" + collector.getTotalPacketsRelayed() + "\t\t\t" + collector.getSumPacketDataSent() + "\t\t\t" + collector.getSumPacketDataReceived());
        }
        System.out.println("Sum:\t\t" + this.sumPacketsSent + "\t\t\t" + this.sumPacketsReceived + "\t\t\t\t" + this.sumPacketsRelayed + "\t\t\t" + this.sumValuesSent + "\t\t\t" + this.sumValuesReceived);
    }

    public int size() {
        return statisticsCollectorHashMap.size();
    }

    public void testPrint() {
        List<Integer> keys = new ArrayList<>(statisticsCollectorHashMap.keySet());
        Collections.sort(keys);

        System.out.println(keys);

        for (Integer n : keys) {
            StatisticsCollector collector = statisticsCollectorHashMap.get(n);
            collector.printStatistics();
        }
    }
}
