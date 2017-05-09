package com.example.eduardo.locmess;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by pedro_silva on 26/04/17.
 */

public class PinMessage implements Serializable {
    private static final long serialVersionUID = -7154677483023979314L;
    private SecureRandom random = new SecureRandom();
    private String _sender;
    private int _id;
    private String _location;
    private Date _startDate;
    private Date _endDate;
    private Date _pubDate;
    private String _phoneNumber;
    private String _email;
    private List<String> _whitelist = new LinkedList<>();
    private List<String> _blacklist = new LinkedList<>();
    private String _content;

    public void setSender(String name){_sender = name;}

    public String getSender() {return _sender;}

    public void generateId(){
        Random rand = new Random();
        _id = rand.nextInt(20);
    }

    public int getID(){return _id;}

    public void setLocation(String location){_location = location;}

    public String getLocation(){
        return _location;
    }

    public void setEmail(String email){
        _email = email;
    }

    public String getEmail(){
        return _email;
    }

    public void addToWhitelist(String constraint){
        _whitelist.add(constraint);
    }

    public void addToBlacklist(String constraint){
        _blacklist.add(constraint);
    }

    public List getWhiteList(){
        return _whitelist;
    }

    public List getBlackList(){
        return _blacklist;
    }

    public void setContent(String text){
        _content = text;
    }

    public String getContent(){
        return _content;
    }

    public void setPhoneNumber(String number){
        _phoneNumber = number;
    }

    public String getPhoneNumber(){
        return _phoneNumber;
    }

    public void setStartDate(Date d){
        _startDate = d;
    }

    public Date getStartDate(){
        return _startDate;
    }

    public void setEndDate(Date d){
        _endDate = d;
    }

    public Date getEndDate(){
        return _endDate;
    }

    public void setPublicationDate(Date d){
        _pubDate = d;
    }

    public Date getPublicationDate(){
        return _pubDate;
    }

}
