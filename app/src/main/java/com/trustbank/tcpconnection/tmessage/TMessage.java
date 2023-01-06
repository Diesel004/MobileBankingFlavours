package com.trustbank.tcpconnection.tmessage;

import com.trustbank.tcpconnection.util.ResponseEntity;
import com.trustbank.util.TrustMethods;

import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TMessage {
    public TMessageField MessageType = new TMessageField("MessageType");
    public TMessageField ProcCode = new TMessageField("ProcCode");
    public TMessageField OriginatingChannel = new TMessageField("OriginatingChannel");
    public TMessageField ChannelRefNo = new TMessageField("ChannelRefNo");
    public TMessageField Stan = new TMessageField("Stan");
    public TMessageField LocalTxnDtTime = new TMessageField("LocalTxnDtTime");
    public TMessageField InstitutionID = new TMessageField("InstitutionID");
    public TMessageField RemitterMobNo = new TMessageField("RemitterMobNo");
    public TMessageField RemitterAccNo = new TMessageField("RemitterAccNo");
    public TMessageField RemitterName = new TMessageField("RemitterName");
    public TMessageField ValidationData = new TMessageField("ValidationData");
    public TMessageField BeneNBIN = new TMessageField("BeneNBIN");
    public TMessageField BeneMAS = new TMessageField("BeneMAS");
    public TMessageField BeneAccNo = new TMessageField("BeneAccNo");
    public TMessageField BeneIFSC = new TMessageField("BeneIFSC");
    public TMessageField BeneAadharNo = new TMessageField("BeneAadharNo");
    public TMessageField TranAmount = new TMessageField("TranAmount");
    public TMessageField RemiPIN = new TMessageField("RemiPIN");
    public TMessageField Remark = new TMessageField("Remark");
    public TMessageField ActCode = new TMessageField("ActCode");
    public TMessageField ActCodeDesc = new TMessageField("ActCodeDesc");
    public TMessageField TransRefNo = new TMessageField("TransRefNo");
    public TMessageField TrandateTime = new TMessageField("TrandateTime");
    public TMessageField BeneName = new TMessageField("BeneName");
    public TMessageFieldRecord Record = new TMessageFieldRecord("Record");
    public TMessageField TranType = new TMessageField("TranType");
    public TMessageField ImpsMessage = new TMessageField("ImpsMessage");

    public TMessageField ChannelBeneName = new TMessageField("ChannelBeneName");

    public TMessageField OriginalChannelRefNo = new TMessageField("OriginalChannelRefNo");
    //
    public TMessageField Message = new TMessageField("Message");
    public TMessageField Mmid = new TMessageField("Mmid");
    public TMessageField IFSCCode = new TMessageField("IFSCCode");
    public TMessageField BeneMobileNo = new TMessageField("BeneMobileNo");
    public TMessageField BeneMMID = new TMessageField("BeneMMID");

    public TMessageField BENEUPIID = new TMessageField("BeneUpiId");
    public TMessageField BBPS_DATA = new TMessageField("BBPS_DATA");
    public TMessageField BBPS_BillerId = new TMessageField("BBPS_BillerId");
    public TMessageField BBPS_BillNo = new TMessageField("BBPS_BillNo");

    public TMessageField BBPS_RESPONSE_DATA = new TMessageField("BBPS_RESPONSE_DATA");

    //

    public TMessage() {
    }


    public String GetXml() throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        //add elements to Document
        Element rootElement = doc.createElement("XML");
        doc.appendChild(rootElement);
        //
        this.MessageType.GetXml(rootElement);
        this.ProcCode.GetXml(rootElement);
        this.OriginatingChannel.GetXml(rootElement);
        this.LocalTxnDtTime.GetXml(rootElement);
        this.Stan.GetXml(rootElement);
        this.RemitterMobNo.GetXml(rootElement);
        this.RemitterAccNo.GetXml(rootElement);
        this.RemitterName.GetXml(rootElement);
        this.BeneAccNo.GetXml(rootElement);
        this.BeneIFSC.GetXml(rootElement);
        this.TranAmount.GetXml(rootElement);
        this.Remark.GetXml(rootElement);
        this.InstitutionID.GetXml(rootElement);
        this.BeneNBIN.GetXml(rootElement);
        this.BeneMAS.GetXml(rootElement);
        this.BeneAadharNo.GetXml(rootElement);
        this.ValidationData.GetXml(rootElement);
        this.RemiPIN.GetXml(rootElement);
        this.ActCode.GetXml(rootElement);
        this.ActCodeDesc.GetXml(rootElement);
        this.TransRefNo.GetXml(rootElement);
        this.TrandateTime.GetXml(rootElement);
        this.BeneName.GetXml(rootElement);
        //this.Record.GetXml(rootElement);
        this.Message.GetXml(rootElement);
        this.Mmid.GetXml(rootElement);
        this.IFSCCode.GetXml(rootElement);
        this.BeneMobileNo.GetXml(rootElement);
        this.BeneMMID.GetXml(rootElement);

        this.ChannelRefNo.GetXml(rootElement);
        this.OriginalChannelRefNo.GetXml(rootElement);
        this.ChannelBeneName.GetXml(rootElement);

        this.BBPS_DATA.GetXml(rootElement);
        this.BENEUPIID.GetXml(rootElement);
        this.BBPS_BillerId.GetXml(rootElement);
        this.BBPS_BillNo.GetXml(rootElement);
        this.BBPS_RESPONSE_DATA.GetXml(rootElement);

        return convertDocumentToString(doc);
    }


    public static ResponseEntity ParseMessage(String xmlString)
    //        throws SAXException, ParserConfigurationException, IOException
    {
        TMessage msg = new TMessage();
        try {
            Document doc = TMessage.ParseXmlFromString(xmlString);
            doc.getDocumentElement().normalize();

            TMessage.BuildProp(doc, msg.MessageType);
            TMessage.BuildProp(doc, msg.ProcCode);
            TMessage.BuildProp(doc, msg.OriginatingChannel);
            TMessage.BuildProp(doc, msg.LocalTxnDtTime);
            TMessage.BuildProp(doc, msg.Stan);
            TMessage.BuildProp(doc, msg.RemitterMobNo);
            TMessage.BuildProp(doc, msg.RemitterAccNo);
            TMessage.BuildProp(doc, msg.RemitterName);
            TMessage.BuildProp(doc, msg.BeneAccNo);
            TMessage.BuildProp(doc, msg.BeneIFSC);
            TMessage.BuildProp(doc, msg.TranAmount);
            TMessage.BuildProp(doc, msg.Remark);
            TMessage.BuildProp(doc, msg.InstitutionID);
            TMessage.BuildProp(doc, msg.BeneNBIN);
            TMessage.BuildProp(doc, msg.BeneMAS);
            TMessage.BuildProp(doc, msg.BeneAadharNo);
            TMessage.BuildProp(doc, msg.ValidationData);
            TMessage.BuildProp(doc, msg.RemiPIN);
            TMessage.BuildProp(doc, msg.ActCode);
            TMessage.BuildProp(doc, msg.ActCodeDesc);
            TMessage.BuildProp(doc, msg.TransRefNo);
            TMessage.BuildProp(doc, msg.TrandateTime);
            TMessage.BuildProp(doc, msg.BeneName);

            TMessage.BuildProp(doc, msg.Message);
            TMessage.BuildProp(doc, msg.Mmid);
            TMessage.BuildProp(doc, msg.IFSCCode);
            TMessage.BuildProp(doc, msg.BeneMobileNo);
            TMessage.BuildProp(doc, msg.BeneMMID);

            TMessage.BuildPropRecord(doc, msg.Record);

            TMessage.BuildProp(doc, msg.TranType);
            TMessage.BuildProp(doc, msg.OriginalChannelRefNo);
            TMessage.BuildProp(doc, msg.TrandateTime);
            TMessage.BuildProp(doc, msg.ImpsMessage);
            TMessage.BuildProp(doc, msg.ChannelBeneName);
            TMessage.BuildProp(doc, msg.BBPS_DATA);
            TMessage.BuildProp(doc, msg.BENEUPIID);
            TMessage.BuildProp(doc, msg.BBPS_BillerId);
            TMessage.BuildProp(doc, msg.BBPS_BillNo);
            TMessage.BuildProp(doc, msg.BBPS_RESPONSE_DATA);
            //
            return ResponseEntity.CreateSuccess(msg);

        } catch (SAXException | ParserConfigurationException | IOException e1) {
            return ResponseEntity.CreateError("9999", e1.getMessage(), e1);
        }
    }

    public static void BuildProp(Document doc, TMessageField field) {
        NodeList nl = doc.getElementsByTagName(field.TagName);
        if (nl.getLength() > 0) {
            //TrustMethods.systemMessage("-------------------------");
            //TrustMethods.systemMessage(nl.item(0).getNodeName());
            //TrustMethods.systemMessage(nl.item(0).getTextContent());
            field.Value = nl.item(0).getTextContent();
        }
    }

    public static void BuildPropRecord(Document doc, TMessageFieldRecord field) {
        NodeList nl = doc.getElementsByTagName(field.TagName);
        for (int i = 0; i < nl.getLength(); i++) {
            Node record = nl.item(i);
            Element element = (Element) record;

            //element.getElementsByTagName("TransRefNo").item(0).getTextContent()
            TMessageFieldRecordItem item = new TMessageFieldRecordItem();

            item.TranType = GetElementValueIfExists(element, "TranType");
            item.TranIndicator = GetElementValueIfExists(element, "TranIndicator");
            item.TransRefNo = GetElementValueIfExists(element, "TransRefNo");
            item.TranDateTime = GetElementValueIfExists(element, "TranDateTime");
            item.TranAmount = GetElementValueIfExists(element, "TranAmount");
            item.BeneName = GetElementValueIfExists(element, "BeneName");
            item.Status = GetElementValueIfExists(element, "Status");
            field.RecordList.add(item);

        }
        TrustMethods.systemMessage("BuildPropRecord - size : " + field.RecordList.size());
    }

    private static String GetElementValueIfExists(Element element, String elementNameToFind) {
        NodeList nlResult = element.getElementsByTagName(elementNameToFind);
        if (nlResult.getLength() > 0) {
            return nlResult.item(0).getTextContent();
        }
        return null;
    }

    public static Document ParseXmlFromString(String xmlString)
            throws SAXException, ParserConfigurationException, IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes());
            Document document = builder.parse(inputStream);
            return document;
        } catch (SAXException | ParserConfigurationException | IOException e1) {
            throw e1;
        }
    }

    private static String convertDocumentToString(Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            // below code to remove XML declaration
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString();
            return output;
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return null;
    }
}