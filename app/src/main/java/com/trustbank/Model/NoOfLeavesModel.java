package com.trustbank.Model;

import java.util.List;

public class NoOfLeavesModel {

    String noOfChequeBook;

    List<NoOfLeavesPerBook> noOfLeavesPerBooks;

    public String getNoOfChequeBook() {
        return noOfChequeBook;
    }

    public void setNoOfChequeBook(String noOfChequeBook) {
        this.noOfChequeBook = noOfChequeBook;
    }

    public List<NoOfLeavesPerBook> getNoOfLeavesPerBooks() {
        return noOfLeavesPerBooks;
    }

    public void setNoOfLeavesPerBooks(List<NoOfLeavesPerBook> noOfLeavesPerBooks) {
        this.noOfLeavesPerBooks = noOfLeavesPerBooks;
    }
}
