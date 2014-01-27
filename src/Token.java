import com.google.gson.Gson;


public class Token {

	private final int tID;
	private final TokenValue tValue;
	private final TokenInfo _tInfo;
	private static Gson _G;
	
	public Token(int id, TokenValue value){
		this.tID = id;
		this.tValue = value;
		
		this._tInfo = new TokenInfo(id, value);
	}
	
	public String getTokenInfo(){
		return _G.toJson(_tInfo);
	}
	
	
	// Valeurs admises des tokens
	enum TokenValue {
		
		t001(0.01),
		t002(0.02),
		t005(0.05),
		t010(0.1),
		t020(0.2),
		t050(0.5),
		t1(1),
		t2(2),
		t5(5),
		t10(10),
		t20(20),
		t50(50),
		t100(100),
		t200(200),
		t500(500);
		
		private double tValue;
		
		TokenValue(double value){
			this.tValue = value;
		}
		
		public double getTValue(){
			return tValue;
		}
		
		
	}
	
	class TokenInfo{
		private int _id;
		private TokenValue _value;
		
		public TokenInfo(int id, TokenValue value){
			_id = id;
			_value = value;
		}
		
		public int getID(){
			return _id;
		}
		
		public double getValue(){
			return _value.getTValue();
		}
		
		
	}
}

