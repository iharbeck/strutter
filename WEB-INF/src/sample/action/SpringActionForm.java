package sample.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import sample.object.Address;

public class SpringActionForm extends ActionForm 
{
	Address customer = new Address();
	String memo;
	
	public void reset(ActionMapping arg0, HttpServletRequest arg1) 
	{
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}


	public Address getCustomer() {
		return customer;
	}

	public void setCustomer(Address customer) {
		this.customer = customer;
	}
}

