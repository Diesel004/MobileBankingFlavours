package com.trustbank.tcpconnection.tdto;

import com.trustbank.tcpconnection.tmessage.TMessage;
import com.trustbank.tcpconnection.tmessage.TMessageUtil;

public class MessageDtoBuilder {

    public TMessage GetGenerateRetriveMMIDDto(String LocalTxnDtTime, String Stan, String RemitterMobNo, String RemitterAccNo,
                                              String ValidationData, String RemitterName, String InstitutionID,
                                              String ChannelRefNo) {
        TMessage msg = new TMessage();
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "111001";
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        msg.LocalTxnDtTime.Value = LocalTxnDtTime;
        msg.Stan.Value = Stan;
        msg.RemitterMobNo.Value = RemitterMobNo;
        msg.RemitterAccNo.Value = RemitterAccNo;
        msg.ValidationData.Value = ValidationData;
        msg.RemitterName.Value = RemitterName;
        msg.InstitutionID.Value = InstitutionID;
        msg.ChannelRefNo.Value = ChannelRefNo;
        return msg;
    }

    public TMessage GetDeleteMMIDDto(String LocalTxnDtTime, String Stan, String RemitterMobNo, String RemitterAccNo,
                                     String ValidationData, String RemitterName, String InstitutionID,
                                     String ChannelRefNo) {
        TMessage msg = new TMessage();
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "111002";
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        msg.LocalTxnDtTime.Value = LocalTxnDtTime;
        msg.Stan.Value = Stan;
        msg.RemitterMobNo.Value = RemitterMobNo;
        msg.RemitterAccNo.Value = RemitterAccNo;
        msg.ValidationData.Value = ValidationData;
        msg.RemitterName.Value = RemitterName;
        msg.InstitutionID.Value = InstitutionID;
        msg.ChannelRefNo.Value = ChannelRefNo;
        return msg;
    }

    public TMessage GetBalanceInquiryDto(String LocalTxnDtTime, String Stan, String RemitterMobNo, String RemitterAccNo,
                                         String ValidationData, String RemitterName, String InstitutionID, String ChannelRefNo) {
        TMessage msg = new TMessage();
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "111011";
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        msg.LocalTxnDtTime.Value = LocalTxnDtTime;
        msg.Stan.Value = Stan;
        msg.RemitterMobNo.Value = RemitterMobNo;
        msg.RemitterAccNo.Value = RemitterAccNo;
        msg.RemitterName.Value = RemitterName;
        msg.InstitutionID.Value = InstitutionID;
        msg.ChannelRefNo.Value = ChannelRefNo;
        return msg;
    }

    public TMessage GetMiniStatementDto(String LocalTxnDtTime, String Stan, String RemitterMobNo, String RemitterAccNo,
                                        String ValidationData, String RemitterName, String InstitutionID, String ChannelRefNo) {
        TMessage msg = new TMessage();
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "111012";
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        msg.LocalTxnDtTime.Value = LocalTxnDtTime;
        msg.Stan.Value = Stan;
        msg.RemitterMobNo.Value = RemitterMobNo;
        msg.RemitterAccNo.Value = RemitterAccNo;
        msg.RemitterName.Value = RemitterName;
        msg.InstitutionID.Value = InstitutionID;
        msg.ChannelRefNo.Value = ChannelRefNo;
        return msg;
    }

    public TMessage GetImpsLastFiveTransactionDto(String LocalTxnDtTime, String Stan, String RemitterMobNo, String RemitterAccNo,
                                                  String InstitutionID, String ChannelRefNo) {
        TMessage msg = new TMessage();
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "111005";
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        msg.LocalTxnDtTime.Value = LocalTxnDtTime;
        msg.Stan.Value = Stan;
        msg.RemitterMobNo.Value = RemitterMobNo;
        msg.RemitterAccNo.Value = RemitterAccNo;
//        msg.RemitterName.Value = RemitterName;
        msg.InstitutionID.Value = InstitutionID;
        msg.ChannelRefNo.Value = ChannelRefNo;
        return msg;
    }

    public TMessage ShowMMIDDto(String LocalTxnDtTime, String Stan, String RemitterMobNo, String RemitterAccNo,
                                String ValidationData, String RemitterName, String InstitutionID) {
        TMessage msg = new TMessage();
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "111001";
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        msg.LocalTxnDtTime.Value = LocalTxnDtTime;
        msg.Stan.Value = Stan;
        msg.RemitterMobNo.Value = RemitterMobNo;
        msg.RemitterAccNo.Value = RemitterAccNo;
        msg.ValidationData.Value = ValidationData;
        msg.RemitterName.Value = RemitterName;
        msg.InstitutionID.Value = InstitutionID;
        return msg;
    }

    //P2P
    public TMessage GetImpsTransferToMobileDto(String LocalTxnDtTime, String Stan, String RemitterMobNo, String RemitterAccNo,
                                               String RemitterName, String BenfMobileNo, String BenfMMIDNo, String TranAmt,
                                               String remark, String InstitutionID, String ChannelRefNo,String BeneName) {
        TMessage msg = new TMessage();
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "111004";
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        msg.LocalTxnDtTime.Value = LocalTxnDtTime;
        msg.Stan.Value = Stan;
        msg.RemitterMobNo.Value = RemitterMobNo;
        msg.RemitterAccNo.Value = RemitterAccNo;
        msg.RemitterName.Value = RemitterName;
        msg.BeneMobileNo.Value = BenfMobileNo; //
        msg.BeneMMID.Value = BenfMMIDNo; //
        msg.TranAmount.Value = TranAmt; //
        msg.Remark.Value = remark; //
        msg.InstitutionID.Value = InstitutionID;
        msg.ChannelRefNo.Value = ChannelRefNo;
        msg.ChannelBeneName.Value = BeneName;
        return msg;
    }

    //P2A
    public TMessage GetImpsTransferToAccountNoDto(String LocalTxnDtTime, String Stan, String RemitterMobNo, String RemitterAccNo, String RemitterName,
                                                  String BeneAccNo, String BeneIFSC, String TransAmount, String Remark,
                                                  String InstitutionID, String BeneName, String ChannelRefNo) {
        TMessage msg = new TMessage();
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "111009";
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        msg.LocalTxnDtTime.Value = LocalTxnDtTime;
        msg.Stan.Value = Stan;
        msg.RemitterMobNo.Value = RemitterMobNo;
        msg.RemitterAccNo.Value = RemitterAccNo;
        msg.RemitterName.Value = RemitterName;
        msg.BeneAccNo.Value = BeneAccNo;//
        msg.BeneIFSC.Value = BeneIFSC;//
        msg.TranAmount.Value = TransAmount;//
        msg.Remark.Value = Remark;//
        msg.InstitutionID.Value = InstitutionID;  //for VMUCB IMPS TESTING
        msg.BeneName.Value = BeneName;
        msg.ChannelRefNo.Value = ChannelRefNo;
        msg.ChannelBeneName.Value = BeneName;
        msg.BeneMobileNo.Value = "";//
        return msg;
    }


    public TMessage GetImpsMpinDto(String LocalTxnDtTime, String Stan, String RemitterMobNo, String RemitterAccNo,
                                   String ValidationData, String RemitterName, String RemitterPin,
                                   String InstitutionID) {
        TMessage msg = new TMessage();
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "111006";
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        msg.LocalTxnDtTime.Value = LocalTxnDtTime;
        msg.Stan.Value = Stan;
        msg.RemitterMobNo.Value = RemitterMobNo;
        msg.RemitterAccNo.Value = RemitterAccNo;
        msg.ValidationData.Value = ValidationData;
        msg.RemitterName.Value = RemitterName;
        msg.RemiPIN.Value = RemitterPin;//
        msg.InstitutionID.Value = InstitutionID;
        return msg;
    }


    public TMessage VerifyP2PBeneficeryDto(String LocalTxnDtTime, String Stan, String RemitterMobNo, String RemitterAccNo,
                                           String RemitterName, String benMobileNo, String benMMID, String InstitutionID,
                                           String ChannelRefNo) {
        TMessage msg = new TMessage();
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "111013";
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        msg.LocalTxnDtTime.Value = LocalTxnDtTime;
        msg.Stan.Value = Stan;
        msg.RemitterMobNo.Value = RemitterMobNo;
        msg.RemitterAccNo.Value = RemitterAccNo;
        //   msg.ValidationData.Value = ValidationData;
        msg.RemitterName.Value = RemitterName;
        msg.BeneMobileNo.Value = benMobileNo;  //ben mobile no.
        msg.BeneMMID.Value = benMMID;  //ben mmid no.
        msg.Remark.Value = "";
        msg.InstitutionID.Value = InstitutionID;
        msg.ChannelRefNo.Value = ChannelRefNo;
        return msg;
//p2P
/*<MessageType>1200</MessageType>
<ProcCode>111013</ProcCode>
<OriginatingChannel>BRC</OriginatingChannel>
<LocalTxnDtTime>20210929123810</LocalTxnDtTime>
<Stan>313866</Stan>
<RemitterMobNo>9860987475</RemitterMobNo>
<RemitterAccNo>100210014003101</RemitterAccNo>
<RemitterName>PARESH BABULAL CHAUDHARI</RemitterName>
<BeneMobileNo>9745481176</BeneMobileNo>
<BeneMMID>8553479</BeneMMID>/BeneIFSC>
<Remark>Name Inquiry P2P</Remark>
<InstitutionID>541</InstitutionID>
<ChannelRefNo>XYZB4FDM96610F285683166</ChannelRefNo>*/

    }

    public TMessage VerifyP2ABeneficeryDto(String LocalTxnDtTime, String Stan, String RemitterMobNo, String RemitterAccNo,
                                           String RemitterName, String benAccountNumber, String ifscCode, String InstitutionID,
                                           String ChannelRefNo) {
        TMessage msg = new TMessage();
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "111013";
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        msg.LocalTxnDtTime.Value = LocalTxnDtTime;
        msg.Stan.Value = Stan;
        msg.RemitterMobNo.Value = RemitterMobNo;
        msg.RemitterAccNo.Value = RemitterAccNo;
        // msg.ValidationData.Value = ValidationData;
        msg.RemitterName.Value = RemitterName;
        msg.BeneAccNo.Value = benAccountNumber;
        msg.BeneIFSC.Value = ifscCode;
        msg.Remark.Value = "";
        msg.InstitutionID.Value = InstitutionID;
        msg.ChannelRefNo.Value = ChannelRefNo;
        //  msg.ChannelRefNo.Value = InstitutionID;
        return msg;
    }


    public TMessage CheckIMPSFTTransactionsDto(String LocalTxnDtTime, String Stan, String RemitterMobNo, String RemitterAccNo,
                                               String RemitterName, String benAccountNumber, String ifscCode, String InstitutionID,
                                               String ChannelRefNo, String rrnNo, String orignatingChannelRefNo) {
        TMessage msg = new TMessage();
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "111003";
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        msg.LocalTxnDtTime.Value = LocalTxnDtTime;
        msg.Stan.Value = Stan;
        msg.RemitterMobNo.Value = RemitterMobNo;
        msg.RemitterAccNo.Value = RemitterAccNo;
        // msg.ValidationData.Value = ValidationData;
        // msg.RemitterName.Value = RemitterName;
        //   msg.BeneAccNo.Value = benAccountNumber;
        //   msg.BeneIFSC.Value = ifscCode;
        //   msg.Remark.Value = "";
        msg.InstitutionID.Value = InstitutionID;

        msg.ChannelRefNo.Value = ChannelRefNo;
        msg.TransRefNo.Value = rrnNo;
        msg.OriginalChannelRefNo.Value = orignatingChannelRefNo;
        return msg;
    }


    public TMessage CheckUPIFTTransactionsDto(String LocalTxnDtTime, String Stan, String RemitterMobNo, String RemitterAccNo,
                                              String RemitterName, String benAccountNumber, String ifscCode, String InstitutionID,
                                              String ChannelRefNo, String rrnNo, String orignatingChannelRefNo) {
        TMessage msg = new TMessage();
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "222004";
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        msg.LocalTxnDtTime.Value = LocalTxnDtTime;
        msg.Stan.Value = Stan;
        msg.RemitterMobNo.Value = RemitterMobNo;
        msg.RemitterAccNo.Value = RemitterAccNo;
        // msg.ValidationData.Value = ValidationData;
        // msg.RemitterName.Value = RemitterName;
        //   msg.BeneAccNo.Value = benAccountNumber;
        //   msg.BeneIFSC.Value = ifscCode;
        //   msg.Remark.Value = "";
        msg.InstitutionID.Value = InstitutionID;

        msg.ChannelRefNo.Value = ChannelRefNo;
        msg.TransRefNo.Value = rrnNo;
        msg.OriginalChannelRefNo.Value = orignatingChannelRefNo;
        return msg;
    }

    public TMessage SearchBillRequest(String LocalTxnDtTime, String Stan, String ChannelRefNo, String BBPS_DATA) {
        TMessage msg = new TMessage();
        msg.BBPS_DATA.Value = BBPS_DATA;
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "333001";
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        // msg.LocalTxnDtTime.Value = LocalTxnDtTime;
        msg.Stan.Value = Stan;
        //  msg.InstitutionID.Value = InstitutionID;
        msg.ChannelRefNo.Value = ChannelRefNo;
        return msg;
    }

    //P2A
    public TMessage GetRTGSTransferToAccountNoDto(String LocalTxnDtTime, String Stan, String RemitterMobNo, String RemitterAccNo, String RemitterName,
                                                  String BeneAccNo, String BeneIFSC, String TransAmount, String Remark,
                                                  String InstitutionID, String BeneName, String ChannelRefNo) {
        TMessage msg = new TMessage();
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "222002";
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        msg.LocalTxnDtTime.Value = LocalTxnDtTime;
        msg.Stan.Value = Stan;
        msg.RemitterMobNo.Value = RemitterMobNo;
        msg.RemitterAccNo.Value = RemitterAccNo;
        msg.RemitterName.Value = RemitterName;
        msg.BeneAccNo.Value = BeneAccNo;//
        msg.BeneIFSC.Value = BeneIFSC;//
        msg.TranAmount.Value = TransAmount;//
        msg.Remark.Value = Remark;//
        msg.InstitutionID.Value = InstitutionID;  //for VMUCB IMPS TESTING
        msg.BeneName.Value = BeneName;
        msg.ChannelRefNo.Value = ChannelRefNo;
        msg.ChannelBeneName.Value = BeneName;
        msg.BeneMobileNo.Value = "";//
        return msg;
    }


    //P2A
    public TMessage GetUPITransferTransactionsDto(String LocalTxnDtTime, String Stan, String RemitterMobNo, String RemitterAccNo, String RemitterName,
                                                  String BeneAccNo, String BeneIFSC, String TransAmount, String Remark,
                                                  String InstitutionID, String BeneName, String ChannelRefNo, String upiId) {
        TMessage msg = new TMessage();
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "222001";  //for UPI Id.
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        msg.LocalTxnDtTime.Value = LocalTxnDtTime;
        msg.Stan.Value = Stan;
        msg.RemitterMobNo.Value = RemitterMobNo;
        msg.RemitterAccNo.Value = RemitterAccNo;
        msg.RemitterName.Value = RemitterName;
        msg.BeneAccNo.Value = "";//
        msg.BeneIFSC.Value = "";//
        msg.BeneMobileNo.Value = "";//
        msg.TranAmount.Value = TransAmount;//
        msg.Remark.Value = Remark;//
        msg.InstitutionID.Value = InstitutionID;  //for VMUCB IMPS TESTING
        msg.BeneName.Value = BeneName;
        msg.ChannelRefNo.Value = ChannelRefNo;
        msg.ChannelBeneName.Value = BeneName;
        msg.BENEUPIID.Value = upiId;

        return msg;
    }

    public TMessage CheckNEFTFTTransactionsDto(String LocalTxnDtTime, String Stan, String RemitterMobNo, String RemitterAccNo,
                                               String RemitterName, String benAccountNumber, String ifscCode, String InstitutionID,
                                               String ChannelRefNo, String rrnNo, String orignatingChannelRefNo) {
        TMessage msg = new TMessage();
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "222005";
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        msg.LocalTxnDtTime.Value = LocalTxnDtTime;
        msg.Stan.Value = Stan;
        msg.RemitterMobNo.Value = RemitterMobNo;
        msg.RemitterAccNo.Value = RemitterAccNo;
        // msg.ValidationData.Value = ValidationData;
        // msg.RemitterName.Value = RemitterName;
        //   msg.BeneAccNo.Value = benAccountNumber;
        //   msg.BeneIFSC.Value = ifscCode;
        //   msg.Remark.Value = "";
        msg.InstitutionID.Value = InstitutionID;

        msg.ChannelRefNo.Value = ChannelRefNo;
        msg.TransRefNo.Value = rrnNo;
        msg.OriginalChannelRefNo.Value = orignatingChannelRefNo;
        return msg;
    }

    public TMessage BillPaymentRequest(String LocalTxnDtTime, String Stan,
                                       String ChannelRefNo, String BBPS_DATA,
                                       String remmiterAcNo, String remmiterMobNo,
                                       String remmiterName, String amount, String remark, String billerid,
                                       String billerName, String billNo) {
        TMessage msg = new TMessage();
        msg.BBPS_DATA.Value = BBPS_DATA;
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "333002";
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        msg.LocalTxnDtTime.Value = LocalTxnDtTime;
        msg.Stan.Value = Stan;
        msg.InstitutionID.Value = "45";
        msg.ChannelRefNo.Value = ChannelRefNo;
        msg.RemitterMobNo.Value = remmiterMobNo;
        msg.RemitterAccNo.Value = remmiterAcNo;
        msg.RemitterName.Value = remmiterName;
        msg.BeneMobileNo.Value = "";
        msg.BeneName.Value = billerName;
        msg.BeneMMID.Value = "";
        msg.BeneIFSC.Value = "";
        msg.BeneAccNo.Value = "";
        msg.TranAmount.Value = amount;
        msg.Remark.Value = remark;
        msg.OriginalChannelRefNo.Value = "";
        msg.BBPS_BillerId.Value = billerid;
        msg.BBPS_BillNo.Value = billNo;

//        <BBPS_BillerId>CESU00000ODI01</BBPS_BillerId>
        return msg;
    }

    public TMessage CheckBillPayementStatusDto(String LocalTxnDtTime, String Stan, String RemitterMobNo, String RemitterAccNo,
                                               String RemitterName, String benAccountNumber, String ifscCode, String InstitutionID,
                                               String ChannelRefNo, String rrnNo, String orignatingChannelRefNo, String data) {
        TMessage msg = new TMessage();
        msg.BBPS_DATA.Value = data;
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "333003";
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        msg.LocalTxnDtTime.Value = LocalTxnDtTime;
        msg.Stan.Value = Stan;
        msg.RemitterMobNo.Value = RemitterMobNo;
        msg.RemitterAccNo.Value = RemitterAccNo;
        msg.RemitterName.Value = RemitterName;

        msg.InstitutionID.Value = InstitutionID;

        msg.ChannelRefNo.Value = ChannelRefNo;
        //  msg.TransRefNo.Value = rrnNo;
        msg.OriginalChannelRefNo.Value = orignatingChannelRefNo;
        return msg;
    }


    public TMessage RegsitserComplinatsDto(String LocalTxnDtTime, String Stan, String InstitutionID,
                                           String ChannelRefNo, String orignatingChannelRefNo, String data,
                                           String mobileNo, String billerId, String remiiterAcNo, String remmiterName, String amount,
                                           String remark) {
        TMessage msg = new TMessage();
        msg.BBPS_DATA.Value = data;
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "333004";
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        msg.LocalTxnDtTime.Value = LocalTxnDtTime;
        msg.Stan.Value = Stan;
        msg.RemitterMobNo.Value = mobileNo;
        msg.RemitterAccNo.Value = remiiterAcNo;
        msg.RemitterName.Value = remmiterName;
        msg.InstitutionID.Value = InstitutionID;
        msg.ChannelRefNo.Value = ChannelRefNo;
        //  msg.TransRefNo.Value = rrnNo;
//        msg.OriginalChannelRefNo.Value = "YB311012SNKCKJXMYC6N";
        msg.OriginalChannelRefNo.Value = orignatingChannelRefNo;
        msg.BBPS_BillerId.Value = billerId;
        msg.TranAmount.Value = amount;
        msg.Remark.Value = remark;
        return msg;
    }


    public TMessage CheckComplaintsStatusDto(String LocalTxnDtTime, String Stan, String InstitutionID,
                                             String ChannelRefNo, String orignatingChannelRefNo, String data,
                                             String mobileNo, String billerId, String remiiterAcNo, String remmiterName, String amount,
                                             String remark) {
        TMessage msg = new TMessage();
        msg.BBPS_DATA.Value = data;
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "333005";
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        msg.LocalTxnDtTime.Value = LocalTxnDtTime;
        msg.Stan.Value = Stan;
        msg.RemitterMobNo.Value = mobileNo;
        msg.RemitterAccNo.Value = remiiterAcNo;
        msg.RemitterName.Value = remmiterName;
        msg.InstitutionID.Value = InstitutionID;
        msg.ChannelRefNo.Value = ChannelRefNo;
        //  msg.TransRefNo.Value = rrnNo;
//        msg.OriginalChannelRefNo.Value = "YB311012SNKCKJXMYC6N";
        msg.OriginalChannelRefNo.Value = orignatingChannelRefNo;
        msg.BBPS_BillerId.Value = billerId;
        msg.TranAmount.Value = amount;
        msg.Remark.Value = remark;
        return msg;
    }


    public TMessage ShowHistroyComplaintsRegDto(String LocalTxnDtTime, String Stan, String InstitutionID,
                                                String ChannelRefNo, String orignatingChannelRefNo, String data,
                                                String mobileNo, String billerId, String remiiterAcNo, String remmiterName, String amount,
                                                String remark) {
        TMessage msg = new TMessage();
        msg.BBPS_DATA.Value = data;
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "333006";
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        msg.LocalTxnDtTime.Value = LocalTxnDtTime;
        msg.Stan.Value = Stan;
        msg.RemitterMobNo.Value = mobileNo;
        msg.RemitterAccNo.Value = remiiterAcNo;
        msg.RemitterName.Value = remmiterName;
        msg.InstitutionID.Value = InstitutionID;
        msg.ChannelRefNo.Value = ChannelRefNo;
        //  msg.TransRefNo.Value = rrnNo;
        msg.OriginalChannelRefNo.Value = orignatingChannelRefNo;
        msg.BBPS_BillerId.Value = billerId;
        msg.TranAmount.Value = amount;
        msg.Remark.Value = remark;
        return msg;
    }

    public TMessage upiCollectMoneyRequest(String amount, String ChannelRefNo, String mobile,String benAccNo) {
        TMessage msg = new TMessage();
        msg.MessageType.Value = "1200";
        msg.ProcCode.Value = "400001";
        msg.OriginatingChannel.Value = TMessageUtil.MSG_ORIGINATING_CHANNEL;
        // msg.LocalTxnDtTime.Value = LocalTxnDtTime;
      //  msg.Stan.Value = Stan;
        //  msg.InstitutionID.Value = InstitutionID;
        msg.BeneAccNo.Value = benAccNo;;
        msg.BeneMobileNo.Value = mobile;;
        msg.TranAmount.Value = amount;
        msg.ChannelRefNo.Value = ChannelRefNo;
        return msg;
    }
}