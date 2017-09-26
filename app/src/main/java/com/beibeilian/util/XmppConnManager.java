package com.beibeilian.util;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;

public class XmppConnManager {

    public static int    SERVER_PORT = 5222;//����˿� ������openfire������
    public static String SERVER_HOST = "112.74.63.86";//��openfire���������ڵ�ip
    public static  String SERVER_NAME = "wpy";//����openfireʱ�ķ�������
    private static XMPPConnection connection = null;
    private static XmppConnManager connManager = null;

    public static  XmppConnManager getInstance(){
        if(connManager == null){
            connManager = new XmppConnManager();
        }
        return connManager;
    }

    private  void openConnection() {
        try {
            if (null == connection || !connection.isAuthenticated()) {
                //XMPPConnection.DEBUG_ENABLED = false;//����DEBUGģʽ
                //��������
            /*  ConnectionConfiguration config = new ConnectionConfiguration(
                        SERVER_HOST, SERVER_PORT,
                        SERVER_NAME);*/
                ConnectionConfiguration config = new ConnectionConfiguration(
                        SERVER_HOST, SERVER_PORT);
                config.setDebuggerEnabled(true);
                config.setSendPresence(false); //�����Ƿ����������Ϣ
                config.setCompressionEnabled(false); // disable for now
                config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);//���Ĵ���
                config.setReconnectionAllowed(true);//�����������

//                config.setReconnectionAllowed(true);
//                config.setTLSEnabled(false);
//                config.setCompressionEnabled(false);
//                config.setSelfSignedCertificateEnabled(false);
               config.setSASLAuthenticationEnabled(false);
//                config.setVerifyChainEnabled(false);

//                config.setSendPresence(false);
////                config.setSASLAuthenticationEnabled(false);
//              config.setSASLAuthenticationEnabled(false);
//                config.setSecurityMode(SecurityMode.disabled);
//                config.setCompressionEnabled(false);
                connection = new XMPPConnection(config);
                connection.connect();//���ӵ�������
                //���ø���Provider
                configureConnection(ProviderManager.getInstance());
            }
        } catch (XMPPException xe) {
            xe.printStackTrace();
        }
    }

    /**
     * ��������
     */ 
    public  XMPPConnection getConnection() {
        if (connection == null) {
            openConnection();
        }
        return connection;
    }

    /**
     * �ر�����
     */ 
    public  void closeConnection() {
    	try
    	{
        if(connection!=null){
            connection.disconnect();
            connection = null;  
        }
    	}
    	catch(Exception e)
    	{
    		
    	}
    }


    /**
     * xmpp����
     */
    private  void configureConnection(ProviderManager pm) {
        // Private Data Storage
        pm.addIQProvider("query", "jabber:iq:private",new PrivateDataManager.PrivateDataIQProvider());
        // Time
        try {
            pm.addIQProvider("query", "jabber:iq:time",Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Roster Exchange
        pm.addExtensionProvider("x", "jabberroster",new RosterExchangeProvider());
        // Message Events
        pm.addExtensionProvider("x", "jabberevent",new MessageEventProvider());
        // Chat State
        pm.addExtensionProvider("active","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());
        pm.addExtensionProvider("composing","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());
        pm.addExtensionProvider("paused","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());
        pm.addExtensionProvider("inactive","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());
        pm.addExtensionProvider("gone","http://jabber.org/protocol/chatstates",new ChatStateExtension.Provider());
        // XHTML
        pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",new XHTMLExtensionProvider());
        // Group Chat Invitations
        pm.addExtensionProvider("x", "jabberconference",new GroupChatInvitation.Provider());
        // Service Discovery # Items //���������б�
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",new DiscoverItemsProvider());
        // Service Discovery # Info //ĳһ���������Ϣ
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",new DiscoverInfoProvider());
        // Data Forms
        pm.addExtensionProvider("x", "jabberdata", new DataFormProvider());
        // MUC User
        pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",new MUCUserProvider());
        // MUC Admin
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",new MUCAdminProvider());
        // MUC Owner
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",new MUCOwnerProvider());
        // Delayed Delivery
        pm.addExtensionProvider("x", "jabberdelay",new DelayInformationProvider());
        // Version
        try {
            pm.addIQProvider("query", "jabber:iq:version",Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            // Not sure what's happening here.
        }
        // VCard
        pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
        // Offline Message Requests
        pm.addIQProvider("offline", "http://jabber.org/protocol/offline",new OfflineMessageRequest.Provider());
        // Offline Message Indicator
        pm.addExtensionProvider("offline","http://jabber.org/protocol/offline",new OfflineMessageInfo.Provider());
        // Last Activity
        pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
        // User Search
        pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
        // SharedGroupsInfo
        pm.addIQProvider("sharedgroup","http://www.jivesoftware.org/protocol/sharedgroup",new SharedGroupsInfo.Provider());
        // JEP-33: Extended Stanza Addressing
        pm.addExtensionProvider("addresses","http://jabber.org/protocol/address",new MultipleAddressesProvider());
        pm.addIQProvider("si", "http://jabber.org/protocol/si",new StreamInitiationProvider());
        pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
        pm.addIQProvider("command", "http://jabber.org/protocol/commands",new AdHocCommandDataProvider());
        pm.addExtensionProvider("malformed-action","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.MalformedActionError());
        pm.addExtensionProvider("bad-locale","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.BadLocaleError());
        pm.addExtensionProvider("bad-payload","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.BadPayloadError());
        pm.addExtensionProvider("bad-sessionid","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.BadSessionIDError());
        pm.addExtensionProvider("session-expired","http://jabber.org/protocol/commands",new AdHocCommandDataProvider.SessionExpiredError());
    }
    
    public MultiUserChat joinChatRoom(String roomName,  String nickName) {  
        try {
            // ʹ��XMPPConnection����һ��MultiUserChat����  
//            MultiUserChat muc = new MultiUserChat(roomName + "@conference.wpy");
        	MultiUserChat muc=new MultiUserChat(XmppConnManager.getInstance().getConnection(), roomName + "@conference.wpy");
            // �����ҷ��񽫻����Ҫ���ܵ���ʷ��¼����  
            DiscussionHistory history = new DiscussionHistory();  
            history.setMaxChars(0);  
            // history.setSince(new Date());  
            // �û�����������  
            muc.join(nickName);  
//            muc.leave();
            return muc;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }
    
    
	/**
	 * Description: ����Ⱥ��������Ϣ
	 * @param chat
	 * @param chatContent
	 * @return
	 */
	public static boolean sendMultiChat(MultiUserChat multiUserChat, String chatContent){
		try {
			multiUserChat.sendMessage(chatContent);
			return true;
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
    
    
    
}