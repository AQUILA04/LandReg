package com.lesadrax.registrationclient.data.model;


public class SynchroData {
    private int synchroCandidateCount;

    private int totalOfflineCount;

    private String batchNumber;

    private int totalReceivedCount;

    private int successPacketCount;

    private int failedPacketCount;

    private int duplicatedPacketCount;

    private int pendingPacketCount;

    private String packetsNumber;

    private boolean isFinished;

    private String operatorAgent;

    private String synchroStatus;

    // Ajoutez des getters et setters pour tous les champs

    public SynchroData(int synchroCandidateCount, int totalOfflineCount) {
        this.synchroCandidateCount = synchroCandidateCount;
        this.totalOfflineCount = totalOfflineCount;
    }

    public SynchroData() {
    }

    public int getSynchroCandidateCount() {
        return synchroCandidateCount;
    }

    public void setSynchroCandidateCount(int synchroCandidateCount) {
        this.synchroCandidateCount = synchroCandidateCount;
    }

    public int getTotalOfflineCount() {
        return totalOfflineCount;
    }

    public void setTotalOfflineCount(int totalOfflineCount) {
        this.totalOfflineCount = totalOfflineCount;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public int getTotalReceivedCount() {
        return totalReceivedCount;
    }

    public void setTotalReceivedCount(int totalReceivedCount) {
        this.totalReceivedCount = totalReceivedCount;
    }

    public int getSuccessPacketCount() {
        return successPacketCount;
    }

    public void setSuccessPacketCount(int successPacketCount) {
        this.successPacketCount = successPacketCount;
    }

    public int getFailedPacketCount() {
        return failedPacketCount;
    }

    public void setFailedPacketCount(int failedPacketCount) {
        this.failedPacketCount = failedPacketCount;
    }

    public int getDuplicatedPacketCount() {
        return duplicatedPacketCount;
    }

    public void setDuplicatedPacketCount(int duplicatedPacketCount) {
        this.duplicatedPacketCount = duplicatedPacketCount;
    }

    public int getPendingPacketCount() {
        return pendingPacketCount;
    }

    public void setPendingPacketCount(int pendingPacketCount) {
        this.pendingPacketCount = pendingPacketCount;
    }

    public String getPacketsNumber() {
        return packetsNumber;
    }

    public void setPacketsNumber(String packetsNumber) {
        this.packetsNumber = packetsNumber;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public String getOperatorAgent() {
        return operatorAgent;
    }

    public void setOperatorAgent(String operatorAgent) {
        this.operatorAgent = operatorAgent;
    }

    public String getSynchroStatus() {
        return synchroStatus;
    }

    public void setSynchroStatus(String synchroStatus) {
        this.synchroStatus = synchroStatus;
    }


    @Override
    public String toString() {
        return "SynchroData{" +
                "synchroCandidateCount=" + synchroCandidateCount +
                ", totalOfflineCount=" + totalOfflineCount +
                ", batchNumber='" + batchNumber + '\'' +
                ", totalReceivedCount=" + totalReceivedCount +
                ", successPacketCount=" + successPacketCount +
                ", failedPacketCount=" + failedPacketCount +
                ", duplicatedPacketCount=" + duplicatedPacketCount +
                ", pendingPacketCount=" + pendingPacketCount +
                ", packetsNumber='" + packetsNumber + '\'' +
                ", isFinished=" + isFinished +
                ", operatorAgent='" + operatorAgent + '\'' +
                ", synchroStatus='" + synchroStatus + '\'' +
                '}';
    }
}
