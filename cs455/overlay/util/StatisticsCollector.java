package cs455.overlay.util;

/*
This class is used by the messaging
*/
public class StatisticsCollector {

    private int totalPacketsSent;
    private int totalPacketsReceived;
    private int totalPacketsRelayed;
    private long sumPacketDataSent;
    private long sumPacketDataReceived;

    public StatisticsCollector() {

        this.totalPacketsSent = 0;
        this.totalPacketsReceived = 0;
        this.totalPacketsRelayed = 0;
        this.sumPacketDataSent = 0;
        this.sumPacketDataReceived = 0;
    }

    public StatisticsCollector(int totalPacketsSent, int totalPacketsReceived, int totalPacketsRelayed, long sumPacketDataSent, long sumPacketDataReceived) {
        this.totalPacketsSent = totalPacketsSent;
        this.totalPacketsReceived = totalPacketsReceived;
        this.totalPacketsRelayed = totalPacketsRelayed;
        this.sumPacketDataSent = sumPacketDataSent;
        this.sumPacketDataReceived = sumPacketDataReceived;
    }


    public synchronized int getTotalPacketsSent() {
        return totalPacketsSent;
    }

    public synchronized int getTotalPacketsReceived() {
        return totalPacketsReceived;
    }

    public synchronized int getTotalPacketsRelayed() {
        return totalPacketsRelayed;
    }

    public synchronized long getSumPacketDataSent() {
        return sumPacketDataSent;
    }

    public synchronized long getSumPacketDataReceived() {
        return sumPacketDataReceived;
    }


    public synchronized void incrementTotalPacketsSent() {
        totalPacketsSent++;
    }

    public synchronized void incrementTotalPacketsReceived() {
        totalPacketsReceived++;
    }

    public synchronized void incrementTotalPacketsRelayed() {
        totalPacketsRelayed++;
    }

    public synchronized void updateSumPacketDataSent(int payload) {
        sumPacketDataSent += payload;
    }

    public synchronized void updateSumPacketDataReceived(int payload) {
        sumPacketDataReceived += payload;
    }

    public synchronized void printStatistics() {
        System.out.println("Total Packets Sent - " + totalPacketsSent);
        System.out.println("Total Packets Received - " + totalPacketsReceived);
        System.out.println("Total Packets Relayed - " + totalPacketsRelayed);
        System.out.println("Sum of Packets Data Sent - " + sumPacketDataSent);
        System.out.println("Sum of Packets Data Received - " + sumPacketDataReceived);
    }

    public synchronized void resetStatistics() {
        this.totalPacketsSent = 0;
        this.totalPacketsReceived = 0;
        this.totalPacketsRelayed = 0;
        this.sumPacketDataSent = 0;
        this.sumPacketDataReceived = 0;
    }
}
