package com.example.nfcproject.DataRep;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Journey implements Comparable{

	private String journeyName;
	private int journeyID;
	private Capsule capsule;

	private String currentMeditation;
	private double latitude;
	private double longitude;

	public Journey(String jName, int jId, Capsule caps){
		this.capsule = caps;
		this.journeyName = jName;
		this.journeyID = jId;
		this.currentMeditation = this.capsule.getMessage().toString();
		this.latitude = this.capsule.getCoords()[0];
		this.longitude = this.capsule.getCoords()[1];
	}

	public Journey nextShrine(SecretKey key) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
		return new Journey(getJourneyName(), getJourneyID(), capsule.innerCapsule(key));
	}

	public int getJourneyID(){
		return this.journeyID;
	}

	public String getJourneyName(){
		return this.journeyName;
	}
	public String getCurrentMeditation(){
		return this.currentMeditation;
	}
	public double getShrineLatitude(){
		return this.latitude;
	}
	public double getGetShrineLongitude(){
		return this.longitude;
	}

	@Override
	public int compareTo(Object o) {
		if (this.getClass() != o.getClass()) throw new IllegalArgumentException();
		if (o == null) throw new NullPointerException();
		Journey j = (Journey) o;
		return this.getJourneyID()-j.getJourneyID();
	}
}