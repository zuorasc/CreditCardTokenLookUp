
/* Class: CreditCardTokenLookUp
 * Author: Mike Sullivan <mike.sullivan@zuora.com>
 * 
 * 
 * 	A simple program to do a look up between Zuora Accounts and Credit Cards
 *  using a token as the key.
 *  
 *  The program builds two HashMaps, token -> zuoraAcctID and token -> CreditCard#
 * 
 */

package com.zuora.msullivan;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;


import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class CreditCardTokenLookUp {

	private static CsvReader zAcctReader;
	private static CsvReader creditCardDataReader;

	private static CsvWriter outputWriter;
	
	private static String outputFile = "zAcct2Card-out" + System.currentTimeMillis() + ".csv";
	
	private static HashMap<String, String> acctLookUp;
	private static HashMap<String, CreditCardData> cardLookUp;
	
	private static int tokenColumn;
	private static int cardNumColumn;
	private static int expirationYearColumn;
	private static int expirationMonthColumn;
	private static int creditCardHolderNameColumn;
	private static int creditCardAddress1Column;
	private static int creditCardAddress2Column;
	private static int creditCardCountryColumn;
	private static int creditCardStateColumn;
	private static int creditCardCityColumn;
	private static int creditCardPostalCodeColumn;
	
	/*
	 * Inner class to hold Credit Card details
	 */
	
	private static class CreditCardData {
			
		// Card Number
		private String cardNumer;
					
		// Card Type
		private String cardType;
					
		// Expiration year
		private String cardExprYear;
					 
		// Expiration month
		private String cardExprMonth;
					
		// Card Holder Name
		private String cardHolderName;
					
		// Address 1
		private String cardAddr1;
					
		// Address 2
		private String cardAddr2;
					
		// Country
		private String cardCountry;
					
		// State
		private String cardState;
					
		// City
		private String cardCity;
					
		// Zip
		private String cardZip; 
		
		CreditCardData(String number, String type, String exprYear, String exprMonth, String name, String addr1, String addr2, String country, String state, String city, String zip) {
			setCardNumer(number);
			setCardType(type);
			setCardExprYear(exprYear);
			setCardExprMonth(exprMonth);
			setCardHolderName(name);
			setCardAddr1(addr1);
			setCardAddr2(addr2);
			setCardCountry(country);
			setCardState(state);
			setCardCity(city);
			setCardZip(zip);
		}

		/**
		 * @return the cardNumer
		 */
		public String getCardNumer() {
			return cardNumer;
		}

		/**
		 * @param cardNumer the cardNumer to set
		 */
		public void setCardNumer(String cardNumer) {
			this.cardNumer = cardNumer;
		}

		
		/**
		 * @return the cardType
		 */
		public String getCardType() {
			return cardType;
		}

		/**
		 * @param cardType the cardType to set
		 */
		public void setCardType(String cardType) {
			if (cardType != null) {
				this.cardType = cardType;
			} else { this.cardType = "ERROR"; }
		}

		/**
		 * @return the cardExprYear
		 */
		public String getCardExprYear() {
			return cardExprYear;
		}

		/**
		 * @param cardExprYear the cardExprYear to set
		 */
		public void setCardExprYear(String cardExprYear) {
			if (cardExprYear.length() > 4) {
				this.cardExprYear = cardExprYear.substring(0, 4);
			} else { this.cardExprYear = "00"; }
			
		}

		/**
		 * @return the cardExprMonth
		 */
		public String getCardExprMonth() {
			return cardExprMonth;
		}

		/**
		 * @param cardExprMonth the cardExprMonth to set
		 */
		public void setCardExprMonth(String cardExprMonth) {
			if (cardExprMonth != null && cardExprMonth.length() == 6) {
				this.cardExprMonth = cardExprMonth.substring(4, 6);
			} else { this.cardExprMonth = "00"; }
		}


		/**
		 * @return the cardHolderName
		 */
		public String getCardHolderName() {
			return cardHolderName;
		}

		/**
		 * @param cardHolderName the cardHolderName to set
		 */
		public void setCardHolderName(String cardHolderName) {
			this.cardHolderName = cardHolderName;
		}

		/**
		 * @return the cardAddr1
		 */
		public String getCardAddr1() {
			return cardAddr1;
		}

		/**
		 * @param cardAddr1 the cardAddr1 to set
		 */
		public void setCardAddr1(String cardAddr1) {
			this.cardAddr1 = cardAddr1;
		}

		/**
		 * @return the cardAddr2
		 */
		public String getCardAddr2() {
			return cardAddr2;
		}

		/**
		 * @param cardAddr2 the cardAddr2 to set
		 */
		public void setCardAddr2(String cardAddr2) {
			this.cardAddr2 = cardAddr2;
		}

		/**
		 * @return the cardCountry
		 */
		public String getCardCountry() {
			return cardCountry;
		}

		/**
		 * @param cardCountry the cardCountry to set
		 */
		public void setCardCountry(String cardCountry) {
			this.cardCountry = cardCountry;
		}

		/**
		 * @return the cardState
		 */
		public String getCardState() {
			return cardState;
		}

		/**
		 * @param cardState the cardState to set
		 */
		public void setCardState(String cardState) {
			this.cardState = cardState;
		}

		/**
		 * @return the cardCity
		 */
		public String getCardCity() {
			return cardCity;
		}

		/**
		 * @param cardCity the cardCity to set
		 */
		public void setCardCity(String cardCity) {
			this.cardCity = cardCity;
		}

		/**
		 * @return the cardZip
		 */
		public String getCardZip() {
			return cardZip;
		}

		/**
		 * @param cardZip the cardZip to set
		 */
		public void setCardZip(String cardZip) {
			this.cardZip = cardZip;
		}
		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		
		if(args.length < 2 ){
			System.out.println("Usage: [option] <CSV Zuora Account Data file> <CSV Credit Card Data file>");
			return;
		}
		
		//1) read in Data files 
		System.out.println("Reading Zuora Account Data file");
		readAccountDataFile(args[0]);
		
		//read in Credit Card Data
		System.out.println("Reading Credit Card Data file");
		readCreditCardDataFile(args[1]);
		
		//2) build lookup maps
		
		System.out.println("Building Zuora Account to token lookup");
		buildAcctLookUp();
		
		System.out.println("Building token to Credit Card Number lookup");
		buildCardNumLookUp(tokenColumn, cardNumColumn);
		
		//3) do lookup and build output 
		
		System.out.println("Building output file");
		writeOutput();
		
		System.out.println("Happily Ever After");
	}// end main
	
	
	private static void readAccountDataFile(String filename){
		try {
			zAcctReader = new CsvReader(filename);
			System.out.println("Headers Found in Lookup: ");
			if(zAcctReader.readHeaders()){
				String[] headers = zAcctReader.getHeaders();
				for(int i =0; i < headers.length; i++){
					System.out.println( i + ") " + headers[i]);
				}
			} else { System.out.println("Error Reading lookup headers"); return;}
			
			
			
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found while reading Zuora Account Data file");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			System.out.println("Error reading Zuora Account Data file");
			e.printStackTrace();
			return;
		} 
	}// end readAccountDataFile
	
	private static void readCreditCardDataFile(String fileName){
		
		Scanner in = new Scanner(System.in);
		
		try {
			creditCardDataReader = new CsvReader(fileName);
			if(creditCardDataReader.readHeaders()){
				System.out.println("Headers Found in Input: ");
				for(int i =0; i < creditCardDataReader.getHeaderCount(); i++){
					System.out.println( i + ") " + creditCardDataReader.getHeader(i));
				}
			} else { System.out.println("Error Reading input headers"); return;}
			
			//Get mapping columns
			
			//token
			System.out.println("Enter Column number of token values");
			tokenColumn = 11;//in.nextInt();
			System.out.println("DEBUG tokenColumn: " + tokenColumn);			
			
			//Card Number
			System.out.println("Enter Column of Credit Card Number values");
			cardNumColumn = 30;//in.nextInt();
			System.out.println("DEBUG cardNumColumn: " + cardNumColumn);
			
			
			//Exp Year
			System.out.println("Enter Column of Credit Card Expiration Year values");
			expirationYearColumn = 31;//in.nextInt();
			System.out.println("DEBUG expirationYearColumn: " + expirationYearColumn);
			
			//Exp Month
			System.out.println("Enter Column of Credit Card Expiration Month values");
			expirationMonthColumn = 31;//in.nextInt();
			System.out.println("DEBUG cardNumColumn: " + expirationMonthColumn);
			
			//Holder Name
			System.out.println("Enter Column of Credit Card Holder Name values");
			creditCardHolderNameColumn = 13;//in.nextInt();
			System.out.println("DEBUG cardNumColumn: " + creditCardHolderNameColumn);
			
			//Address 1
			System.out.println("Enter Column of Credit Card Address 1 values");
			creditCardAddress1Column = 19;//in.nextInt();
			System.out.println("DEBUG cardNumColumn: " + creditCardAddress1Column);
			
			//Address 2
			System.out.println("Enter Column of Credit Card Address 2 values");
			creditCardAddress2Column = 20;//in.nextInt();
			System.out.println("DEBUG cardNumColumn: " + creditCardAddress2Column);
			
			//Country
			System.out.println("Enter Column of Credit Card Country values");
			creditCardCountryColumn = 26;//in.nextInt();
			System.out.println("DEBUG cardNumColumn: " + creditCardCountryColumn);
			
			//State
			System.out.println("Enter Column of Credit Card State values");
			creditCardStateColumn = 24;//in.nextInt();
			System.out.println("DEBUG cardNumColumn: " + creditCardStateColumn);
			
			//City
			System.out.println("Enter Column of Credit Card City values");
			creditCardCityColumn = 22;//in.nextInt();
			System.out.println("DEBUG cardNumColumn: " + creditCardCityColumn);
			
			//Postal Code
			System.out.println("Enter Column of Credit Card Postal Code values");
			creditCardPostalCodeColumn = 25;//in.nextInt();
			System.out.println("DEBUG cardNumColumn: " + creditCardPostalCodeColumn);
			
			
			
			
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found while reading Credit Card Data file");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			System.out.println("Error reading Credit Card Data file");
			e.printStackTrace();
			return;
		}
		
		
	}// end readCreditCardFile
	
	
	/*
	 * This funtion build the token to Zuora Account Id look up map
	 * it assumes the file will be formated as follows:
	 *
	 * ZuoraAccountId, Token
	 *
	 */
	private static void buildAcctLookUp(){
		
		System.out.println("Assumed Headers: ZuoraAcctID, token");
		
		acctLookUp = new HashMap<String, String>();
		
		try {
			while (zAcctReader.readRecord()){
				
				String[] records = zAcctReader.getValues();
				
				for(int l=0; l <= records.length; l++){
					// put token, zAcctID
					acctLookUp.put(zAcctReader.get(1), zAcctReader.get(0));
					
				}
				
			}
		} catch (IOException e) {
			System.out.println("Error Building Account Lookup");
			e.printStackTrace();
		}

	
	} // end buildAcctLookUp
	
	private static void buildCardNumLookUp(int tokenColumn, int cardNumColumn){
	
		cardLookUp = new HashMap<String, CreditCardData>();
		
		
		try {
			while (creditCardDataReader.readRecord()){
				
				String[] records = creditCardDataReader.getValues();
				
				for(int l=0; l <= records.length; l++){
					
					// get Credit Card values
					
					//get token
					String token = creditCardDataReader.get(tokenColumn);
					
					//prefilter if the token is not in the Account Look up then skip
					if(acctLookUp.containsKey(token)){
						
						//get card number
						String cardNum = creditCardDataReader.get(cardNumColumn);
						
						//get Card Type
						String cardType = getCreditCardType(cardNum);
						//setCreditCardType(cardNum);
						
						//get expr year
						String exprYear = creditCardDataReader.get(expirationYearColumn);
						 
						//get expr month
						String exprMonth = creditCardDataReader.get(expirationMonthColumn);
						
						//get Card Holder Name
						String cardHolderName = creditCardDataReader.get(creditCardHolderNameColumn);
						
						//get Address 1
						String addr1 = creditCardDataReader.get(creditCardAddress1Column);
						
						//get Address 2
						String addr2 = creditCardDataReader.get(creditCardAddress2Column);
						
						//get Country
						String country = creditCardDataReader.get(creditCardCountryColumn);
						
						//get State
						String state = creditCardDataReader.get(creditCardStateColumn);
						
						//get City
						String city = creditCardDataReader.get(creditCardCityColumn);
						
						//get Zip
						String zip = creditCardDataReader.get(creditCardPostalCodeColumn);
						
						//add to lookup map
						cardLookUp.put(token , new CreditCardData(cardNum, cardType, exprYear, exprMonth, cardHolderName, addr1, addr2, country, state, city, zip));
					} //end if(acctLookUp.containsKey(token))
				}// end for loop
				
			} // end while credit card file
		} catch (IOException e) {
			System.out.println("Error Building CreditCard Lookup");
			e.printStackTrace();
		}
	
		
	} // end buildCardNumLookUp
	
	static private void writeOutput(){
	try {
		outputWriter = new CsvWriter(new FileWriter(outputFile, true), ',');
		
		/*/ write headers 
		outputWriter.write("Zuora Account ID");
		outputWriter.write("Credit Card Number");
		outputWriter.endRecord();
		*/
		printHeaders(outputWriter);
		//do lookups to get Account Id to Card Number mapping
	
		for(String token : acctLookUp.keySet()){
			
			System.out.println("DEBUG: token: " + token);
			
			//get zAcctID
			String zAcctID = acctLookUp.get(token);
	
			//get Card Data
			if(cardLookUp.containsKey(token)){
			CreditCardData cardData = cardLookUp.get(token);
			String cardType = cardData.getCardType();
			
			// write to output

			outputWriter.write("");
			outputWriter.write(zAcctID);
			outputWriter.write("CreditCard");
			outputWriter.write(cardType);
			outputWriter.write(cardData.getCardNumer());
			outputWriter.write(""); 
			outputWriter.write(cardData.getCardExprYear());
			outputWriter.write(cardData.getCardExprMonth());
			outputWriter.write(cardData.getCardHolderName());
			outputWriter.write(cardData.getCardAddr1());
			outputWriter.write(cardData.getCardAddr2());
			outputWriter.write(cardData.getCardCountry());
			outputWriter.write(cardData.getCardState());
			outputWriter.write(cardData.getCardCity());
			outputWriter.write(cardData.getCardZip());
			outputWriter.write("");
			outputWriter.write("");
			outputWriter.write("");
			outputWriter.write("");
			outputWriter.write("");
			outputWriter.write("");
			outputWriter.write("TRUE");
			outputWriter.write("");
			outputWriter.write("Active");
			outputWriter.write("");
			outputWriter.write("");
			outputWriter.endRecord();
			}

		}// end for token in zAcctLookUp
			
		outputWriter.close();
			
		} catch (IOException e) {
			System.out.println("Error Building Output file");
			e.printStackTrace();
		}
		
		return;
	} // end writeOutput
	
	private static void printHeaders(CsvWriter csvWriter){
		
		try{
			csvWriter.write("Id");
			csvWriter.write("Customer Account");
			csvWriter.write("Payment Method Type");
			csvWriter.write("Credit Card Type");
			csvWriter.write("Credit Card Number");
			csvWriter.write("Bank Identification Number");
			csvWriter.write("Expiration Year");
			csvWriter.write("Expiration Month");
			csvWriter.write("Credit Card Holder Name");
			csvWriter.write("Credit Card Address1");
			csvWriter.write("Credit Card Address2");
			csvWriter.write("Credit Card Country");
			csvWriter.write("Credit Card State");
			csvWriter.write("Credit Card City");
			csvWriter.write("Credit Card Postal Code");
			csvWriter.write("Last Transaction");
			csvWriter.write("Last Transaction Time");
			csvWriter.write("Created By");
			csvWriter.write("Updated By");
			csvWriter.write("Created On");
			csvWriter.write("Updated On");
			csvWriter.write("Is Default");
			csvWriter.write("Consecutive Failed Payments");
			csvWriter.write("Status");
			csvWriter.write("Email");
			csvWriter.write("Phone");
			csvWriter.endRecord();
			//csvWriter.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	} // end printHeaders
	
	
	private static String getCreditCardType(String creditCardNumber) {
		try {
		    if (creditCardNumber.charAt(0)=='4')
		        return "Visa";
		    else if (creditCardNumber.charAt(0)=='5')
		        return "MasterCard";
		    else if (creditCardNumber.charAt(0)=='6')
		        return "Discover";
		    else if (creditCardNumber.charAt(0)=='3')
		          return "AmericanExpress";
		    else
		        return "Unknown card type";
		} catch (Exception e) {
			//System.out.println("Error Looking up Card type for card Number: " + creditCardNumber);
			//e.printStackTrace();
			return  "ERROR";
		}
    } // end GetCreditCardType
	
} // CreditCardTokenLookUp 