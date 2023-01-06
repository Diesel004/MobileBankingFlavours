
package com.trustbank.tcpconnection.tmessage;


import java.util.ArrayList;
import java.util.List;

public class TMessageFieldRecord extends TMessageField {

    public List<TMessageFieldRecordItem> RecordList;
    public TMessageFieldRecord(String tagName){
        super(tagName);
        this.RecordList = new ArrayList<TMessageFieldRecordItem>();
    }


}