package tcp;

import java.io.Serializable;

import javax.swing.ImageIcon;

import org.json.simple.JSONObject;

@SuppressWarnings("serial")
public class TCPPayload implements Serializable
{
	private int phase;
	private int type;
	private int size;
	private String apPayload;
	private JSONObject jobj;
	private ImageIcon img;
	private byte[] byteImg;
	
	private boolean str;
	private boolean json;
	private boolean image;
	
	private boolean token;
	
	private int idToken;
	private int imageHash;
	private int jsonHash;


	public TCPPayload() {
		super();
	}
	
	public TCPPayload(int phase, int type, byte[] img) {
		super();
		this.phase = phase;
		this.type = type;
		this.byteImg = img;
		this.image = false;
		this.str = false;
		this.json = false;
		this.token = false;
		this.idToken = 0;
		this.jsonHash = 0;
		this.imageHash = 0;
	}

	public TCPPayload(int phase, int type, ImageIcon img) {
		super();
		this.phase = phase;
		this.type = type;
		this.img = img;
		this.setImage(true);
		this.str = false;
		this.json = false;
		this.token = false;
		this.idToken = 0;
		this.jsonHash = 0;
		this.imageHash = 0;
	}

	public TCPPayload(int phase, int type, JSONObject jobj) {
		super();
		this.phase = phase;
		this.type = type;
		this.jobj = jobj;
		this.setImage(false);
		this.str = false;
		this.json = true;
		this.token = false;
		this.idToken = 0;
		this.jsonHash = 0;
		this.imageHash = 0;
	}
	

	public TCPPayload(int p, int t, int s, String ap)
    {
    	this.phase = p;
    	this.type = t;
    	this.size = s;
    	this.apPayload = ap;
		this.setImage(false);
		this.str = true;
		this.json = false;
		this.token = false;
		this.idToken = 0;
		this.jsonHash = 0;
		this.imageHash = 0;
    }
    

    /** Get a String representation of this class. */
    public String toString()
    {
    	if(str) {
    		return apPayload;
    	}
    	else if(json) {
    		return jobj.toString();
    	}
    	else if(image){
    		return img.toString();
    	}
    		return "Something went wrong.";

    	
    }


	public int getPhase() {
		return phase;
	}


	public void setPhase(int phase) {
		this.phase = phase;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public int getSize() {
		return size;
	}


	public void setSize(int size) {
		this.size = size;
	}


	public String getApPayload() {
		return apPayload;
	}


	public void setApPayload(String apPayload) {
		this.apPayload = apPayload;
	}

	public JSONObject getJobj() {
		return jobj;
	}

	public void setJobj(JSONObject jobj) {
		this.jobj = jobj;
	}

	public ImageIcon getImg() {
		return img;
	}

	public void setImg(ImageIcon img) {
		this.img = img;
	}

	public boolean isImage() {
		return image;
	}

	public void setImage(boolean image) {
		this.image = image;
	}

//	public int getHashVal() {
//		return hashVal;
//	}
//
//	public void setHashVal(int hashVal) {
//		this.hashVal = hashVal;
//	}

	public boolean isToken() {
		return token;
	}

	public void setToken(boolean token) {
		this.token = token;
	}

	public int getImageHash() {
		return imageHash;
	}

	public void setImageHash(int imageHash) {
		this.imageHash = imageHash;
	}

	public int getJsonHash() {
		return jsonHash;
	}

	public void setJsonHash(int jsonHash) {
		this.jsonHash = jsonHash;
	}
	
    public int getIdToken() {
		return idToken;
	}

	public void setIdToken(int idToken) {
		this.idToken = idToken;
	}

	public byte[] getByteImg() {
		return byteImg;
	}

	public void setByteImg(byte[] byteImg) {
		this.byteImg = byteImg;
	}
}
