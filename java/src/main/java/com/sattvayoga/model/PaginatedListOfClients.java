package com.sattvayoga.model;

import java.util.List;

public class PaginatedListOfClients {

    private List<ClientDetails> listOfClients;
    private int totalRows;

    public PaginatedListOfClients() {
    }

    public List<ClientDetails> getListOfClients() {
        return listOfClients;
    }

    public void setListOfClients(List<ClientDetails> listOfClients) {
        this.listOfClients = listOfClients;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }
}
