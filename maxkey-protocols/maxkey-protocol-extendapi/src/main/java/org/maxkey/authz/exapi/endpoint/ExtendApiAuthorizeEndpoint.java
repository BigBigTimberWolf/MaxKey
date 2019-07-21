/**
 * 
 */
package org.maxkey.authz.exapi.endpoint;

import javax.servlet.http.HttpServletRequest;

import org.maxkey.authz.endpoint.AuthorizeBaseEndpoint;
import org.maxkey.authz.endpoint.adapter.AbstractAuthorizeAdapter;
import org.maxkey.constants.BOOLEAN;
import org.maxkey.domain.Accounts;
import org.maxkey.domain.apps.Applications;
import org.maxkey.util.Instance;
import org.maxkey.web.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Crystal.Sea
 *
 */
@Controller
public class ExtendApiAuthorizeEndpoint  extends AuthorizeBaseEndpoint{
	final static Logger _logger = LoggerFactory.getLogger(ExtendApiAuthorizeEndpoint.class);

	@RequestMapping("/authz/api/{id}")
	public ModelAndView authorize(HttpServletRequest request,@PathVariable("id") String id){
		
		Applications apps=getApplication(id);
		_logger.debug(""+apps);
		
		if(BOOLEAN.isTrue(apps.getIsAdapter())){
			Accounts appUser=getAppAccounts(apps);
			
			if(appUser	==	null){
				return generateInitCredentialModelAndView(id,"/authorize/api/"+id);
			}

			ModelAndView modelAndView=new ModelAndView();
			
			AbstractAuthorizeAdapter adapter =(AbstractAuthorizeAdapter)Instance.newInstance(apps.getAdapter());
			
			apps.setAppUser(appUser);
			
			modelAndView=adapter.authorize(
					WebContext.getUserInfo(), 
					apps, 
					appUser.getRelatedUsername()+"."+appUser.getRelatedPassword(), 
					modelAndView);
			return modelAndView;
		}else{
			String redirec_uri=getApplication(id).getLoginUrl();
			return WebContext.redirect(redirec_uri);
		}
		
	}
}