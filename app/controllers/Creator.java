package controllers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import models.Demotivator;
import models.Domain;
import models.User;

import org.apache.commons.lang.StringUtils;

import play.data.validation.Required;
import play.i18n.Messages;
import play.mvc.Util;
import services.DemotivatorCreator;
import services.DemotivatorCreatorImpl;
import utils.SharedConstants;
import controllers.Secure.Security;

/**
 * Demotivator Creator controller .
 * Handles rendering add page and logic of creating a new Demotivator, 
 * including validation. 
 * @author vitaliikravets
 *
 */

public class Creator extends BaseController{
	
	private static final String REQUIRED = "validation.required";
	private static final String TEXT_CANT_MESSAGE_KEY = "text.cant.be.longer";
	private static final String TITLE_CANT_MESSAGE_KEY = "title.cant.be.longer";
	private static final String PLEASE_SELECT_MESSAGE_KEY = "please.select.an.image";
	private static final String BAD_FILE_MESSAGE_KEY = "bad.file.error";
	
	static DemotivatorCreator creator = new DemotivatorCreatorImpl();
	
	
	public static void add() throws Throwable{	
		String userName = Security.connected();		
		if(userName == null){
			Secure.login();
		}
		
		render();
	}
	
	public static void create(@Required String title, String text, File[] image, String mode) throws Throwable {
		Map<String, String> errors = new HashMap<String, String>();
		
		String userName = Security.connected();		
		if(userName == null){
			error(403, Messages.get(SharedConstants.PLEASE_LOGIN));
		}
		
		validateParameters(errors, title, text, image);
		if(!errors.keySet().isEmpty()){
			renderJSON(errors);
		}
		
		String domainStr = request.domain;
		
		String fileName = null;
		try {
			fileName = creator.createDemotivator(image[0], title, text, mode, domainStr);
		} catch (IOException e) {
			errors.put("image_error", Messages.get(BAD_FILE_MESSAGE_KEY));
			renderJSON(errors);
		}
		
		Map<String, String> success = new HashMap<String, String>(3);
		
		if("create".equals(mode)){
			Domain domain = Domain.getOrCreate(domainStr);
			
			User user = BaseSecurity.currentUser();	
			Demotivator demo = new Demotivator(title, text, fileName, user, domain);
			demo.save();
			
			success.put("id", demo.id.toString());
		}
		
		success.put("status", "success");
		success.put("fileName", "/image/thumb." + fileName);
		
		renderJSON(success);
	}

	@Util
	private static void validateParameters(Map<String, String> errors, String title, String text, File[] image) {
		if(StringUtils.isBlank(title)){
			errors.put("title_error", Messages.get(REQUIRED));
		} else if(title.length() > 30){
			errors.put("title_error", Messages.get(TITLE_CANT_MESSAGE_KEY));
		}
		
		if(text.length() > 80){
			errors.put("text_error", Messages.get(TEXT_CANT_MESSAGE_KEY));
		}
		
		if(image == null || image[0] == null){
			errors.put("image_error", Messages.get(PLEASE_SELECT_MESSAGE_KEY));
		}
	}
	
}
