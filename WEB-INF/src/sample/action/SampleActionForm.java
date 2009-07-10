package sample.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.apache.struts.util.LabelValueBean;

import sample.object.Address;

public class SampleActionForm extends ActionForm 
{
	Address customer = new Address();
	
	//String [] anreden = new String[] { "Herr", "Frau", "Dr." };
	
	// Alternative
	LabelValueBean [] anreden = new LabelValueBean[] { 
			new LabelValueBean("Herr", "11"), 
			new LabelValueBean("Frau", "22"), 
			new LabelValueBean("Dr.", "33" )};
	
	
	String rposition;

	String[] tog = new String[10]; 
	
	String c1iso;
	String c2iso;
	String c3iso;
	String c4iso;
	
	String memo;
	
	FormFile file = null;
	String   filename = "";
	
	public void reset(ActionMapping arg0, HttpServletRequest arg1) 
	{
		super.reset(arg0, arg1);
		c1iso="";
		c2iso="";
		c3iso="";
		c4iso="";
		tog = new String[10]; 
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}


	public String getRposition() {
		return rposition;
	}

	public void setRposition(String rgolgo) {
		this.rposition = rgolgo;
	}


	public String getC1iso() {
		return c1iso;
	}

	public void setC1iso(String c1iso) {
		this.c1iso = c1iso;
	}

	public String getC2iso() {
		return c2iso;
	}

	public void setC2iso(String c2iso) {
		this.c2iso = c2iso;
	}

	public String getC3iso() {
		return c3iso;
	}

	public void setC3iso(String c3iso) {
		this.c3iso = c3iso;
	}

	public String getC4iso() {
		return c4iso;
	}

	public void setC4iso(String c4iso) {
		this.c4iso = c4iso;
	}


	public LabelValueBean[] getAnreden() {
		return anreden;
	}

	public void setAnreden(LabelValueBean[] anreden) {
		this.anreden = anreden;
	}

	public Address getCustomer() {
		return customer;
	}

	public void setCustomer(Address customer) {
		this.customer = customer;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String[] getTog() {
		return tog;
	}

	public void setTog(String[] tog) {
		this.tog = tog;
	}
}

