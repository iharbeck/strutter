/*
 * Copyright (c) 2006, Your Corporation. All Rights Reserved.
 */

package sample2;

import sample.object.CounterBean;
import struts2.easy.Action2Config;

import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.Preparable;
import com.opensymphony.xwork.ValidationAware;

/**
 * <code>HomeAction</code>
 *
 * @author Ingo Harbeck
 */ 

public class HomeAction extends ActionSupport implements Preparable , ValidationAware
{
	public static Action2Config struts = new Action2Config();
	
	static {
		struts.addForward("success", "/home.jsp");
		struts.addForward("input",   "/home.jsp");
		struts.addForward("error",   "/home.jsp");
//		struts.addInterseptor("prepare");
//		struts.addInterseptor("prepare");
//		struts.addInterseptor("prepare");
//		struts.addInterseptor("completeStack");
	}
	
	public void prepare() throws Exception {
		System.out.println("++ PREPARE ++");
	}

	
	/** Spring managed bean reference */
    private CounterBean counterBean;
    private String mypar;

    /**
     * IoC setter for the spring managed CounterBean.
     *
     * @param counterBean
     */
    public void setCounterBean(CounterBean counterBean) {
        this.counterBean = counterBean;
    }
    
	
	String firstname = "ingo";	
	String lastname = "harbeck";
	int number = 12;
	
	String checker="0";
	
	
    public void validate() {
		super.validate();
		
		System.out.println(getFieldErrors().size());		
	}
 
    public String execute() throws Exception {
    	System.out.println(checker);
    	System.out.println(number);
    	
    	System.out.println(counterBean);
    	System.out.println(mypar);
    	
    	return SUCCESS;
    }

    public String doIngo() throws Exception {
    	this.addActionError("ingo");
    	this.addFieldError("mynumber", "dd");
    	this.addFieldError("lastname", "last");
    	System.out.println("INGO");
        return SUCCESS;
    }

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}


	public String getChecker() {
		return checker;
	}

	public void setChecker(String checker) {
		this.checker = checker;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getMypar() {
		return mypar;
	}

	public void setMypar(String mypar) {
		this.mypar = mypar;
	}
}
