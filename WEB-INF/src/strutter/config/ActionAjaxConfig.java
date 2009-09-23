package strutter.config;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.struts.util.LabelValueBean;
import org.directwebremoting.Browser;
import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.ServerContext;
import org.directwebremoting.ServerContextFactory;
import org.directwebremoting.fluent.FluentConfigurator;
import org.directwebremoting.ui.browser.Document;
import org.directwebremoting.ui.browser.Window;
import org.directwebremoting.ui.dwr.Util;

import strutter.helper.ActionHelper;

public class ActionAjaxConfig extends FluentConfigurator {

	public void configure() {
		
		withCreatorType("strutter", StrutterCreator.class.getName());
		
		ArrayList list = ActionPlugin.getActionclasses();	
    	
    	for(int i=0; i < list.size(); i++)
    	{
    		String classname = (String)list.get(i); //"sample.DWRAction";

	    	withCreator("strutter", "DWRAction")
	    	  //.addParam("scope", "request")
	    	  .addParam("actionclass", classname)
	    	  .addParam("javascript", classname.substring(classname.lastIndexOf(".")+1))
	    	  ;

//	    	withCreator("new", "DWRAction")
//	    	  .addParam("scope", "request")
//	    	  .addParam("class", classname);
    	}
    	
    	//withConverterType("bean", LabelValueBean.class.getName());
    	withConverter("bean", LabelValueBean.class.getName());

    	
    	list = ActionPlugin.getDWRPOJOs();
    	
    	for(int i=0; i < list.size(); i++)
    	{
    		String classname = ((Class)list.get(i)).getName(); //"sample.DWRAction";

        	withConverter("bean", classname)
    			.addParam("javascript", classname.substring(classname.lastIndexOf(".")+1));

    	}

    	
    	//withConverterType("bean", "sample.dao.Address");
//    	withConverter("bean", "sample.dao.*")
//    		.addParam("javascript", "Employee");

    	
    	//withConverter("bean", "sample.dao.Address");
    	//withConverter("bean", ".*");


//		   withConverterType("dog", "com.yourcompany.beans.Dog");
//
//		   withCreatorType("ejb", "com.yourcompany.dwr.creator.EJBCreator");
//
//		   withCreator("new", "ApartmentDAO")
//		       .addParam("scope",  "session")
//		       .addParam("class", "com.yourcompany.dao.ApartmentDAO")
//		       .exclude("saveApartment")
//		       .withAuth("method", "role")
//		       ;
//
//		   withCreator("struts", "DogDAO")
//		       .addParam("class", "com.yourcompany.dao.DogDAO")
//		       .include("getDog")
//		       .include("getColor");
//		   
//		   withConverter("dog", "*.Dog")
//		       .addParam("name", "value");
		  
//		   withSignature()
//		       .addLine("import java.util.List;")
//		       .addLine("import com.example.Check;")
//		       .addLine("Check.setLotteryResults(List nos);");
		   
/*
   <script src='/[YOUR-WEBAPP]/dwr/interface/[YOUR-SCRIPT].js'></script>
   <script src='/[YOUR-WEBAPP]/dwr/engine.js'></script>

   dwr.engine.setActiveReverseAjax(true);

	   withCreator("new", "vorkleberaction")
	       .addParam("scope",  "application")
	       .addParam("class", "storck.action.VorkleberAction")
	       ;


   <allow>
    <create creator="new" javascript="vorkleberaction" scope="application">
      <param name="class" value="storck.action.VorkleberAction"/>
    </create>

	<convert converter="bean"  
            match="org.apache.struts.util.LabelValueBean" />  
    <!-- 
    <convert converter="bean" match="ingo.Pos"/>
    -->
  </allow> 
  
   

 		   
 */
		   

		   
	}

	public static void dosomething() {
		
		Window.alert("");
		Document.setTitle("");
		
//		WebContext wctx = WebContextFactory.get();
//		Util util = new Util(wctx.getScriptSession());
        
        Util.removeAllOptions("vorkleber.logofiles"); 
        Util.addOptions("vorkleber.logofiles", new String[] {"a", "b", "c"});
    
        
        
	    Browser.withAllSessions(new Runnable() 
	    {
            public void run()
            {
                 Collection<ScriptSession> sessions = Browser.getTargetSessions();
                 for (ScriptSession scriptSession : sessions)
                 {
                     scriptSession.addScript(new ScriptBuffer("alert('')"));
                 }
            }
        });
	}
	
	
	public void test() {
        
        //WebContext wctx = WebContextFactory.get();
        //ServerContext sctx = ServerContextFactory.get(wctx.getServletContext());

        ServerContext sctx = ServerContextFactory.get(ActionHelper.getContext());
        
        ScriptBuffer script = new ScriptBuffer();
        script.appendScript("updateCoordinate();");

        // Push change out to clients viewing the page
        Collection<ScriptSession> sessions = sctx.getAllScriptSessions();        
        
        for (ScriptSession session : sessions) {
          session.addScript(script);
          //System.out.println(session.getPage());
        }   
        
	}
	
}
