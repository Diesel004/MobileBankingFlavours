
package com.trustbank.tcpconnection.tmessage;


import org.w3c.dom.Element;

public class TMessageField{
    public String TagName;
    public String TagDesc;   
    public String Value;   
    public TMessageField(String tagName) {
        this.TagName = tagName;
    }

    public void GetXml( Element element) {
        if (this.Value != null) {
            Element node =  element.getOwnerDocument().createElement(this.TagName);
            node.appendChild(element.getOwnerDocument().createTextNode(this.Value));
            element.appendChild(node);
        }

    }
}